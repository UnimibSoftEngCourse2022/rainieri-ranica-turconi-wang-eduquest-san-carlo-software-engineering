import { AttemptsService } from "../services/attempts-service.js";
import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { QuestionRunner } from "./question-runner.js";
import { Alert } from "./shared/alert.js";

export class QuizRunner extends BaseComponent {
  setupComponent() {
    this.quizAttemptId = this.getAttribute("quiz-attempt-id");
    this.attemptsService = new AttemptsService();
    this.quizService = new QuizService();

    this.render();
    this.loadData();

    this.quizQuestions = [];
    this.currentQuestionIndex = -1;
  }
  
  get questionRunner() { return this.querySelector("question-runner"); }
  get quizErrorSpace() { return this.querySelector("quiz-error"); }

  render() {
    this.innerHTML = `
    <div class="card text-center container">
    <div class="card-header" id="quiz-title"></div>
    <div class="card-body" id="questions-viewer"></div>
    <question-runner></question-runner>
    <button class="btn btn-primary" id="next-question-button">Next Question</button>
    <div id="quiz-error"></div>
    </div>
    `;
  }  

  attachEventListeners() {
    this.addEventListenerWithTracking("#next-question-button", "click", () => {
      this.handleSaveAnswerToCurrentQuestion();
    })
  }

  get quizHeader() { return this.querySelector("#quiz-title"); }
  get quizError() { return this.querySelector("#quiz-error"); }

  async loadData() {
    const attemptData = await this.attemptsService.getAttemptById(this.quizAttemptId);
    if (!attemptData) {
      this.quizError.innerHTML = "Failed to load quiz attempt data.";
      return;
    }

    const quizData = await this.quizService.getQuizById(attemptData.quizId);
    if (!quizData) {
      this.quizError.innerHTML = "Failed to load quiz data.";
      return;
    }

    this.quizQuestions = quizData.questions;
    this.currentQuestionIndex = 0;

    this.renderHeader(attemptData);
    this.updateQuestionViewer();
  }

  renderHeader(attemptData) {
    const [startDate, completeStartTime] = attemptData.startedAt.split("T")
    const [startTime, _] = completeStartTime.split(".")
    this.quizHeader.innerHTML = `<h4>${attemptData.quizTitle}</h4>Started at: ${startTime}, ${startDate}`
  }

  updateQuestionViewer() {
    this.questionRunner.question = this.quizQuestions[this.currentQuestionIndex];
    this.questionRunner.render();
  }

  async handleSaveAnswerToCurrentQuestion() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    
    const requestBody = {
      questionId: currentQuestion.id
    }
    
    const answer = this.questionRunner.answer;

    if (answer == null || answer == undefined || answer === "") {
      this.quizError.innerHTML = `
      <alert-component type="warning" message="Please provide an answer before proceeding."></alert-component>
      `
      return;
    }

    const currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
    if (currentQuestionType === "OPENED") {
      requestBody.openedAnswerText = answer;
    } else if (currentQuestionType === "CLOSED") {
      requestBody.selectedOptionId = answer;
    }

    const response = await this.attemptsService.saveAttemptAnswer(this.quizAttemptId, requestBody);

    if (response) {
      if (this.currentQuestionIndex < this.quizQuestions.length - 1) {
        this.currentQuestionIndex++;
        this.currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
        this.updateQuestionViewer();
      } else {
        this.handleCompleteQuiz();
      }
    } else {
      this.quizError.innerHTML = `
      <alert-component type="danger" message="Error sending your answer, please try again later"></alert-component>
      `
    }
  }

  async handleCompleteQuiz() {
    const response = await this.attemptsService.completeAttemptAnswer(this.quizAttemptId);
    if (response) {
      window.location = "../student-dashboard/";
      return;
    } else {
      this.quizErrorSpace.innerHTML = `
      <alert-component type="danger" message="Error trying to complete the quiz, please try again later"></alert-component>
      `
    }
  }
}

customElements.define('quiz-runner', QuizRunner);