import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { QuestionsViewer } from "./questions-viewer.js";
import { Alert } from "./shared/alert.js";

export class QuizEditor extends BaseComponent {
  setupComponent() {
    this.quizId = this.getAttribute("id");
    this.quizService = new QuizService();

    this.render();
    this.loadData();
  }

  attachEventListeners() {
    this.addEventListener("click", (e) => {
        const btn = e.target.closest(".remove-question-from-quiz-button");
        if (btn) this.removeQuestionFromQuiz(btn.dataset.id);
    });
    this.addEventListener("question-added-to-quiz", () => this.loadData());
  }

  get quizTitleInput() { return this.querySelector("#title-input"); }

  get quizDescriptionInput() { return this.querySelector("#description-input"); }

  get quizQuestions() { return this.querySelector("#questions"); }

  render() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <h1>Quiz editor</h1>
        <form id="quiz-editor-general-info-form">
            <div class="mb-3">
                <label for="title-input" class="form-label">
                    Title
                </label>
                <input
                    type="text"
                    class="form-control"
                    id="title-input"
                />
            </div>
            <div class="mb-3">
                <label for="description-input" class="form-label">
                    Description
                </label>
                <input
                    type="text"
                    class="form-control"
                    id="description-input"
                />
            </div>
        </form>
        <div id="questions"></div>
        <hr>
        <h3>Add a question</h3>
        <questions-viewer role="TEACHER" quizId="${this.quizId}"></questions-viewer>
    </div>
    `;
  }

  async loadData() {
    const quizData = await this.quizService.getQuizById(this.quizId);
    if (quizData) {        
        this.quizTitleInput.value = quizData.title;
        this.quizDescriptionInput.value = quizData.description;
        this.showQuizQuestions(quizData.questions);
    } else {
        this.innerHTML = `<alert-component type="danger" message="Error trying to show the quiz, please try again later"></alert-component>`;
    }
  }

  showQuizQuestions(questions) {
    if (!questions.length) {
        this.quizQuestions.innerHTML = `<alert-component type="info" message="No questions added to the quiz yet"></alert-component>`;
        return;
    }

    const questionsHTML = questions.map(q => `
        <a class="list-group-item list-group">${q.text}<button class="btn remove-question-from-quiz-button" data-id="${q.id}">🗑️</button></a>
    `).join('');
    this.quizQuestions.innerHTML = `<h3>Quiz questions</h3><div class="list-group">${questionsHTML}</div>`;
  }

  async removeQuestionFromQuiz(questionId) {
    const response = await this.quizService.removeQuestionFromQuiz(this.quizId, questionId);

    if (response) {
        this.loadData();
    }
  }
}

customElements.define('quiz-editor', QuizEditor);