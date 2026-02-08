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
  }

  get quizzesAttempts() {
    return this.querySelector("#quizzes-attempts");
  }

  render() {
    this.innerHTML = `
    <collapsible-panel title=" " open>
      <div id="quizzes-attempts" class="container">Loading...</div>
    </collapsible-panel>
    `;
  }

  async loadData() {
    if (!this.userId) this.userId = this.getAttribute("user-id");
    
    if (!this.userId) return;

    try {
        const attempts = await this.attemptsService.getAttemptsByStudentId(this.userId);
        
        if (!attempts || attempts.length === 0) {
            this.quizzesAttempts.innerHTML = `<alert-component type="warning" message="Start a quiz to see your attempts here."></alert-component>`;
        } else {
            attempts.sort((a, b) => new Date(b.startedAt || 0) - new Date(a.startedAt || 0));
            
            const listHtml = `
                <div class="list-group">
                    ${attempts.map(att => this.getQuizAttemptRow(att)).join('')}
                </div>`;
            this.quizzesAttempts.innerHTML = listHtml;
        }
    } catch (e) {
        console.error(e);
        this.quizzesAttempts.innerHTML = `<alert-component type="danger" message="Error loading history."></alert-component>`;
    }
  }

  getQuizAttemptRow(att) {
    if (att.status === "COMPLETED") {
        const pct = att.score && att.maxScore ? (att.score / att.maxScore) : 0;
        const color = pct > 0.6 ? "success" : "danger";
        return `
        <div class="list-group-item d-flex justify-content-between align-items-center">
            ${att.quizTitle}
            <span class="badge text-bg-${color}">${att.status} (${(pct * 100).toFixed(0)}%)</span>
        </div>`;
    } else {
        return `
        <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
           onclick="sessionStorage.setItem('currentQuizId', '${att.quizId}'); 
                    sessionStorage.setItem('currentTestId', '${att.testId || ''}'); 
                    globalThis.location.hash = '#quiz-runner'; return false;">
            ${att.quizTitle}
            <span class="badge text-bg-secondary">${att.status}</span>
        </a>`;
    }
  }
}

customElements.define('quizzes-attempts-viewer', QuizzesAttemptsViewer);