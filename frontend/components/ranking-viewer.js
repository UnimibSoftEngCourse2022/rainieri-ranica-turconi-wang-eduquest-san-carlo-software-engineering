import { BaseComponent } from "./base-component.js";
import { GamificationService } from "../services/gamification-service.js"
import "./shared/alert.js";

export class RankingViewer extends BaseComponent {
    setupComponent() {
      this.gamificationService = new GamificationService();
      this.render();
      this.loadData();
    }

    set rankingType(rankingType) {
        this._rankingType = rankingType;
        this.loadData();
    }

    async render() {
      this.innerHTML = `
      <h4 id="ranking-title"></h4>
      <table class="table">
        <tbody id="ranking-table">
        </tbody>
      </table>
      `
    }

    async loadData() {
      let ranking = [];
      let rankingTitleHTML;

      const rankingTable = this.querySelector("#ranking-table");
      const rankingTitle = this.querySelector("#ranking-title");

      if (this._rankingType === "quizzes-number") {
        try {
          ranking = await this.gamificationService.getRankingByCompletedQuizzes();
          rankingTitleHTML = `<h4>Number of completed quizzes</h4>`;
        } catch {
          rankingTable.innerHTML = `
          <alert-component type="danger" message="Error getting ranking"></alert-component>
          `;
        }
      } else if (this._rankingType === "average-score") {
        try {
          ranking = await this.gamificationService.getRankingByAverageScore();
        } catch {
          rankingTable.innerHTML = `
          <alert-component type="danger" message="Error getting ranking"></alert-component>
          `;
        }
        rankingTitleHTML = `<h4>Average score</h4>`;
      } else {
        rankingTitle.innerHTML = ``;
        rankingTable.innerHTML = ``;
        return;
      }

      rankingTable.innerHTML = `
      <th>Position</th>
      <th>Name and Surname</th>
      <th>Value</th>
      `;

      rankingTitle.innerHTML = rankingTitleHTML;

      let currentPosition = 1;
      ranking.forEach(studentData => {
        const studentElement = document.createElement("tr");
        studentElement.innerHTML = `
            <th scope="row">${currentPosition}</th>
            <td scope="row">${studentData.name} ${studentData.surname}</td>
            <td scope="row">${studentData.value}</td>
        `
        rankingTable.appendChild(studentElement);
        currentPosition++;
      });
    }
}
customElements.define('ranking-viewer', RankingViewer);