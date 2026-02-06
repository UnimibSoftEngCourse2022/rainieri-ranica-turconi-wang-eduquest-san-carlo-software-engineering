import { GamificationService } from "../services/gamification-service.js";
import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { Quiz } from "./quiz-item.js"
import { Alert } from "./shared/alert.js";


export class ChallengesViewer extends BaseComponent {
  setupComponent() {
    this.gamificationService = new GamificationService();
    this.render();
    this.loadData();
  }

  render() {
    this.innerHTML = '<div id="challenges-container" class="row g-4"></div>'
  }

  get challengesContainer() { return this.querySelector("#challenges-container"); }

  async loadData() {
    const challenges = await this.gamificationService.getMyChallenges();
    console.log(challenges);
    if (!challenges) {
      this.challengesContainer.innerHTML = `
      <alert-component type="danger" message="Error loading your challenges, please try again later"></alert-component>
      `;
    }

    if (challenges.length == 0) {
      this.challengesContainer.innerHTML = `
      <alert-component type="success" message="No challenges to display"></alert-component>
      `;
    } else {
      challenges.forEach(challenge => {
        const challengeElement = document.createElement("div");
        const challengeBadgeColor = challenge.status == "COMPLETED" ? `success` : `warning`
        
        challengeElement.innerHTML = `
        <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
            <h3>${challenge.quizTitle} vs ${challenge.opponentName} ${challenge.opponentSurname}</h3>
            <span class="badge text-bg-${challengeBadgeColor}">${challenge.status}</span>
        </div>
        `
        this.challengesContainer.appendChild(challengeElement);
      })
    }
  }
}

customElements.define('challenges-viewer', ChallengesViewer);