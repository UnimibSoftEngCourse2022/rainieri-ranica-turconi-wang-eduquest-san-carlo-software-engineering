import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";

export class Quiz extends BaseComponent {
  setupComponent() {
    this.id = this.getAttribute('id');
    this.title = this.getAttribute('title');
    this.description = this.getAttribute('description') || "";
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');

    this.attemptsService = new AttemptsService();

    let buttonText = "";
    if (this.role === "STUDENT") {
      buttonText = "Run quiz";
    } else if (this.role === "TEACHER") {
      buttonText = "Edit quiz";
    }
    const button = `
    <a class="quiz-button">
      <button class="btn btn-sm btn-primary">${buttonText}</button>
    </a>
    `;

    this.innerHTML = `
      <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
        <h3>${this.title}</h3>
        <p>${this.description}</p>
        ${button}
    </div>
    `;

  }
  
  attachEventListeners() {
    this.addEventListenerWithTracking(".quiz-button", "click", (event) => this.handleQuizButtonClick());
  }

  async handleQuizButtonClick() {
    if (this.role == "TEACHER") {
      window.location = `../quiz-editor/?id=${this.id}`;
      return;
    } else if (this.role == "STUDENT") {
      const response = await this.attemptsService.addAttempt(this.id, this.userId);
      this.dispatchCustomEvent("quiz-attempt-started");
    }
  }
}

customElements.define('quiz-item', Quiz);