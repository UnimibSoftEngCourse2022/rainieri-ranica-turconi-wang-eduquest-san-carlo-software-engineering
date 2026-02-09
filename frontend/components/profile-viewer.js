import { BaseComponent } from "./base-component.js";
import { roundWithTwoDecimals } from "../js/utils.js"
import { GamificationService } from "../services/gamification-service.js";

export class UserSearch extends BaseComponent {
    setupComponent() {
        this._userData = null;
        this.gamificationService = new GamificationService();
        this.render();
    }

    set userData(userData) {
        this._userData = userData;
        this.render();
    }

    async render() {
        if (this._userData) {
            this.innerHTML = `
            <div class="text-center">
                <h1>User info</h1><div id="user-table"></div>
                <h4>Stats</h4><div id="stats-table"></div>
                <h4>Badges</h4><div id="badges" class="row g-4 justify-content-center"></div>
            </div>
            `;
        } else {
            this.innerHTML = `<div class="text-center my-5"><loading-spinner></loading-spinner></div>`;
            return;
        }
        this.loadData();
    }

    async loadData() {
        this.loadUserTable();
        this.loadStatsTable();
        this.loadMissionsTable();
    }

    loadUserTable() {
        const userTable = this.querySelector("#user-table");
        userTable.innerHTML = `
        <table class="table">
            <tbody>
                <tr>
                    <th scope="row">ID</th>
                    <td>${this._userData.id}</td>
                </tr>
                <tr>
                    <th scope="row">Name</th>
                    <td>${this._userData.name}</td>
                </tr>
                <tr>
                    <th scope="row">Surname</th>
                    <td>${this._userData.surname}</td>
                </tr>
                <tr>
                    <th scope="row">Email</th>
                    <td>${this._userData.email}</td>
                </tr>
                <tr>
                    <th scope="row">Role</th>
                    <td>${this._userData.role}</td>
                </tr>
            </tbody>
        </table>
        `;
    }

    loadStatsTable() {
        const statsTable = this.querySelector("#stats-table");
        if (this._userData.role == "STUDENT") {
            if (this._userData?.studentStats?.quizzesCompleted) {
                statsTable.innerHTML = `
                <table class="table">
                    <tbody>
                        <tr>
                            <th scope="row">Completed quizzes</th>
                            <td>${this._userData.studentStats.quizzesCompleted}</td>
                        </tr>
                        <tr>
                            <th scope="row">Average quizzes score</th>
                            <td>${roundWithTwoDecimals(this._userData.studentStats.averageQuizzesScore)}</td>
                        </tr>
                        <tr>
                            <th scope="row">Number of answers given</th>
                            <td>${this._userData.studentStats.totalAnswerGiven}</td>
                        </tr>
                        <tr>
                            <th scope="row">Percentage of correct answers</th>
                            <td>${Math.round(this._userData.studentStats.totalCorrectAnswers / this._userData.studentStats.totalAnswerGiven * 100)}%</td>
                        </tr>
                    </tbody>
                </table>
                `;
            } else {
                statsTable.innerHTML = `
                <alert-component type="warning" message="This user hasn't completed a quiz yet!"></alert-component>
                `
            }
        } else {
            statsTable.innerHTML = `
            <alert-component type="warning" message="This user is a teacher"></alert-component>
            `;
        }
    }

    async loadMissionsTable() {
        const badgesContainer = this.querySelector("#badges");
        badgesContainer.innerHTML = "";
        if (this._userData.role === "STUDENT") {
            let badges = [];
            try {
                badges = await this.gamificationService.getUserBadges(this._userData.id);
            } catch {
                badgesContainer.innerHTML = `
                <alert-component type="danger" message="Error loading badges"></alert-component>
                `;
            }

            if (!badges || badges.length === 0) {
                badgesContainer.innerHTML = `
                <alert-component type="warning" message="This user hasn't any badge"></alert-component>
                `;
            }

            badges.forEach(badge => {
                const badgeCard = document.createElement("div");
                badgeCard.classList.add("card", "mb-3", "me-3");
                badgeCard.style = "width: 18rem;";
                badgeCard.style.display = "inline-block";
                let dateStr;
                if (badge.obtainedDate) {
                    dateStr = new Date(badge.obtainedDate).toLocaleDateString();
                } else {
                    dateStr = 'Unknown date';
                }
                badgeCard.innerHTML = `
                <div class="card-body text-center">
                    <div style="font-size: 2.5rem; margin-bottom: 10px;">🏆</div>                  
                    <h5 class="card-title fw-bold" style="color: #b38f00;">
                        ${badge.name} 
                    </h5>
                    <p class="card-text small text-muted">
                        ${badge.description}
                    </p>
                    <div class="border-top pt-2 mt-3">
                        <small class="text-muted" style="font-style: italic;">
                            Earned on: ${dateStr}
                        </small>
                    </div>
                </div>
                `;
                badgesContainer.appendChild(badgeCard);
            });
        } else {
            badgesContainer.innerHTML = `
            <alert-component type="warning" message="This user is a teacher"></alert-component>
            `;
        }
    }
}

customElements.define('profile-viewer', UserSearch);