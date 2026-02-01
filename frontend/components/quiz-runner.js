import { AttemptsService } from "../services/attempts-service.js";
import { QuizService } from "../services/quiz-service.js";
import { Alert } from "./shared/alert.js";

export class QuizRunner extends HTMLElement {
  connectedCallback() {
    this.quizAttemptId = this.getAttribute("quiz-attempt-id");
    this.attemptsService = new AttemptsService();
    this.quizService = new QuizService();

    this.render();
    this.loadData();

    this.quizQuestions = [];
    this.currentQuestionIndex = -1;
    this.currentQuestionType;
  }

  get upperSpace() {
    return this.querySelector("#quiz-title");
  }

  get quizErrorSpace() {
    return this.querySelector("#quiz-error");
  }

  get questionsViewer() {
    return this.querySelector("#questions-viewer");
  }

  get saveAnswerButton() {
    return this.querySelector("#save-answer-button");
  }

  get openQuestionAnswer() {
    return this.querySelector("#open-question-answer");
  }

  get closedQuestionAnswer() {
    return this.querySelector('input[type="radio"]:checked');
  }

  render() {
    this.innerHTML = `
    <div class="card text-center">
        <div class="card-header" style="padding: 20px">
            <div id="quiz-title"></div>
        </div>
        <div class="card-body" id="questions-viewer">
        </div>
        <div id="quiz-error"></div>
    </div>
    `;
  }

  async loadData() {
    const attemptData = await this.attemptsService.getAttemptById(this.quizAttemptId);

    if (attemptData) {
        this.showAttemptData(attemptData);
    } else {
        this.quizErrorSpace.innerHTML = `
        <alert-component type="danger" message="Cannot load the test, please try again later"></alert-component>
        `
    }
  }

  async showAttemptData(attemptData) {
    const [startDate, completeStartTime] = attemptData.startedAt.split("T")
    const [startTime, _] = completeStartTime.split(".")
    this.upperSpace.innerHTML = `
    <h4>${attemptData.quizTitle}</h4>
    Started at: ${startTime}, ${startDate}
    `

    const quizData = await this.quizService.getQuizById(attemptData.quizId);
    if (quizData) {
      this.quizQuestions = quizData.questions;
      this.currentQuestionIndex = 0;
      this.currentQuestionType = quizData.questions[0].questionType;
      this.updateQuestionsViewer();
    } else {
      this.quizErrorSpace.innerHTML = `
      <alert-component type="danger" message="Cannot load the test, please try again later"></alert-component>
      `
    }
  }

  async updateQuestionsViewer() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    
    let questionsViewerHTML = `
    ${this.getQuestionHTML(currentQuestion)} <br>
    <p>Domanda ${this.currentQuestionIndex + 1}/${this.quizQuestions.length} <br>
    `
    
    this.questionsViewer.innerHTML = questionsViewerHTML;
    this.saveAnswerButton.addEventListener("click", () => { 
      this.handleSaveAnswerToCurrentQuestion();
    });
  }

  getQuestionHTML(question) {
    console.log(question);
    let html = ``;
    html += `<h1>${question.text}</h1>`;
    if (question.questionType == "OPENED") {
      html += `<input class="form-control" placeholder="Write here your answer..." id="open-question-answer"></input><br>`
    } else if (question.questionType == "CLOSED") {
      html += `<div class="input-group mb-3">`
      question.closedQuestionOptions.forEach(option => {
        html += `
        <div class="input-group-text">
            <input class="form-check-input mt-0" name="question-${question.id}" type="radio" value="${option.id}" id="closed-option-${option.id}">
        </div>
        <label class="form-control" for="closed-option-${option.id}">${option.text}</label>
        `
      })
      html += `</div>`
    }
    html += `<button class="btn btn-primary" id="save-answer-button">Save answer</button><br>`
    return html;
  }

  async handleSaveAnswerToCurrentQuestion() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];

    const requestBody = {
      questionId: currentQuestion.id
    }

    let answer;
    if (this.currentQuestionType == "OPENED") {
      answer = this.openQuestionAnswer.value;
      requestBody.openQuestionAnswer = answer;
    } else if (this.currentQuestionType == "CLOSED") {
      if (!this.closedQuestionAnswer) {
        return;
      }
      answer = parseInt(this.closedQuestionAnswer.value);
      requestBody.selectedOptionId = answer;
    }

    const response = await this.attemptsService.saveAttemptAnswer(this.quizAttemptId, requestBody);

    if (response) {
      if (this.currentQuestionIndex < this.quizQuestions.length - 1) {
        this.currentQuestionIndex++;
        this.currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
        this.updateQuestionsViewer();
      } else {
        this.handleCompleteQuiz();
      }
    } else {
      this.quizErrorSpace.innerHTML = `
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