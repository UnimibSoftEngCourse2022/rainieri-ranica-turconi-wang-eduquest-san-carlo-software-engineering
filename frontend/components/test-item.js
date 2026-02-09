import { TestsService } from "../services/tests-service.js";
import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";

export class TestItem extends BaseComponent {
  setupComponent() {
    this.testsService = new TestsService();
    this.attemptsService = new AttemptsService();
    
    if (this._testData) {
        this.updateStats();
    }
  }

  set testData(testData) {
    this._testData = testData;
    this.render();
    this.updateStats(); 
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
    if (!this._testData) return;

    const quizTitle = this._testData.quiz ? this._testData.quiz.title : "Test";
    
    const btnId = `btn-test-${this._testData.id}`;
    const statsId = `stats-test-${this._testData.id}`; 
    
    const isTeacher = this._role === "TEACHER";
    const buttonText = isTeacher ? "Delete" : "Run test";
    const btnClass = isTeacher ? "btn-danger" : "btn-primary";
    
    let statsHtml = "";
    if (!isTeacher) {
        statsHtml = `
            <div style="border-top: 1px solid rgba(0,0,0,0.1); width: 100%; margin-top: 20px; padding-top: 15px;">
                <div id="${statsId}" class="text-muted small text-center" style="white-space: nowrap;">
                    Average score: 0.00 | Total attempts: 0
                </div>
            </div>
        `;
    }

    this.innerHTML = `
      <div class="card my-3 shadow-sm" style="border-radius: 8px; border: 1px solid #e0e0e0; background-color: #fcfcfc;">
        <div class="card-body d-flex flex-column align-items-center p-3">
            
            <h3 class="card-title mb-3 fw-normal">${quizTitle}</h3>
            
            <div class="mb-4 text-center text-dark">
                <div class="mb-1"><strong>Time Limit:</strong> ${this._testData.maxDuration} min</div>
                <div><strong>Max Attempts:</strong> ${this._testData.maxTries}</div>
            </div>
            
            <div class="mb-2">
                <button id="${btnId}" class="btn ${btnClass} px-4">${buttonText}</button>
            </div>

            ${statsHtml}
        </div>
      </div>
    `;

    const btn = this.querySelector(`#${btnId}`);
    if (btn) {
        btn.addEventListener("click", (e) => this.handleButtonClick(e));
    }
  }

  async updateStats() {
      if (this._role === "TEACHER") return;
      if (!this.testsService) return; 

      try {
          const myAttempts = await this.testsService.getMyAttempts(this._testData.id);
          
          if (myAttempts && Array.isArray(myAttempts) && myAttempts.length > 0) {
              
              const count = myAttempts.length;
              const sumScore = myAttempts.reduce((acc, curr) => acc + (curr.score || 0), 0);
              const avg = sumScore / count;

              const statsEl = this.querySelector(`#stats-test-${this._testData.id}`);
              if (statsEl) {
                  statsEl.innerHTML = `Average score: ${avg.toFixed(2)} | Total attempts: ${count}`;
                  }
          }
      } catch (e) {
          console.error(e);
      }
  }

  async handleButtonClick(event) {
    if (this._role === "TEACHER") {
        if (confirm("Are you sure you want to delete this test?")) {
            try {
                await this.testsService.deleteTest(this._testData.id);
                this.dispatchEvent(new CustomEvent("test-deleted", { bubbles: true, composed: true }));
            } catch (e) {
                console.error(e);
            }
        }
    } else {
        const studentId = this.getStudentId();
        if (studentId) {
            const cardBody = this.querySelector(".card-body");
            const oldAlerts = this.querySelectorAll("alert-component");
            oldAlerts.forEach(a => a.remove());
            try {
                const sessionData = await this.attemptsService.addAttempt(this._testData.quiz.id, studentId, this._testData.id);
                if (sessionData.testId && sessionData.testId !== this._testData.id) {
                     throw new Error("You have another Test/Quiz in progress.");
                }
                const isResuming = 
                    (this._testData.attemptStatus === "STARTED") || 
                    (this._testData.status === "STARTED") ||
                    (sessionData.existingAnswers && sessionData.existingAnswers.length > 0);
                if (isResuming) {
                    if (cardBody) {
                        cardBody.insertAdjacentHTML('beforeend', `
                        <alert-component type="danger" message="This test has already started! Resume it from the Dashboard." timeout="3000"></alert-component>`);
                    }
                } else {
                    if (cardBody) {
                        cardBody.insertAdjacentHTML('beforeend', `
                        <alert-component type="success" message="Test started! Good luck." timeout="2000"></alert-component>`);
                    }
                    this._testData.status = "STARTED";
                    this._testData.attemptStatus = "STARTED";
                }
                localStorage.setItem("currentQuizId", this._testData.quiz.id);
                localStorage.setItem("currentTestId", this._testData.id);
                
                this.dispatchEvent(new CustomEvent("attempt-created", { 
                bubbles: true, 
                composed: true 
                }));
            } catch (e) {
                console.error(e);
                let msg = e.message || "Unknown error";
                msg = msg.replace(/^Error:\s*/i, "");
                if (cardBody) {
                    this.querySelector(".card-body").insertAdjacentHTML('beforeend', `
                    <alert-component type="danger" message="${e.message}" timeout="3000"></alert-component>`);
                }
            }
        }
    }
  }

  getStudentId() {
      let studentId = this.getAttribute("student-id");
      if (!studentId) {
          const userJson = localStorage.getItem("user");
          if (userJson) {
              try {
                  const user = JSON.parse(userJson);
                  return user.id;
              } catch (e) { console.error(e); }
          }
      }
      return studentId;
  }
}

customElements.define('test-item', TestItem);