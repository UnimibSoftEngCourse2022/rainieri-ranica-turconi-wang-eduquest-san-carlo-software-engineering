import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";
import { Alert } from "./shared/alert.js";
import "./shared/collapsible-panel.js";

export class QuizzesAttemptsViewer extends BaseComponent {
  setupComponent() {
    this.userId = this.getAttribute("user-id");
    this.attemptsService = new AttemptsService();
    this.render();
    this.loadData();

  }

  attachEventListeners() {
    document.addEventListener("quiz-attempt-started", () => this.loadData());
  }

  get quizzesAttempts() {
    return this.querySelector("#quizzes-attempts");
  }

  render() {
    this.innerHTML = `
    <collapsible-panel title=" " open>
      <div id="quizzes-attempts" class="container"></div>
    </collapsible-panel>
    `;
  }

  async loadData() {
    const quizzesAttempts = await this.attemptsService.getAttemptsByStudentId(this.userId);

    if (quizzesAttempts) {
        if (quizzesAttempts.length == 0) {
          this.quizzesAttempts.innerHTML = `
          <alert-component type="warning" message="Start a quiz to see your attempts here."></alert-component>
          `
        } else {
          let quizzesAttemptsHTML = `<div class="list-group">`
          quizzesAttempts.forEach(quizAttempt => {
            quizzesAttemptsHTML += this.getQuizAttemptRow(quizAttempt);
          })
          quizzesAttemptsHTML += `</div>`
          this.quizzesAttempts.innerHTML = quizzesAttemptsHTML;
        }
    } else {
        this.quizzesAttempts.innerHTML = `
        <alert-component type="danger" message="Error trying to access the quizzes attempts, please try again later"></alert-component>
        `
    }
  };

  getQuizAttemptRow(quizAttempt) {
    if (quizAttempt.status == "COMPLETED") {
      const quizResultPercentage = quizAttempt.score ? quizAttempt.score / quizAttempt.maxScore : 0;
      const badgeColor = quizResultPercentage > 0.6 ? "success" : "danger";
      return `
      <p class="list-group-item list-group"">
        ${quizAttempt.quizTitle}
        <span class="badge text-bg-${badgeColor}">${quizAttempt.status} (${quizResultPercentage * 100}%)</span>
      </a>
      `
    } else {
      const quizAttemptLink = `../quiz-runner/?quizAttemptId=${quizAttempt.id}`
      return `
        <a class="list-group-item list-group" href="${quizAttemptLink}">
          ${quizAttempt.quizTitle}
          <span class="badge text-bg-secondary">${quizAttempt.status}</span>
        </a>
      `
    }
  }
}

customElements.define('quizzes-attempts-viewer', QuizzesAttemptsViewer);