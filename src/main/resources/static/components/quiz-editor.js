import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import "./questions-viewer.js";
import "./shared/alert.js";

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
    this.addEventListenerWithTracking("#quiz-editor-general-info-form", "submit", (event) => {
        event.preventDefault();
        this.handleSaveQuizGeneralInfo();
    });
  }

  get quizTitleInput() { return this.querySelector("#title-input"); }
  get quizDescriptionInput() { return this.querySelector("#description-input"); }
  get quizIsPublicInput() { return this.querySelector("#is-public-input"); }
  get quizQuestions() { return this.querySelector("#questions"); }
  get quizStats() { return this.querySelector("#stats"); }

  render() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <div class="d-flex justify-content-start mb-3">
            <a href="../teacher-dashboard/index.html" class="btn btn-outline-secondary">
                ← Back to Dashboard
            </a>
        </div>
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
            <div class="mb-4 form-check text-start d-inline-block border p-3 rounded bg-light">
                <input type="checkbox" class="form-check-input" id="is-public-input">
                <label class="form-check-label fw-bold" for="is-public-input">
                    Make Public 🔓
                </label>
                <div class="form-text">
                    If checked, students will see this quiz in their Practice Dashboard.
                </div>
            </div>
            <input type="submit" class="btn btn-primary form-control" value="Save changes"></input>
            <div id="quiz-general-info-result"></div>
        </form>
        <hr>
        <div id="questions"></div>
        <div id="stats"></div>
        <hr>
        <h3>Add a question</h3>
        <questions-viewer userRole="TEACHER" quizId="${this.quizId}"></questions-viewer>
    </div>
    `;
  }

  async loadData() {
    try {
        const quizData = await this.quizService.getQuizById(this.quizId);
        this.quizTitleInput.value = quizData.title;
        this.quizDescriptionInput.value = quizData.description;
        if (this.quizIsPublicInput) {
            this.quizIsPublicInput.checked = quizData.isPublic;
        }
        this.showQuizQuestions(quizData);
        this.showQuizStats(quizData.quizStats);
    } catch (e) {
        console.error(e);
        this.innerHTML = `
        <alert-component type="danger" message="Error trying to show the quiz" timeout="5000"></alert-component>
        `;
    }
  }

  showQuizQuestions(quizData) {
    const questions = quizData.questions;
    if (!questions.length) {
        this.quizQuestions.innerHTML = `<alert-component type="info" message="No questions added to the quiz yet"></alert-component>`;
        return;
    }

    let questionsHTML = ``;
    questions.forEach(q => {
        const questionRelativeStats = quizData.quizStats.statsPerQuestion[q.id];
        let questionRelativeStatsHTML = ``;
        if (questionRelativeStats) {
            const questionRelativePercentage = questionRelativeStats.totalAnswers ? questionRelativeStats.correctAnswer / questionRelativeStats.totalAnswers : 0;
            const questionRelativeStatsColor = questionRelativePercentage >= 0.6 ? `success` : `danger`;
            questionRelativeStatsHTML = `
            <span class="badge text-bg-${questionRelativeStatsColor}">
            ${questionRelativeStats.correctAnswer} correct answer, ${questionRelativeStats.totalAnswers}
            total attempts (${questionRelativePercentage * 100}%)
            </span>
            `;
        }

        questionsHTML +=`
        <a class="list-group-item list-group">
            ${q.text}
            <button class="btn remove-question-from-quiz-button" data-id="${q.id}">🗑️</button>
            ${questionRelativeStatsHTML}
        </a>
        `;
    });
    this.quizQuestions.innerHTML = `<h3>Quiz questions</h3><div class="list-group">${questionsHTML}</div>`;
  }

  async removeQuestionFromQuiz(questionId) {
    try {
        await this.quizService.removeQuestionFromQuiz(this.quizId, questionId);
        this.loadData();
    } catch (e) {
        console.error(e);
    }
  }

  async handleSaveQuizGeneralInfo() {
    const title = this.querySelector("#title-input").value;
    const description = this.querySelector("#description-input").value;
    const isPublicInput = this.querySelector("#is-public-input");
    const isPublic = isPublicInput.checked;
    const questionsCount = this.quizQuestions.querySelectorAll(".list-group-item").length;
    const resultDiv = this.querySelector("#quiz-general-info-result");

    if (isPublic && questionsCount == 0) {
        isPublicInput.checked = false; 
        resultDiv.innerHTML = `
        <alert-component type="danger" message="You cannot make a quiz public without any questions." timeout="2500"></alert-component>
        `;
        return; 
    }

    const quizData = {
        title, description, isPublic
    };
    
    try {
        await this.quizService.modifyQuiz(this.quizId, quizData);
        resultDiv.innerHTML = `
        <alert-component type="success" message="Quiz modified correctly" timeout="2000"></alert-component>
        `;
    } catch (e) {
        console.error(e);
        resultDiv.innerHTML = `
        <alert-component type="danger" message="Title and description cannot be empty!" timeout="2500"></alert-component>
        `;
    }
  }

  showQuizStats(quizStats) {
	const avgScore = quizStats.averageScore ? Number(quizStats.averageScore).toFixed(2) : "0.00";
    const quizStatsMessage = `
    <h4>Quiz stats</h4>
    Total attempts: ${quizStats.totalAttempts} | Average score: ${avgScore} 
    `;
    this.quizStats.innerHTML = `
    <alert-component type="primary" message="${quizStatsMessage}"></alert-component>
    `
  }
}

customElements.define('quiz-editor', QuizEditor);