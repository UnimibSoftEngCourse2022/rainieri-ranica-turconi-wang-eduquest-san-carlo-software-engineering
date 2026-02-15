import { GamificationService } from "../services/gamification-service.js";
import { UsersService } from "../services/users-service.js";
import { BaseComponent } from "./base-component.js";
import "./quiz-item.js"
import "./shared/alert.js";
import "./shared/collapsible-panel.js";


export class ChallengesViewer extends BaseComponent {
  setupComponent() {
    this.usersService = new UsersService();
    this.gamificationService = new GamificationService();
    this.render();
    this.loadData();
  }

  render() {
    this.innerHTML = `
    <collapsible-panel title=" " open>
      <input class="form-check-input" type="checkbox" value="1" id="show-only-active-challenges" checked>
      <label class="form-check-label" for="show-only-active-challenges" checked>
        Show only in progress challenges
      </label>
      <div id="challenges-container" class="row g-4"></div>
    </collapsible-panel>
    `;
  }

  attachEventListeners() {
    document.addEventListener("challenge-added", (event) => this.loadData());
    this.addEventListenerWithTracking("#show-only-active-challenges", "click", () => this.loadData());
  }

  get challengesContainer() { return this.querySelector("#challenges-container"); }

  async loadData() {
    try {
      this.userData = await this.usersService.getMyUserInfo();
    } catch (e) {
      console.error(e);
      this.innerHTML = `
      <alert-component type="danger" message="Error loading challenges"></alert-component>
      `;
      return;
    }

    this.challengesContainer.innerHTML = ``;
    let challenges;
    try {
      challenges = await this.gamificationService.getMyChallenges();
    } catch (e) {
      console.error(e);
      this.challengesContainer.innerHTML = `
      <alert-component type="danger" message="Error loading your challenges, please try again later"></alert-component>
      `;
    }

    const showOnlyInActiveChallenges = this.querySelector("#show-only-active-challenges").checked;
    const filteredChallenges = challenges.filter(challenge => !showOnlyInActiveChallenges || challenge.status == "ACTIVE");
    
    if (filteredChallenges.length == 0) {
      this.challengesContainer.innerHTML = `
      <alert-component type="info" message="No challenges to display"></alert-component>
      `;
    } else {
      filteredChallenges.forEach(challenge => {
        const challengeElement = document.createElement("div");
        let challengeBadgeColor = "warning";
        if (challenge.status === "COMPLETED") {
          challengeBadgeColor = "success";
        } else if (challenge.status === "ACTIVE") {
          challengeBadgeColor = "primary";
        } else if (challenge.status === "EXPIRED") {
          challengeBadgeColor = "secondary";
        }

        const completedQuiz =    `<span class="badge text-bg-success">Quiz completed</span>`;
        const notCompletedQuiz = `<span class="badge text-bg-warning">Quiz not completed yet</span>`;
        
        let challengerStyle = "border: 1px solid #eee;";
        let opponentStyle = "border: 1px solid #eee;";
        if (challenge.status === "COMPLETED" && challenge.winnerName) {
            const challengerWon = challenge.winnerName === challenge.challenger.name && challenge.winnerSurname === challenge.challenger.surname;
            const opponentWon = challenge.winnerName === challenge.opponent.name && challenge.winnerSurname === challenge.opponent.surname;
            if (challengerWon) {
                challengerStyle = "border: 2px solid #198754; background-color: #d1e7dd; border-radius: 8px;";
                opponentStyle = "border: 2px solid #dc3545; background-color: #f8d7da; border-radius: 8px;";
            } else if (opponentWon) {
                challengerStyle = "border: 2px solid #dc3545; background-color: #f8d7da; border-radius: 8px;";
                opponentStyle = "border: 2px solid #198754; background-color: #d1e7dd; border-radius: 8px;";
            }
        }

        challengeElement.innerHTML = `
        <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
            <h3>${challenge.quizTitle}</h3>
            <span class="badge text-bg-${challengeBadgeColor}">${challenge.status}</span>

            <div style="display: flex; gap: 10px; margin-top: 15px;">

                <div style="flex: 1; aspect-ratio: 1 / 1; padding: 10px; overflow: hidden; height: 120px; ${challengerStyle}">
                    <h4>Challenger</h4>  
                    ${challenge.challenger.name} ${challenge.challenger.surname}
                    ${challenge.challenger.id == this.userData.id ? `(You)` : ``}
                    <br>
                    ${challenge.challenger.hasCompletedQuiz ? completedQuiz : notCompletedQuiz}
                </div>

                <div style="flex: 1; aspect-ratio: 1 / 1; padding: 10px; overflow: hidden; height: 120px; ${opponentStyle}">
                    <h4>Opponent</h4>
                    ${challenge.opponent.name} ${challenge.opponent.surname}
                    ${challenge.opponent.id == this.userData.id ? `(You)` : ``}
                    <br>
                    ${challenge.opponent.hasCompletedQuiz ? completedQuiz : notCompletedQuiz}
                </div>

            </div>

            ${challenge.status == "COMPLETED" ? (
              challenge.winnerName ? `<h4>Winner: ${challenge.winnerName} ${challenge.winnerSurname}` :`<h4>Draw</h4>`
            ) : ``}
        </div>
        `
        this.challengesContainer.appendChild(challengeElement);
      })
    }
  }
}

customElements.define('challenges-viewer', ChallengesViewer);