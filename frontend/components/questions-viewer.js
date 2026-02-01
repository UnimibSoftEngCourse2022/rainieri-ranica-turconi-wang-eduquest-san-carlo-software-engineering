import { QuestionsService } from "../services/questions-service.js";
import { QuizService } from "../services/quiz-service.js";
import { Alert } from "./shared/alert.js";

export class QuestionsViewer extends HTMLElement {
  connectedCallback() {
    this.quizId = this.getAttribute("quizId");
    this.authorId = this.getAttribute("authorId");
    this.role = this.getAttribute("role");

    this.questionsService = new QuestionsService();
    this.quizService = new QuizService();
    this.render();
    this.loadData();
  }

  get questions() {
    return this.querySelector("#questions");
  }

  render() {
    this.innerHTML = `<div id="questions" class="container"></div>`;
  }

  async loadData() {
    let questions = null;
    if (this.authorId) {
      questions = await this.questionsService.getQuestionByAuthorId(this.authorId);
    } else {
      questions = await this.questionsService.getQuestions();
    }
    
    if (questions != null && questions != undefined) {
      this.showQuestions(questions)
    } else {
      this.questions.innerHTML = `
        <alert-component type="danger" message="Cannot get questions, please try again"></alert-component>
        `
    }
  }

  async showQuestions(questions) {
    let questionsHTML = '';
    questions.forEach(question => {
        let difficultyBannerHTML = `
        <span class="badge text-bg-secondary">${question.difficulty}</span></h6>
        `
        let answers = ''
        if (question.questionType == "OPENED") {
          answers = question.validAnswersOpenQuestion.join(",")
        } else if (question.questionType == "CLOSED") {
          answers = []
          question.closedQuestionOptions.forEach(option => answers.push(option.text))
        }

        questionsHTML += `
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">${question.text}</h5>
                ${difficultyBannerHTML} <br>
                Answers: ${answers} <br>
                ${this.role == "TEACHER" ? `<a href="#" class="btn btn-primary add-question-to-quiz-button" data-id="${question.id}">Add to quiz</a>` : ``}
                <div id="add-question-${question.id}-result"></div>
            </div>
        </div>
        `
    })
    this.questions.innerHTML = questionsHTML
    this.questions.querySelectorAll(".add-question-to-quiz-button").forEach(button => {
      button.addEventListener("click", (event) => {
        const questionId = event.target.getAttribute("data-id");
        this.addQuestionToQuiz(questionId);
      })
    })
  }

  async addQuestionToQuiz(questionId) {
    const response = await this.quizService.addQuestionToQuiz(this.quizId, questionId);
    if (response) {
      this.dispatchEvent(new CustomEvent("question-added-to-quiz", {
        bubbles: true,
        composed: true
      }));
    } else {
      const addQuestionResult = this.querySelector(`#add-question-${questionId}-result`);
      addQuestionResult.innerHTML = `
      <alert-component type="danger" message="Error adding question"></alert-component>
      `
    }
  }
}

customElements.define('questions-viewer', QuestionsViewer);