import { callApi, endpoints } from "../js/api.js";
import { QuizService } from "../services/quiz-service.js";
import { Quiz } from "./quiz-item.js"

export class QuizzesViewer extends HTMLElement {
  async connectedCallback() {
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');
    this.quizService = new QuizService();

    this.render();
    this.loadData();

    document.addEventListener("quiz-created", () => {
        this.loadData();
    })
  }

  render() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>
    `;
  }

  async loadData() {
    this.render();
    try {
        const quizzes = await this.quizService.getQuizzes();
        if (quizzes.length == 0) {
            let message = "";
            if (this.role == "STUDENT") {
                message = "There isn't any quiz yet! Wait until your teacher will create one!"
            } else if (this.role == "TEACHER") {
                message = "You don't have any quiz yet! Create one to start!";
            }
            this.innerHTML = `
            <div class="alert alert-warning" role="alert">
                ${message}
            </div>
            `
        } else {
            let quizzesHTML = ''
            quizzes.forEach(quiz => {
                quizzesHTML += `
                <quiz-item id="${quiz.id}" title="${quiz.title}" description="${quiz.description}" role="${this.role}" user-id="${this.userId}"></quiz-item>
                `
            });
            this.innerHTML = quizzesHTML;
        }
    } catch (e) {
        console.log(e);
        this.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot get the quizzes list, please try again later
        </div>
        `
    }
  }
}
customElements.define('quizzes-viewer', QuizzesViewer);