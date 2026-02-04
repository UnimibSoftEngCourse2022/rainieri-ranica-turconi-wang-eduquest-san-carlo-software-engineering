import { UsersService } from "../services/users-service.js";
import { BaseComponent } from "./base-component.js";
import { GamificationService } from "../services/gamification-service.js";
import { Alert } from "./shared/alert.js";

export class MissionsViewer extends BaseComponent {
    setupComponent() {
        this.GamificationService = new GamificationService();
        this.render();
    }

    async render() {
        const missionsProgresses = await this.GamificationService.getUserMissionsProgresses();
        console.log(missionsProgresses);
        if (missionsProgresses.length == 0) {
            this.innerHTML = `<alert-component type="warning" message="Complete your first quiz to see the missions!"></alert-component>`
            return;
        }

        this.innerHTML = `<div class="row g-4" id="missions-container"></div>`;
        const missionsContainer = this.querySelector("#missions-container");

        missionsProgresses.forEach(missionProgress => {
            missionsContainer.appendChild(this.getMissionElement(missionProgress));
        })
    }

    getMissionElement(missionProgress) {
        const missionPercentage = missionProgress.currentCount / missionProgress.goal * 100;
        const cardColor = missionProgress.completed ? "bg-success" : "bg-primary" 
        
        const missionContainer = document.createElement("div");
        missionContainer.classList.add("card");
        missionContainer.style = "width: 18rem;";
        missionContainer.innerHTML = `
        <div class="card-body "col-12 col-md-6 col-lg-4">
            <h5 class="card-title">${missionProgress.mission.title}</h5>
            ${missionProgress.completed ? `<span class="badge text-bg-success">Completed</span>` : ``}
            <h6 class="card-subtitle mb-2 text-body-secondary">${missionProgress.mission.description}</h6>
            <p class="card-text">${missionProgress.currentCount} / ${missionProgress.goal}</p>
            <div class="progress" role="progressbar" aria-label="Basic example" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                <div class="progress-bar ${cardColor}" style="width: ${missionPercentage}%"></div>
            </div>
        </div>
        `;

        return missionContainer;
    }
}

customElements.define('missions-viewer', MissionsViewer);