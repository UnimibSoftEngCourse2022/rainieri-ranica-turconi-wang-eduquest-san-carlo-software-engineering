import { UsersService } from "../services/users-service.js";
import { BaseComponent } from "./base-component.js";
import { MissionsService } from "../services/missions-service.js";
import { Alert } from "./shared/alert.js";

export class MissionsViewer extends BaseComponent {
    setupComponent() {
        this.missionsService = new MissionsService();
        this.render();
    }

    async render() {
        const missionsProgresses = await this.missionsService.getUserMissionsProgresses();
        if (missionsProgresses.length == 0) {
            this.innerHTML = `<alert-component type="warning" message="Complete your first quiz to see the missions!"></alert-component>`
            return;
        }
        missionsProgresses.forEach(missionProgress => {
            this.innerHTML += this.getMissionHTML(missionProgress);
        })
    }

    getMissionHTML(missionProgress) {
        const missionPercentage = missionProgress.currentCount / missionProgress.goal * 100;
        const cardColor = missionProgress.completed ? "bg-success" : "bg-primary" 
        return `
        <div class="card" style="width: 18rem;">
            <div class="card-body">
                <h5 class="card-title">${missionProgress.mission.title}</h5>
                ${missionProgress.completed ? `<span class="badge text-bg-success">Completed</span>` : ``}
                <h6 class="card-subtitle mb-2 text-body-secondary">${missionProgress.mission.description}</h6>
                <p class="card-text">${missionProgress.currentCount} / ${missionProgress.goal}</p>
                <div class="progress" role="progressbar" aria-label="Basic example" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                    <div class="progress-bar ${cardColor}" style="width: ${missionPercentage}%"></div>
                </div>
            </div>
        </div>
        `
    }
}

customElements.define('missions-viewer', MissionsViewer);