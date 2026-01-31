import { AttemptsService } from "../services/attempts-service.js";

export class Quiz extends HTMLElement {
  connectedCallback() {
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
    <a class="quiz-button" data-id=${this.id}>
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

    this.querySelectorAll(".quiz-button").forEach(button => {
      button.addEventListener("click", (event) => {
        const quizId = event.target.getAttribute("data-id");
        this.handleQuizButtonClick(quizId);
      })
    });
  }

  async handleQuizButtonClick() {
    if (this.role == "TEACHER") {
      window.location = `../quiz-editor/?id=${this.id}`;
      return;
    } else if (this.role == "STUDENT") {
      const response = await this.attemptsService.addAttempt(this.id, this.userId);
      this.dispatchEvent(new CustomEvent("quiz-attempt-started", {
          bubbles: true,
          composed: true
      }))
    }
  }
}
customElements.define('quiz-item', Quiz);