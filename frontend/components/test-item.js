import { TestsService } from "../services/tests-service.js";
import { BaseComponent } from "./base-component.js";

export class TestItem extends BaseComponent {
  setupComponent() {
    this.testsService = new TestsService();
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
    const title = this._testData.quizTitle || `Test #${this._testData.id}`;
    
    let actionButton = "";
    
    if (this._role === "STUDENT") {
      actionButton = `
        <button class="btn btn-sm btn-primary test-action-btn" data-action="run">Run test</button>
      `;
    } else if (this._role === "TEACHER") {
      actionButton = `
        <button class="btn btn-sm btn-danger test-action-btn" data-action="delete">Delete</button>
      `;
    }

    this.innerHTML = `
      <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
        <h3>${title}</h3>
        
        <div class="mb-2 text-muted">
            <small>Configured for Quiz ID: ${this._testData.quizId}</small>
        </div>

        <p>
            <strong>Time Limit:</strong> ${this._testData.timeLimit} min<br>
            <strong>Max Attempts:</strong> ${this._testData.maxAttempts}
        </p>

        <div class="mt-2">
            ${actionButton}
        </div>
    </div>
    `;
  }

  attachEventListeners() {
    this.addEventListenerWithTracking(".test-action-btn", "click", (event) => this.handleButtonClick(event));
  }

  async handleButtonClick(event) {
    const action = event.target.dataset.action;

    if (action === "delete" && this._role === "TEACHER") {
      if (confirm("Are you sure you want to delete this test?")) {
          const success = await this.testsService.deleteTest(this._testData.id);
          if (success) {
              this.dispatchEvent(new CustomEvent("test-deleted", {
                  bubbles: true,
                  composed: true
              }));
          } else {
              alert("Error deleting test");
          }
      }
    } else if (action === "run" && this._role === "STUDENT") {
      console.log("Starting test attempt for test ID:", this._testData.id);
    }
  }
}

customElements.define('test-item', TestItem);