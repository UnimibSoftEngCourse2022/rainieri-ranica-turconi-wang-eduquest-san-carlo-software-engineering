import { OpenQuestionViewer } from "./open-question-viewer.js";

export class QuestionsViewer extends HTMLElement {
  connectedCallback() {
    this.authorId = this.getAttribute("authorId");
    this.role = this.getAttribute("role");
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
    const jwt = window.localStorage.getItem("token");

    let questionsEndpoint = "http://localhost:8080/api/quiz/question";
    if (this.authorId) {
        questionsEndpoint += `?authorId=${this.authorId}`
    }
    const response = await fetch(questionsEndpoint, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const questions = await response.json();
        this.showQuestions(questions)
    } else {
        this.questions.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot get questions, please try again
        </div>
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

        }

        questionsHTML += `
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">${question.text}</h5>
                ${difficultyBannerHTML} <br>
                Answers: ${answers} <br>
                <a href="#" class="btn btn-primary">Add to quiz</a>
            </div>
        </div>
        `
    })
    this.questions.innerHTML = questionsHTML
  }
}

customElements.define('questions-viewer', QuestionsViewer);