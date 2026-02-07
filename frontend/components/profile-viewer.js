import { UsersService } from "../services/users-service.js";
import { BaseComponent } from "./base-component.js";
import { roundWithTwoDecimals } from "../js/utils.js"

export class UserSearch extends BaseComponent {
    setupComponent() {
        this._userData = null;
        this.render();
    }

    set userData(userData) {
        this._userData = userData;
        this.render();
    }

    render() {
        if (!this._userData) {
            this.innerHTML = `<div class="text-center my-5"><loading-spinner></loading-spinner></div>`
        } else {
            this.innerHTML = this.getUserTable();
        }
    }

    getUserTable() {
        const generalTable = `
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
        `

        let statsTable = ``;
        if (this._userData.role == "STUDENT") {
            statsTable = (this._userData.studentStats && this._userData.studentStats.quizzesCompleted) ? `
            <h4>Stats</h4>
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
            ` : `
            <alert-component type="warning" message="This user hasn't completed a quiz yet!"></alert-component>
            `
        }

        return `
        <div class="text-center my-5">
            <h1>${this._userData.name} ${this._userData.surname}</h1>
            ${generalTable}
            ${statsTable}
        `;
    }
}

customElements.define('profile-viewer', UserSearch);