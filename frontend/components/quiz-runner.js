import { AttemptsService } from "../services/attempts-service.js";
import { QuizService } from "../services/quiz-service.js";
import { TestsService } from "../services/tests-service.js";
import { BaseComponent } from "./base-component.js";
import "./question-runner.js";
import "./shared/alert.js";

export class QuizRunner extends BaseComponent {
  setupComponent() {
    this.sessionData = null;
    this.quizAttemptData = null;
    this.quizQuestions = [];
    this.currentQuestionIndex = -1;
    this.timerInterval = null;

    this.attemptsService = new AttemptsService();
    this.quizService = new QuizService();
    this.testsService = new TestsService();

    const urlParams = new URLSearchParams(window.location.search);
    this.quizId = urlParams.get('quizId');
    this.testId = urlParams.get('testId'); 
    
    let rawId = this.getAttribute("student-id");

    if (!rawId) {
        const userJson = localStorage.getItem("user"); 
        if (userJson) {
            try {
                const userObj = JSON.parse(userJson);
                rawId = userObj.id;
            } catch (e) {
                console.error("Error parsing user from localStorage", e);
            }
        }
    }

    this.studentId = rawId ? Number.parseInt(rawId) : null; 

    this.render();
    this.initQuiz();
  }
  
  get questionRunner() { return this.querySelector("question-runner"); }
  get quizErrorSpace() { return this.querySelector("quiz-error"); }
  get quizHeader() { return this.querySelector("#quiz-title"); }
  get quizTimer() { return this.querySelector("#quiz-timer"); }
  get quizError() { return this.querySelector("#quiz-error"); }
  get questionsViewer() { return document.getElementById("questions-viewer"); }
  get questionNumber() { return this.querySelector("#question-number"); }
  get nextQuestionButton() { return this.querySelector("#next-question-button"); }
  get previousQuestionButton() { return this.querySelector("#previous-question-button"); }
  get completeQuizButton() { return this.querySelector("#complete-quiz-button"); }

  render() {
    this.innerHTML = `
    <div class="card text-center container mt-4">
      <div class="card-header d-flex justify-content-between align-items-center">
          <div id="quiz-title">Loading...</div>
          <div id="quiz-timer" class="fw-bold text-danger"></div>
      </div>
      
      <div class="card-body">
          <div id="questions-viewer"></div>
          <question-runner></question-runner>
      </div>
      
      <div class="card-footer">
          <p id="question-number"></p>
          <button class="btn btn-primary" id="previous-question-button" disabled>Previous Question</button>
          <button class="btn btn-primary" id="next-question-button" disabled>Next Question</button>
          <hr>
          <button class="btn btn-success" id="complete-quiz-button" disabled>Complete Quiz</button>
          <div id="quiz-error" class="mt-2"></div>
      </div>
    </div>
    `;
  }  

  attachEventListeners() {
    this.addEventListenerWithTracking("#next-question-button", "click", async () => {
      const ok = await this.handleSaveAnswerToCurrentQuestion();
      if (ok) {
        this.currentQuestionIndex++;
        this.currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
        this.updateQuestionViewer();
      }
    });

    this.addEventListenerWithTracking("#previous-question-button", "click", async () => {
      const ok = await this.handleSaveAnswerToCurrentQuestion();
      if (ok) {
        this.currentQuestionIndex--;
        this.currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
        this.updateQuestionViewer();
      }
    })

    this.addEventListenerWithTracking("#complete-quiz-button", "click", () => {
      this.handleCompleteQuiz();
    })
  }

  async initQuiz() {
      if (!this.quizId || !this.studentId) {
          this.quizError.innerHTML = `<alert-component type="danger" message="Missing Quiz ID or User not logged in."></alert-component>`;
          return;
      }

      try {
          const sessionData = await this.attemptsService.addAttempt(this.quizId, this.studentId, this.testId);
          
          if (!sessionData) {
              throw new Error("Could not start quiz session.");
          }

          this.sessionData = sessionData;
          this.quizAttemptId = sessionData.attemptId || sessionData.id;
          
          if (!this.quizAttemptId) {
             throw new Error("No attempt ID returned from server.");
          }
          
          await this.loadData();

          const btn = this.querySelector("#next-question-button");
          if(btn) btn.disabled = false;

      } catch (e) {
          console.error(e);
          const errorMsg = e.message || "";
          
          if (errorMsg.toLowerCase().includes("attempts") || errorMsg.toLowerCase().includes("tentativi") || errorMsg.toLowerCase().includes("tries")) {
              await this.showMaxAttemptsView(errorMsg); 
              return;
          }
          
          this.quizError.innerHTML = `<alert-component type="danger" message="${errorMsg || "Error starting quiz"}"></alert-component>`;
      }
  }

  async loadData() {
    this.attemptData = await this.attemptsService.getAttemptById(this.quizAttemptId);
    
    if (!this.attemptData) {
      this.quizError.innerHTML = "Failed to load quiz attempt data.";
      return;
    }

    this.quizQuestions = this.sessionData.questions;
    this.currentQuestionIndex = 0;

    this.renderHeader();
    this.updateQuestionViewer();

    if (this.testId) {
        await this.setupTimer();
    }
  }

