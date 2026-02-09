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

    this.quizId = localStorage.getItem("currentQuizId") || sessionStorage.getItem("currentQuizId");
    this.testId = localStorage.getItem("currentTestId") || sessionStorage.getItem("currentTestId");
    
    let rawId = this.getAttribute("student-id");
    if (!rawId) {
        const userJson = localStorage.getItem("user"); 
        if (userJson) {
            try { rawId = JSON.parse(userJson).id; } catch (e) { console.error(e); }
        }
    }
    this.studentId = rawId ? Number.parseInt(rawId) : null; 

    this.render();
    this.initQuiz();
  }
  
  get questionRunner() { return this.querySelector("question-runner"); }
  get quizHeader() { return this.querySelector("#quiz-title"); }
  get quizDate() { return this.querySelector("#quiz-date"); }
  get quizTimer() { return this.querySelector("#quiz-timer"); }
  get quizError() { return this.querySelector("#quiz-error"); }
  get questionsContainer() { return document.getElementById("questions-container"); }
  get questionNumber() { return this.querySelector("#question-number"); }
  get nextQuestionButton() { return this.querySelector("#next-question-button"); }
  get previousQuestionButton() { return this.querySelector("#previous-question-button"); }
  get completeQuizButton() { return this.querySelector("#complete-quiz-button"); }

  render() {
    this.innerHTML = `
    <div class="card text-center container mt-4">
      <div class="card-header">
          <h4 id="quiz-title" class="m-0">Loading...</h4>
          <div id="quiz-date" class="small text-muted"></div>
      </div>
      
      <div class="card-body">
          <div id="quiz-timer" class="fw-bold fs-4 mb-3" style="display:none;"></div>
          
          <div id="questions-container">
              <div id="questions-viewer"></div>
              <question-runner></question-runner>
          </div>
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
        this.updateQuestionViewer();
      }
    });

    this.addEventListenerWithTracking("#previous-question-button", "click", async () => {
      const ok = await this.handleSaveAnswerToCurrentQuestion();
      if (ok) {
        this.currentQuestionIndex--;
        this.updateQuestionViewer();
      }
    })

    this.addEventListenerWithTracking("#complete-quiz-button", "click", () => {
      this.handleCompleteQuiz();
    })
  }

  async initQuiz() {
      if (!this.quizId || !this.studentId) {
          this.quizError.innerHTML = `<alert-component type="danger" message="Error: Missing Data."></alert-component>`;
          return;
      }

      try {
          const sessionData = await this.attemptsService.addAttempt(this.quizId, this.studentId, this.testId);
          if (!sessionData) throw new Error("Could not start session.");

          this.sessionData = sessionData;
          this.quizAttemptId = sessionData.attemptId || sessionData.id;
          
          if (!this.quizAttemptId) throw new Error("No attempt ID.");
          
          await this.loadData();
          
          const btn = this.querySelector("#next-question-button");
          if(btn) btn.disabled = false;

      } catch (e) {
          console.error(e);
          const errorMsg = e.message || "";
          if (errorMsg.toLowerCase().includes("attempts")) {
              await this.showMaxAttemptsView(errorMsg); 
              return;
          }
          this.quizError.innerHTML = `<alert-component type="danger" message="${errorMsg || "Error starting quiz"}"></alert-component>`;
      }
  }

  async loadData() {
    this.attemptData = await this.attemptsService.getAttemptById(this.quizAttemptId);
    
    if (!this.attemptData) {
      this.quizError.innerHTML = "Failed to load attempt data.";
      return;
    }

    this.quizQuestions = this.sessionData.questions;
    this.currentQuestionIndex = 0;

    this.renderHeader();
    this.updateQuestionViewer();
    
    await this.setupTimer();
  }

  async setupTimer() {
    if (!this.quizTimer) return;

    let maxDuration = 0;

    // 1. Chiamata API esplicita per ottenere la durata corretta
    if (this.testId && this.testId !== "null" && this.testId !== "undefined") {
        try {
            const test = await this.testsService.getTestById(this.testId);
            if (test && test.maxDuration) {
                maxDuration = test.maxDuration;
            }
        } catch (e) { console.error("Error fetching test details", e); }
    }

    // 2. Fallback
    if (!maxDuration) {
        if (this.attemptData.maxDuration) maxDuration = this.attemptData.maxDuration;
        else if (this.attemptData.test && this.attemptData.test.maxDuration) maxDuration = this.attemptData.test.maxDuration;
    }

    this.quizTimer.style.display = 'block';

    if (maxDuration > 0) {
        const startTime = new Date(this.attemptData.startedAt).getTime();
        const endTime = startTime + (maxDuration * 60 * 1000);
        
        if (Date.now() >= endTime) {
            this.handleTimeExpired(true);
            return;
        }

        this.updateTimerDisplay(endTime, maxDuration);

        this.timerInterval = setInterval(() => {
            const now = Date.now();
            const distance = endTime - now;

            if (distance < 0) {
                clearInterval(this.timerInterval);
                this.quizTimer.innerHTML = "TEMPO SCADUTO";
                this.handleTimeExpired(); 
            } else {
                this.updateTimerDisplay(endTime, maxDuration);
            }
        }, 1000);
    } 
  }

  updateTimerDisplay(endTime, totalMinutes) {
     if(!this.quizTimer) return;
     const now = Date.now();
     const distance = endTime - now;
     
     if (distance > 0) {
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        const m = minutes < 10 ? "0" + minutes : minutes;
        const s = seconds < 10 ? "0" + seconds : seconds;
        
        const colorClass = minutes < 1 ? "text-danger" : "text-dark";
        this.quizTimer.innerHTML = `<span class="${colorClass}">Tempo: ${m}:${s} / ${totalMinutes} min</span>`;
     }
  }

  async handleTimeExpired(immediate = false) {
      if (this.timerInterval) clearInterval(this.timerInterval);
      
      this.quizError.innerHTML = `<alert-component type="danger" message="Tempo scaduto!"></alert-component>`;
      
      if (this.questionsContainer) {
          this.questionsContainer.innerHTML = `
            <div class="alert alert-warning p-4 mt-3">
                <h4>Tempo Esaurito</h4>
                <p>Sto inviando i risultati...</p>
            </div>
          `;
      }
      
      const btns = this.querySelectorAll("button");
      btns.forEach(b => b.disabled = true);

      if (!immediate) {
          try { await this.handleSaveAnswerToCurrentQuestion(); } catch(e) {}
      }
      
      setTimeout(() => this.handleCompleteQuiz(), 1500);
  }

  disconnectedCallback() {
      if (this.timerInterval) clearInterval(this.timerInterval);
      super.disconnectedCallback();
  }

  renderHeader() {
    if (!this.attemptData) return;
    let dateStr = "";
    try {
        dateStr = new Date(this.attemptData.startedAt).toLocaleString();
    } catch(e) { dateStr = this.attemptData.startedAt; }
    this.quizHeader.textContent = this.attemptData.quizTitle || "Quiz";
    this.quizDate.textContent = `Inizio: ${dateStr}`;
  }

  async updateQuestionViewer() {
    if (!this.quizQuestions || this.quizQuestions.length === 0) return;
    
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    this.questionRunner.question = currentQuestion;
    this.questionRunner.answer = null;
    
    if (this.sessionData && this.sessionData.existingAnswers) {
        const found = this.sessionData.existingAnswers.find(a => a.questionId == currentQuestion.id);
        if (found) {
             if (found.questionType == "OPENED") this.questionRunner.answer = found.textOpenAnswer;
             else if (found.questionType == "CLOSED") this.questionRunner.answer = found.selectedOptionId;
        }
    }
    this.questionRunner.render();

    this.questionNumber.innerHTML = `${this.currentQuestionIndex + 1} / ${this.quizQuestions.length}`;
    this.previousQuestionButton.disabled = (this.currentQuestionIndex == 0);
    this.nextQuestionButton.disabled = (this.currentQuestionIndex == this.quizQuestions.length - 1);
    this.completeQuizButton.disabled = (this.currentQuestionIndex < this.quizQuestions.length - 1);

    if (this.currentQuestionIndex == this.quizQuestions.length - 1) {
        this.nextQuestionButton.style.display = 'none';
        this.completeQuizButton.style.display = 'inline-block';
    } else {
        this.nextQuestionButton.style.display = 'inline-block';
        this.completeQuizButton.style.display = 'none';
    }
  }

  async handleSaveAnswerToCurrentQuestion() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    const answer = this.questionRunner.answer;

    if (!answer) return true;

    const requestBody = { 
        questionId: currentQuestion.id,
        textOpenAnswer: currentQuestion.questionType === "OPENED" ? answer : null,
        selectedOptionId: currentQuestion.questionType === "CLOSED" ? answer : null
    };

    try {
        await this.attemptsService.saveAttemptAnswer(this.quizAttemptId, requestBody);
        
        if (this.sessionData && this.sessionData.existingAnswers) {
             this.sessionData.existingAnswers = this.sessionData.existingAnswers.filter(a => a.questionId != currentQuestion.id);
             this.sessionData.existingAnswers.push({...requestBody, questionType: currentQuestion.questionType});
        }
        
        this.quizError.innerHTML = "";
        return true;
    } catch(e) {
        return false;
    }
  }

  async handleCompleteQuiz() {
    if (this.timerInterval) clearInterval(this.timerInterval);
    const btn = this.querySelector("#complete-quiz-button");
    if (btn) { btn.disabled = true; btn.innerHTML = "Processing..."; }

    if (this.questionsContainer && this.questionsContainer.innerHTML !== "") {
        if (this.currentQuestionIndex == this.quizQuestions.length - 1) {
            try { await this.handleSaveAnswerToCurrentQuestion(); } catch(e) {}
        }
    }

    try {
        await this.attemptsService.completeAttemptAnswer(this.quizAttemptId);
        window.location.href = window.location.pathname; 
    } catch(e) {
        if (btn) { btn.disabled = false; btn.innerHTML = "Complete Quiz"; }
        this.quizError.innerHTML = `<alert-component type="danger" message="Errore completamento."></alert-component>`;
    }
  }

  async showMaxAttemptsView(customMessage="Max attempts reached.") { 
    this.innerHTML = `
        <div class="container text-center mt-5">
            <div class="alert alert-danger p-4 shadow mx-auto" style="max-width: 600px;">
                <h3>Accesso Negato</h3>
                <p>${customMessage}</p>
                 <button class="btn btn-secondary mt-3" onclick="globalThis.location.href = globalThis.location.pathname">Home</button>
            </div>
        </div>
    `;
  }
}

customElements.define('quiz-runner', QuizRunner);