import { GamificationService } from "../services/gamification-service.js";
import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import "./quiz-item.js"
import "./shared/alert.js";

export class AddChallenge extends BaseComponent {
  setupComponent() {
    this.gamificationService = new GamificationService();
    this.quizService = new QuizService();
    this.render();
  }

  render() {
    this.innerHTML = `
        <div class="container text-center">
        <form id="add-challenge-form">
            <div class="mb-3">
                <label for="opponent-id-input" class="form-label">
                    Opponent ID
                </label>
                <input
                    type="numeric"
                    class="form-control"
                    id="opponent-id-input"
                />
            </div>
            <div class="mb-3">
                <label for="opponent-id-input" class="form-label">
                    Quiz ID
                </label>
                <select class="form-control" id="quiz-id-input" name="quiz-id-input">
                </select>
            </div>
            <div class="mb-3">
                <label for="duration-input" class="form-label">
                    Duration (in hours)
                </label>
                <input
                    type="numeric"
                    class="form-control"
                    id="duration-input"
                />
            </div>
            <div id="other-fields"></div>
            <button type="submit" class="btn btn-primary" id="add-challenge-button">Add challenge</button>
            <div class="container my-2" id="add-challenge-result"></div>
        </form>
    </div>
    `;
    this.loadQuizzesOptions();
  }

  async loadQuizzesOptions() {
    try {
        const quizzesInput = this.querySelector("#quiz-id-input");

        const quizzes = await this.quizService.getQuizzes();
        quizzes.forEach(quiz => {
            const quizElement = document.createElement("option");
            quizElement.value = quiz.id;
            quizElement.innerHTML = quiz.title;
            quizzesInput.appendChild(quizElement);
        });
    } catch (e) {
        console.error(e);
    }
  }

  get addChallengeForm() { return this.querySelector("#add-challenge-form"); }
  get addChallengeResult() { return this.querySelector("#add-challenge-result"); }

  attachEventListeners() {
    this.addEventListenerWithTracking("#add-challenge-form", "submit", (event) => {
        event.preventDefault();
        this.handleAddQuestion();
    });
  }

  async handleAddQuestion() {
    const opponentId = this.querySelector("#opponent-id-input").value;
    const quizId = this.querySelector("#quiz-id-input").value;
    const duration = this.querySelector("#duration-input").value;

    try {
        await this.gamificationService.addChallenge(opponentId, quizId, duration);
        this.addChallengeResult.innerHTML = `
        <alert-component type="success" message="Challenge added correctly" timeout="2000"></alert-component>
        `;
        this.addChallengeForm.reset();
        this.dispatchCustomEvent("challenge-added");
    } catch {
        this.addChallengeResult.innerHTML = `
        <alert-component type="danger" message="Error adding the challenge" timeout="2000"></alert-component>
        `;
    }
  }
}

customElements.define('add-challenge', AddChallenge);