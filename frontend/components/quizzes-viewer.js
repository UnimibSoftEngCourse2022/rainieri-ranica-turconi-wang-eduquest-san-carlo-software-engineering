import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { Quiz } from "./quiz-item.js"
import { Alert } from "./shared/alert.js";


export class QuizzesViewer extends BaseComponent {
  setupComponent() {
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');
    this.quizService = new QuizService();
    this.allQuizzes = [];

    this.render();
    this.loadData();
  }

  attachEventListeners() {
    document.addEventListener("quiz-created", () => {
      this.loadData();
    });

    const searchInput = this.querySelector("#search-input");
    if (searchInput) {
        searchInput.addEventListener("input", (e) => {
            const searchTerm = e.target.value.toLowerCase();
            const filteredQuizzes = this.allQuizzes.filter(quiz => 
                quiz.title.toLowerCase().includes(searchTerm)
            );
            this.displayQuizzes(filteredQuizzes);
        });
    }
  }

  render() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <div class="row justify-content-center mb-4">
            <div class="col-md-6">
                <input type="text" id="search-input" class="form-control" placeholder="Quiz Title">
            </div>
        </div>
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
        <div id="message-container"></div>
        <div class="row g-4 justify-content-center" id="quizzes-container"></div>
    </div>
    `;
  }

  async loadData() {
    const loader = this.querySelector(".spinner-border");
    const messageContainer = this.querySelector("#message-container");
    try {
      let quizzes = []
      if (this.role == "STUDENT") {
        quizzes = await this.quizService.getQuizzes();
      } else if (this.role == "TEACHER") {
        quizzes = await this.quizService.getQuizzesByAuthorId(this.userId);
      }
      
      this.allQuizzes = quizzes;

      if (loader) {
        loader.style.display = "none";
      }

      if (quizzes.length == 0) {
          messageContainer.innerHTML = `<alert-component type="warning" message="There is not quiz to display"></alert-component>`
      } else {
          /*this.innerHTML = `<div class="row g-4" id="quizzes-container"></div>`;
          const quizzesContainer = this.querySelector("#quizzes-container");
          quizzes.forEach(quiz => {
            const quizItem = document.createElement("quiz-item");
            quizItem.classList.add("col-12", "col-md-6", "col-lg-4");

            quizItem.quizData = quiz;
            quizItem.role = this.role;
            quizItem.userId = this.userId;

            quizzesContainer.appendChild(quizItem);
          })*/
         this.displayQuizzes(this.allQuizzes);
      }
    } catch (e) {
      console.log(e);
        this.innerHTML = `<alert-component type="danger" message="Cannot get the quizzes list, please try again later"></alert-component>`
    }
  }

  displayQuizzes(quizzesList) {
      const container = this.querySelector("#quizzes-container");
      const messageContainer = this.querySelector("#message-container");
      container.innerHTML = "";
      messageContainer.innerHTML = "";

      if (quizzesList.length === 0) {
          messageContainer.innerHTML = `<div class="alert alert-light">No quizzes found matching your search.</div>`;
          return;
      }
      quizzesList.forEach(quiz => {
          const quizItem = document.createElement("quiz-item");
          quizItem.classList.add("col-12", "col-md-6", "col-lg-4");
          quizItem.quizData = quiz;
          quizItem.role = this.role;
          quizItem.userId = this.userId;
          container.appendChild(quizItem);
      });
  }
}

customElements.define('quizzes-viewer', QuizzesViewer);