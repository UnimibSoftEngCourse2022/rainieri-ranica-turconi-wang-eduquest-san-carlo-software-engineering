import { GamificationService } from "../services/gamification-service.js";
import { UsersService } from "../services/users-service.js";
import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { Quiz } from "./quiz-item.js"
import { Alert } from "./shared/alert.js";


export class ChallengesViewer extends BaseComponent {
  setupComponent() {
    this.usersService = new UsersService();
    this.gamificationService = new GamificationService();
    this.render();
    this.loadData();
  }

  render() {
    this.innerHTML = '<div id="challenges-container" class="row g-4"></div>';
  }

  attachEventListeners() {
    document.addEventListener("challenge-added", (event) => this.loadData());
  }

  get challengesContainer() { return this.querySelector("#challenges-container"); }

  async loadData() {
    this.userData = await this.usersService.getMyUserInfo();
    if (!this.userData) {
      this.innerHTML = `
      <alert-component type="danger" message="Cannot load user data, please try again later"></alert-component>
      `;
      return;
    }

    this.challengesContainer.innerHTML = ``;
    const challenges = await this.gamificationService.getMyChallenges();
    console.log(challenges);
    if (!challenges) {
      this.challengesContainer.innerHTML = `
      <alert-component type="danger" message="Error loading your challenges, please try again later"></alert-component>
      `;
    }

    if (challenges.length == 0) {
      this.challengesContainer.innerHTML = `
      <alert-component type="info" message="No challenges to display"></alert-component>
      `;
    } else {
      challenges.forEach(challenge => {
        const challengeElement = document.createElement("div");
        const challengeBadgeColor = challenge.status == "COMPLETED" ? `success` : `warning`;

        const completedQuiz =    `<span class="badge text-bg-success">Quiz completed</span>`;
        const notCompletedQuiz = `<span class="badge text-bg-warning">Quiz not completed yet</span>`;

        challengeElement.innerHTML = `
        <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
            <h3>${challenge.quizTitle}</h3>
            <span class="badge text-bg-${challengeBadgeColor}">${challenge.status}</span>

            <div style="display: flex; gap: 10px; margin-top: 15px;">

                <div style="flex: 1; aspect-ratio: 1 / 1; border: 1px solid #eee; padding: 10px; overflow: hidden; height: 120px;">
                    <h4>Challenger</h4>  
                    ${challenge.challenger.name} ${challenge.challenger.surname}
                    ${challenge.challenger.id == this.userData.id ? `(You)` : ``}
                    <br>
                    ${challenge.challenger.hasCompletedQuiz ? completedQuiz : notCompletedQuiz}
                </div>

                <div style="flex: 1; aspect-ratio: 1 / 1; border: 1px solid #eee; padding: 10px; overflow: hidden; height: 120px;">
                    <h4>Opponent</h4>
                    ${challenge.opponent.name} ${challenge.opponent.surname}
                    ${challenge.opponent.id == this.userData.id ? `(You)` : ``}
                    <br>
                    ${challenge.opponent.hasCompletedQuiz ? completedQuiz : notCompletedQuiz}
                </div>

            </div>

            ${challenge.status == "COMPLETED" ? `<h4>Winner: ${challenge.winnerName} ${challenge.winnerSurname}` : null}
        </div>
        `
        this.challengesContainer.appendChild(challengeElement);
      })
    }
  }
}

customElements.define('challenges-viewer', ChallengesViewer);