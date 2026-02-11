import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";

export class Quiz extends BaseComponent {
  setupComponent() {
    this.attemptsService = new AttemptsService();
    this.render();
  }

  set quizData(quizData) {
    this._quizData = quizData;
    this.render();
  }

  set userId(userId) {
    this._userId = userId;
    this.render();
  }

  set role(role) {
    this._role = role;
    this.render();
  }

  render() {
    const isTeacher = this._role === "TEACHER";
    const buttonText = isTeacher ? "Edit quiz" : "Run quiz";
    const btnId = `btn-run-${this._quizData.id}`;

    const avgScore = this._quizData.quizStats?.averageScore || 0;
    const totalAttempts = this._quizData.quizStats?.totalAttempts || 0;

    this.innerHTML = `
    <div class="card my-3 shadow-sm" style="border-radius: 8px; border: 1px solid #e0e0e0; background-color: #fcfcfc;">
        <div class="card-body d-flex flex-column align-items-center p-3">
            
            <h3 class="card-title mb-3 fw-normal">
                ${this._quizData.title}
                <span class="text-muted" style="font-size: 0.6em; vertical-align: middle;">#${this._quizData.id}</span>
            </h3>
            
            <span class="badge text-bg-secondary">${this._quizData.difficulty}</span></h6>

            <div class="mb-4 text-center text-muted">
                ${this._quizData.description || "No description available"}
            </div>
            
            <div class="mb-2">
                <button id="${btnId}" class="btn btn-primary px-4">${buttonText}</button>
            </div>
            <div style="border-top: 1px solid rgba(0,0,0,0.1); width: 100%; margin-top: 20px; padding-top: 15px;">
                <div class="text-muted small text-center" style="white-space: nowrap;">
                    Average score: ${Number(avgScore).toFixed(2)} | Total attempts: ${totalAttempts}
                </div>
            </div>

        </div>
    </div>
    `;

    const btn = this.querySelector(`#${btnId}`);
    if (btn) {
        btn.addEventListener("click", () => this.handleQuizButtonClick());
    }
  }

  async handleQuizButtonClick() {
    if (this._role === "TEACHER") {
        globalThis.location = `../quiz-editor/?id=${this._quizData.id}`;
        return;
    } 
    
    let studentId = this.getAttribute("student-id");
    
    if (!studentId) {
        const userJson = localStorage.getItem("user");
        if (userJson) {
            try {
                const user = JSON.parse(userJson);
                studentId = user.id;
            } catch (e) { console.error(e); }
        }
    }

    if (studentId) {
        const cardBody = this.querySelector(".card-body");
        try {
            const sessionData = await this.attemptsService.addAttempt(this._quizData.id, studentId, null);
            if (sessionData.resumed) {
                if (cardBody) {
                    cardBody.insertAdjacentHTML('beforeend', `
                    <alert-component type="danger" message="This quiz has already started! Resume it from the Dashboard." timeout="3000"></alert-component>`);
                }
            } else {
                 if (cardBody) {
                    cardBody.insertAdjacentHTML('beforeend', `
                    <alert-component type="success" message="Quiz started! Good luck." timeout="2000"></alert-component>`);
                }
                this._quizData.status = "STARTED"; 
                this._quizData.attemptStatus = "STARTED";
            }
            localStorage.setItem("currentQuizId", this._quizData.id);
            localStorage.removeItem("currentTestId"); 
            
            this.dispatchEvent(new CustomEvent("attempt-created", { 
                bubbles: true, 
                composed: true 
            }));


        } catch (e) {
            console.error(e);
            let msg = e.message || "Unknown error";
            msg = msg.replace(/^Error:\s*/i, "");
            this.querySelector(".card-body").insertAdjacentHTML('beforeend', `
            <alert-component type="danger" message="${msg}" timeout="3000"></alert-component>`);
        }
    }
  }
}

customElements.define('quiz-item', Quiz);