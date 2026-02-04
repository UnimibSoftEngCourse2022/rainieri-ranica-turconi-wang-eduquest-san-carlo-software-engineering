import { QuizService } from "../services/quiz-service.js";
import { TestsService } from "../services/tests-service.js";
import { BaseComponent } from "./base-component.js";
import { Alert } from "./shared/alert.js";

export class AddTest extends BaseComponent {
  setupComponent() {
    this.quizService = new QuizService();
    this.testsService = new TestsService();
    this.quizzes = [];
    this.render();
    this.loadQuizzes(); 
  }

  async loadQuizzes() {
      this.quizzes = await this.quizService.getQuizzes() || [];
      this.render();
  }

  get quizSelect() { return this.querySelector("#test-quiz-select"); }
  get timeLimitInput() { return this.querySelector("#test-time-input"); }
  get maxAttemptsInput() { return this.querySelector("#test-attempts-input"); }
  get addTestResult() { return document.getElementById("add-test-result"); }

  render() {
    const options = this.quizzes.length > 0 
        ? this.quizzes.map(q => `<option value="${q.id}">${q.title}</option>`).join('') 
        : '<option disabled>Loading quizzes...</option>';

    // --- MODIFICATO QUI SOTTO: placeholder="0" invece di value="0" ---
    this.innerHTML = `
    <div class="mb-3">
        <label for="test-quiz-select" class="form-label">Select Quiz</label>
        <select class="form-select" id="test-quiz-select">
            <option value="" disabled selected>Choose a quiz...</option>
            ${options}
        </select>
    </div>

    <div class="mb-3">
        <label for="test-time-input" class="form-label">Time Limit (minutes)</label>
        <input type="number" class="form-control" id="test-time-input" min="1" placeholder="0" />
    </div>

    <div class="mb-3">
        <label for="test-attempts-input" class="form-label">Max Attempts</label>
        <input type="number" class="form-control" id="test-attempts-input" min="1" placeholder="0" />
    </div>

    <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" id="add-test-button">Create Test</button>
    </div>
    <div id="add-test-result" class="container"></div>
    `;

    this.attachEventListeners();
  }

  attachEventListeners() {
    const btn = this.querySelector("#add-test-button");
    if (btn) {
        btn.onclick = (e) => this.handleAddTest(e);
    }
  }

  async handleAddTest(event) {
    event.preventDefault();

    const quizId = this.quizSelect ? this.quizSelect.value : null;
    const timeLimit = this.timeLimitInput.value;
    const maxAttempts = this.maxAttemptsInput.value;
    
    // Controllo rigoroso: se l'utente non scrive nulla, timeLimit è una stringa vuota ""
    // e quindi entra nell'if mostrando l'errore "Please fill all fields".
    if (!quizId || timeLimit === "" || maxAttempts === "") {
        this.addTestResult.innerHTML = `<alert-component type="warning" message="Please fill all fields"></alert-component>`;
        return;
    }

    const requestBody = {
        quizId: parseInt(quizId),
        maxDurationMinutes: parseInt(timeLimit),
        maxTries: parseInt(maxAttempts)
    };
    
    this.submitData(requestBody);
  }

  async submitData(requestBody) {
    const success = await this.testsService.createTest(requestBody);
    if (success) {
        this.addTestResult.innerHTML = `
        <alert-component type="success" message="Test created successfully"></alert-component>
        `
        this.dispatchEvent(new CustomEvent("test-created", {
            bubbles: true,
            composed: true
        }))
    } else {
        this.addTestResult.innerHTML = `
        <alert-component type="warning" message="Error during test creation"></alert-component>
        `
    }
  }
}

customElements.define('add-test', AddTest);