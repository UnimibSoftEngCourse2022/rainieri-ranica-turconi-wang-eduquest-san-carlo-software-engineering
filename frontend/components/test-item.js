import { TestsService } from "../services/tests-service.js";
import { AttemptsService } from "../services/attempts-service.js";
import { BaseComponent } from "./base-component.js";

export class TestItem extends BaseComponent {
  setupComponent() {
    this.testsService = new TestsService();
    this.attemptsService = new AttemptsService();
    this.render();
  }

  set testData(testData) {
    this._testData = testData;
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
    const title = this._testData.quiz ? this._testData.quiz.title : `Test #${this._testData.id}`;
    const btnId = `btn-test-${this._testData.id}`;
    
    let actionButton = "";
    if (this._role === "STUDENT") {
      actionButton = `<button id="${btnId}" class="btn btn-sm btn-primary" data-action="run">Run test</button>`;
    } else {
      actionButton = `<button id="${btnId}" class="btn btn-sm btn-danger" data-action="delete">Delete</button>`;
    }

    this.innerHTML = `
      <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
        <h3>${title}</h3>
        <p>
            <strong>Time Limit:</strong> ${this._testData.maxDuration} min<br>
            <strong>Max Attempts:</strong> ${this._testData.maxTries}
        </p>
        <div class="mt-2">${actionButton}</div>
        <hr>
        <small class="text-muted">
            Average score: ${(this._testData.testAverageScore || 0).toFixed(2)} | 
            Total attempts: ${this._testData.testTotalAttempts || 0}
        </small>
      </div>
    `;

    const btn = this.querySelector(`#${btnId}`);
    if (btn) {
        btn.addEventListener("click", (e) => this.handleButtonClick(e));
    }
  }

  async handleButtonClick(event) {
    const action = event.target.dataset.action;

    if (action === "delete" && this._role === "TEACHER") {
        await this.handleDelete();
    } else if (action === "run" && this._role === "STUDENT") {
        await this.handleRun();
    }
  }

  async handleDelete() {
    if (confirm("Are you sure you want to delete this test?")) {
        const success = await this.testsService.deleteTest(this._testData.id);
        if (success) {
            this.dispatchEvent(new CustomEvent("test-deleted", { bubbles: true, composed: true }));
        }
    }
  }

  async handleRun() {
    const studentId = this.getStudentId();

    if (studentId) {
        try {
            await this.attemptsService.addAttempt(this._testData.quiz.id, studentId, this._testData.id);
            
            this.dispatchEvent(new CustomEvent("attempt-created", { 
                bubbles: true, 
                composed: true 
            }));

        } catch (e) {
            console.error(e);
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