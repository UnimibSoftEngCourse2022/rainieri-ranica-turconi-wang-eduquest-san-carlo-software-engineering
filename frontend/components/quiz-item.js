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

    this.innerHTML = `
    <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
        <h3>${this._quizData.title}</h3>
        <p>${this._quizData.description}</p>
        
        <button id="${btnId}" class="btn btn-sm btn-primary">${buttonText}</button>
        
        <hr>
        <small class="text-muted">
            Average score: ${(this._quizData.quizStats?.averageScore || 0).toFixed(2)} | 
            Total attempts: ${this._quizData.quizStats?.totalAttempts || 0}
        </small>
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
        try {
            await this.attemptsService.addAttempt(this._quizData.id, studentId, null);
            
            this.dispatchEvent(new CustomEvent("attempt-created", { 
                bubbles: true, 
                composed: true 
            }));

        } catch (e) {
            console.error(e);
        }
    }
  }
}

customElements.define('quiz-item', Quiz);