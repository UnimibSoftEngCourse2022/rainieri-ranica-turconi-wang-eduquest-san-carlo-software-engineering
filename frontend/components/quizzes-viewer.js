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
        const quizzes = await this.quizService.getQuizzes();
        if (quizzes.length == 0) {
            this.innerHTML = `<alert-component type="warning" message="There is not quiz to display"></alert-component>`
        } else {
            this.innerHTML = ``;
            quizzes.forEach(quiz => {
              const quizItem = document.createElement("quiz-item");

              quizItem.quizData = quiz;
              quizItem.role = this.role;
              quizItem.userId = this.userId;

              this.appendChild(quizItem);
            })
        }
    } catch (e) {
      console.log(e);
        this.innerHTML = `<alert-component type="danger" message="Cannot get the quizzes list, please try again later"></alert-component>`
    }
  }
}

customElements.define('quizzes-viewer', QuizzesViewer);