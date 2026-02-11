import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";
import "./shared/collapsible-panel.js";
import "./shared/alert.js";

export class QuizzesAttemptsViewer extends BaseComponent {
  setupComponent() {
    this.userId = this.getAttribute("user-id");
    this.attemptsService = new AttemptsService();
    this.render();
    this.loadData();
  }

  attachEventListeners() {
    document.addEventListener("attempt-created", () => {
        this.loadData();
    });
    this.addEventListenerWithTracking("#show-only-in-progress-attempts", "click", () => this.loadData());
  }

  get quizzesAttempts() {
    return this.querySelector("#quizzes-attempts");
  }

  render() {
    this.innerHTML = `
    <collapsible-panel title=" " open>
      <input class="form-check-input" type="checkbox" value="1" id="show-only-in-progress-attempts" checked>
      <label class="form-check-label" for="show-only-in-progress-attempts">
        Show only in progress attempts
      </label>
      <div id="quizzes-attempts" class="container">Loading...</div>
    </collapsible-panel>
    `;
  }

  async loadData() {
    if (!this.userId) this.userId = this.getAttribute("user-id");
    
    if (!this.userId) return;

    const showOnlyInProgressAttempts = this.querySelector("#show-only-in-progress-attempts").checked;
    try {
        const attempts = await this.attemptsService.getAttemptsByStudentId(this.userId);
        const filteredAttempts = attempts.filter((attempt => !showOnlyInProgressAttempts || attempt.status == "STARTED"));

        if (!filteredAttempts || filteredAttempts.length === 0) {
            this.quizzesAttempts.innerHTML = `<alert-component type="primary" message="No attempts to show"></alert-component>`;
        } else {
            filteredAttempts.sort((a, b) => new Date(b.startedAt || 0) - new Date(a.startedAt || 0));
            
            const listHtml = `
                <div class="list-group">
                    ${filteredAttempts.map(attempt => this.getQuizAttemptRow(attempt)).join('')}
                </div>`;
            this.quizzesAttempts.innerHTML = listHtml;
        }
    } catch (e) {
        console.error(e);
        this.quizzesAttempts.innerHTML = `<alert-component type="danger" message="Error loading history."></alert-component>`;
    }
  }

  getQuizAttemptRow(attempt) {
    if (attempt.status === "COMPLETED") {
        const pct = attempt.score && attempt.maxScore ? (attempt.score / attempt.maxScore) : 0;
        const color = pct > 0.6 ? "success" : "danger";
        return `
        <div class="list-group-item d-flex justify-content-between align-items-center">
            ${attempt.quizTitle}
            <span class="badge text-bg-${color}">${attempt.status} (${(pct * 100).toFixed(0)}%)</span>
        </div>`;
    } else {
        return `
        <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
           onclick="sessionStorage.setItem('currentQuizId', '${attempt.quizId}'); 
                    sessionStorage.setItem('currentTestId', '${attempt.testId || ''}'); 
                    globalThis.location.hash = '#quiz-runner'; return false;">
            ${attempt.quizTitle}
            <span class="badge text-bg-secondary">${attempt.status}</span>
        </a>`;
    }
  }
}

customElements.define('quizzes-attempts-viewer', QuizzesAttemptsViewer);