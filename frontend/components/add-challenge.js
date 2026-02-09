import { GamificationService } from "../services/gamification-service.js";
import { BaseComponent } from "./base-component.js";
import "./quiz-item.js"
import "./shared/alert.js";

export class AddChallenge extends BaseComponent {
  setupComponent() {
    this.gamificationService = new GamificationService();
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
                <input
                    type="numeric"
                    class="form-control"
                    id="quiz-id-input"
                />
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
    } catch(Error) {
        this.addChallengeResult.innerHTML = `
        <alert-component type="danger" message="Error adding the challenge" timeout="2000"></alert-component>
        `;
    }
  }
}

customElements.define('add-challenge', AddChallenge);