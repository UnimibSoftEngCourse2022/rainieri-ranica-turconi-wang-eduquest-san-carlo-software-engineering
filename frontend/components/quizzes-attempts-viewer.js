import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";
import "./shared/collapsible-panel.js";
import "./shared/alert.js";

export class QuizzesAttemptsViewer extends BaseComponent {
  setupComponent() {
    this.userId = this.getAttribute("user-id");
    this.attemptsService = new AttemptsService();
    this.filteredAttempts = [];
    this.render();
    this.loadData();
  }

  attachEventListeners() {
    document.addEventListener("attempt-created", () => {
        this.loadData();
    });
    this.addEventListenerWithTracking("#show-only-in-progress-attempts", "click", () => this.loadData());

    const attemptsContainer = document.querySelector('#quizzes-attempts');
    attemptsContainer.addEventListener('click', async (event) => this.updateQuizAttemptDetails(event));
  }

  async updateQuizAttemptDetails(event) {
    const item = event.target.closest('.list-group-item');
      
    if (!item) {
      return;
    }
    const attemptId = item.id.replace('attempt-', '');
    const details = this.quizAttemptDetails;
    details.innerHTML = await this.getQuizAttemptDetails(attemptId);
  }

  get quizzesAttempts() { return this.querySelector("#quizzes-attempts"); }
  get quizAttemptDetails() { return this.querySelector("#quiz-attempt-details"); }

  render() {
    this.innerHTML = `
    <collapsible-panel title=" " open>
      <input class="form-check-input" type="checkbox" value="1" id="show-only-in-progress-attempts" checked>
      <label class="form-check-label" for="show-only-in-progress-attempts">
        Show only in progress attempts
      </label>
      <div id="quizzes-attempts" class="container">Loading...</div>
    </collapsible-panel>

    ${this.getQuizAttemptDetailsModal()}
    `;
  }

  getQuizAttemptDetailsModal() {
    return `
    <div class="modal fade" id="quiz-attempt-details-modal" tabindex="-1" aria-labelledby="quiz-attempt-details-modal" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h1 class="modal-title fs-5" id="quiz-attempt-details-modal">Attempt viewer</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body" id="quiz-attempt-details">
          </div>
        </div>
      </div>
    </div>
    `;
  }

  async loadData() {
    if (!this.userId) this.userId = this.getAttribute("user-id");
    
    if (!this.userId) return;

    const showOnlyInProgressAttempts = this.querySelector("#show-only-in-progress-attempts").checked;
    try {
        const attempts = await this.attemptsService.getAttemptsByStudentId(this.userId);
        this.filteredAttempts = attempts.filter((attempt => !showOnlyInProgressAttempts || attempt.status == "STARTED"));

        if (!this.filteredAttempts || this.filteredAttempts.length === 0) {
            this.quizzesAttempts.innerHTML = `<alert-component type="primary" message="No attempts to show"></alert-component>`;
        } else {
            this.filteredAttempts.sort((a, b) => new Date(b.startedAt || 0) - new Date(a.startedAt || 0));
            const listHtml = `
                <div class="list-group">
                    ${this.filteredAttempts.map(attempt => this.getQuizAttemptRow(attempt)).join('')}
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
        const color = pct >= 0.6 ? "success" : "danger";
        return `
        <div class="list-group-item d-flex justify-content-between align-items-center" id="attempt-${attempt.id}" data-bs-toggle="modal" data-bs-target="#quiz-attempt-details-modal">
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

  async getQuizAttemptDetails(attemptId) {
    const quizAttemptSession = await this.attemptsService.getAttemptSessionById(attemptId);
    let quizAttemptDetailsHTML = ``;
    quizAttemptSession.questions.forEach(question => {
      const answerObject = quizAttemptSession.existingAnswers.find(a => a.questionId == question.id);
      let answerText;
      if (answerObject) {
        answerText = question.questionType == "OPENED" ? answerObject.textOpenAnswer : answerObject.selectedOptionText;
      } else {
        answerText = `None`;
      }
      const answerTextClass = answerObject?.correct ? `text-success` : `text-danger`;

      quizAttemptDetailsHTML += `
      <p>Question: "${question.text}</p>
      <p class="${answerTextClass}">Your answer: ${answerText}</p>
      <hr>
      `
    })
    return quizAttemptDetailsHTML;  
  }
}

customElements.define('quizzes-attempts-viewer', QuizzesAttemptsViewer);