  async setupTimer() {
    try {
        const test = await this.testsService.getTestById(this.testId);
        
        if (test && test.maxDuration > 0) {
            const maxDurationMinutes = test.maxDuration;
            const startTime = new Date(this.attemptData.startedAt).getTime();
            const endTime = startTime + (maxDurationMinutes * 60 * 1000);
            
            this.updateTimerDisplay(endTime);

            this.timerInterval = setInterval(() => {
                const now = Date.now();
                const distance = endTime - now;

                if (distance < 0) {
                    clearInterval(this.timerInterval);
                    this.quizTimer.innerHTML = "TIME EXPIRED";
                    this.handleTimeExpired(); 
                } else {
                    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                    const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                    this.quizTimer.innerHTML = `Time Left: ${minutes}m ${seconds}s`;
                }
            }, 1000);
        }
    } catch (e) {
        console.error("Error setting up timer", e);
    }
  }

  updateTimerDisplay(endTime) {
     const now = Date.now();
     const distance = endTime - now;
     if (distance > 0) {
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        this.quizTimer.innerHTML = `Time Left: ${minutes}m ${seconds}s`;
     }
  }

  async handleTimeExpired() {
      this.quizError.innerHTML = `<alert-component type="danger" message="Time is up! Submitting quiz..."></alert-component>`;
      const btn = this.querySelector("#next-question-button");
      if(btn) btn.disabled = true;
      
      setTimeout(() => this.handleCompleteQuiz(), 2000);
  }

  disconnectedCallback() {
      if (this.timerInterval) {
          clearInterval(this.timerInterval);
      }
      super.disconnectedCallback();
  }

  renderHeader() {
    if (!this.attemptData) return;
    const [startDate, completeStartTime] = this.attemptData.startedAt.split("T")
    const [startTime, _] = completeStartTime.split(".")
    this.quizHeader.innerHTML = `<h4>${this.attemptData.quizTitle}</h4>Started at: ${startTime}, ${startDate}`
  }

  async updateQuestionViewer() {
    if (!this.quizQuestions || this.quizQuestions.length === 0) return;
    
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    this.questionRunner.question = currentQuestion;

    this.questionRunner.answer = null;
    
    const sessionData = await this.attemptsService.addAttempt(this.quizId, this.studentId, this.testId);
    if (sessionData?.existingAnswers) {
        sessionData.existingAnswers.forEach(answer => {
          if (answer.questionId == currentQuestion.id) {
            let answerValue = ``;
            if (answer.questionType == "OPENED") {
              answerValue = answer.textOpenAnswer;
            } else if (answer.questionType == "CLOSED") {
              answerValue = answer.selectedOptionId;
            }
            this.questionRunner.answer = answerValue;
          }
        });
    }
    this.questionRunner.render();

    this.questionNumber.innerHTML = (this.currentQuestionIndex + 1) + "/" + this.quizQuestions.length;
    this.previousQuestionButton.disabled = (this.currentQuestionIndex == 0);
    this.nextQuestionButton.disabled = (this.currentQuestionIndex == this.quizQuestions.length - 1);
    this.completeQuizButton.disabled = (this.currentQuestionIndex < this.quizQuestions.length - 1);
  }

  async handleSaveAnswerToCurrentQuestion() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    
    const requestBody = {
      questionId: currentQuestion.id
    }
    
    const answer = this.questionRunner.answer;

    if (!answer) {
      return true;
    }

    if (answer == null || answer == undefined || answer === "") {
      this.quizError.innerHTML = `
      <alert-component type="warning" message="Please provide an answer before proceeding."></alert-component>
      `
      return false;
    }

    const currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
    if (currentQuestionType === "OPENED") {
      requestBody.textOpenAnswer = answer;
    } else if (currentQuestionType === "CLOSED") {
      requestBody.selectedOptionId = answer;
    }

    const response = await this.attemptsService.saveAttemptAnswer(this.quizAttemptId, requestBody);
    if (response) {
      this.quizError.innerHTML = "";
      return true;
    } else {
      this.quizError.innerHTML = `
      <alert-component type="danger" message="Error sending your answer, please try again later"></alert-component>
      `;
      return false;
    }
  }

  async handleCompleteQuiz() {
    if (this.timerInterval) clearInterval(this.timerInterval);

    const btn = this.querySelector("#next-question-button");
    if (btn) {
      btn.disabled = true;
      btn.innerHTML = "Processing...";
    }

    // First I save the answer to the last question
    if (this.currentQuestionIndex == this.quizQuestions.length - 1) {
      await this.handleSaveAnswerToCurrentQuestion();
    }

    try {
      const response = await this.attemptsService.completeAttemptAnswer(this.quizAttemptId);
      if (response) {
        window.location.href = window.location.pathname;
      } else {
        throw new Error("Submission failed");
      }
    } catch (e) {
      if (btn) {
        btn.disabled = false;
        btn.innerHTML = "Finish Quiz";
      }
      this.quizError.innerHTML = `
      <alert-component type="danger" message="Error completing the quiz. Please try again."></alert-component>
      `;
    }
  }

  async showMaxAttemptsView(customMessage) { 
    const displayMessage = customMessage || "Max attempts reached for this test.";

    this.innerHTML = `
        <div class="container text-center mt-5">
            <div class="alert alert-danger p-4 shadow mx-auto" style="max-width: 600px; border-left: 5px solid #dc3545;" role="alert">
                <h3 class="alert-heading mb-3 fw-bold">Access Denied</h3>
                <p class="mb-0 fs-5">${displayMessage}</p>
            </div>
        </div>
    `;
  }
}

customElements.define('quiz-runner', QuizRunner);