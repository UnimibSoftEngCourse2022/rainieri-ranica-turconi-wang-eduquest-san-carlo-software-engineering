import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";

export class Quiz extends BaseComponent {
  setupComponent() {
    this.attemptsService = new AttemptsService();
    this.render();
  }

  set quizData(quizData) {
    this._quizData = quizData;
    this.render();
  }

  set userId(userId) {
    this._userId = userId;
    this.render();
  }

  set role(role) {
    this._role = role;
    this.render();
  }

  render() {
    let buttonText = "";
    if (this._role === "STUDENT") {
      buttonText = "Run quiz";
    } else if (this._role === "TEACHER") {
      buttonText = "Edit quiz";
    }
    const button = `
    <a class="quiz-button">
      <button class="btn btn-sm btn-primary">${buttonText}</button>
    </a>
    `;

    this.innerHTML = `
    <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
        <h3>${this._quizData.title}</h3>
        <p>${this._quizData.description}</p>
        ${button}
        <hr>
        Average score: ${(this._quizData.quizStats.averageScore || 0).toFixed(2)} | Total attempts: ${this._quizData.quizStats.totalAttempts}
    </div>
    `;
  }

  attachEventListeners() {
    this.addEventListenerWithTracking(".quiz-button", "click", (event) => this.handleQuizButtonClick());
  }

  async handleQuizButtonClick() {
    if (this._role == "TEACHER") {
      window.location = `../quiz-editor/?id=${this._quizData.id}`;
      return;
    } else if (this._role == "STUDENT") {
      window.location.href = `?view=runner&quizId=${this._quizData.id}`;
    }
  }
}

customElements.define('quiz-item', Quiz);