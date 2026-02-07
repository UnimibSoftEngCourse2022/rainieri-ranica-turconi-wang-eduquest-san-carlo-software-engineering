import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { Quiz } from "./quiz-item.js"
import { Alert } from "./shared/alert.js";


export class QuizzesViewer extends BaseComponent {
  setupComponent() {
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');
    this.quizService = new QuizService();

    this.render();
    this.loadData();
  }

  attachEventListeners() {
    document.addEventListener("quiz-created", () => {
      this.loadData();
    });
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
      let quizzes = []
      if (this.role == "STUDENT") {
        quizzes = await this.quizService.getQuizzes();
      } else if (this.role == "TEACHER") {
        quizzes = await this.quizService.getQuizzesByAuthorId(this.userId);
      }

      if (quizzes.length == 0) {
          this.innerHTML = `<alert-component type="warning" message="There is not quiz to display"></alert-component>`
      } else {
          this.innerHTML = `<div class="row g-4" id="quizzes-container"></div>`;
          const quizzesContainer = this.querySelector("#quizzes-container");
          quizzes.forEach(quiz => {
            const quizItem = document.createElement("quiz-item");
            quizItem.classList.add("col-12", "col-md-6", "col-lg-4");

            quizItem.quizData = quiz;
            quizItem.role = this.role;
            quizItem.userId = this.userId;

            quizzesContainer.appendChild(quizItem);
          })
      }
    } catch (e) {
      console.log(e);
        this.innerHTML = `<alert-component type="danger" message="Cannot get the quizzes list, please try again later"></alert-component>`
    }
  }
}

customElements.define('quizzes-viewer', QuizzesViewer);