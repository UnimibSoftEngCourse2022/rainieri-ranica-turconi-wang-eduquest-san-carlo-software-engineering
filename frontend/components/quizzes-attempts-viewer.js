import { AttemptsService } from "../services/attempts-service.js";

export class QuizzesAttemptsViewer extends HTMLElement {
  connectedCallback() {
    this.userId = this.getAttribute("user-id");
    this.attemptsService = new AttemptsService();
    this.render();
    this.loadData();

    document.addEventListener("quiz-attempt-started", () => this.loadData());
  }

  get quizzesAttempts() {
    return this.querySelector("#quizzes-attempts");
  }

  render() {
    this.innerHTML = `
    <div id="quizzes-attempts" class="container"></div>
    `;
  }

  async loadData() {
    const quizzesAttempts = await this.attemptsService.getAttemptsByStudentId(this.userId);

    if (quizzesAttempts) {
        if (quizzesAttempts.length == 0) {
          this.quizzesAttempts.innerHTML = `
          <div class="alert alert-warning" role="alert">
            You haven't started a quiz yet!
          </div>
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
        <div class="alert alert-danger" role="alert">
            Error trying to access the quizzes attempts, please try again later
        </div>
        `
    }
  };

  getQuizAttemptRow(quizAttempt) {
    if (quizAttempt.status == "COMPLETED") {
      const quizResultPercentage = quizAttempt.score / quizAttempt.maxScore;
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