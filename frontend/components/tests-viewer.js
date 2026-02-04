import { TestsService } from "../services/tests-service.js";
import { BaseComponent } from "./base-component.js";
import { TestItem } from "./test-item.js"; 
import { Alert } from "./shared/alert.js";

export class TestsViewer extends BaseComponent {
  setupComponent() {
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');
    this.testsService = new TestsService();

    this.render();
    this.loadData();
  }

  attachEventListeners() {
    document.addEventListener("test-created", () => {
      this.loadData();
    });
    
    this.addEventListener("test-deleted", () => {
        this.loadData();
    });
  }

  render() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>
    `;
  }

  async loadData() {
    try {
        const tests = await this.testsService.getTests();
        
        if (!tests || tests.length == 0) {
            this.innerHTML = `<alert-component type="warning" message="There are no active tests to display"></alert-component>`
        } else {
            this.innerHTML = `<div class="row g-4" id="tests-inner-container"></div>`;
            const testsContainer = this.querySelector("#tests-inner-container");
            
            tests.forEach(test => {
              const testItem = document.createElement("test-item");
              testItem.classList.add("col-12", "col-md-6", "col-lg-4");

              testItem.testData = test;
              testItem.role = this.role;
              testItem.userId = this.userId;

              testsContainer.appendChild(testItem);
            })
        }
    } catch (e) {
      console.log(e);
        this.innerHTML = `<alert-component type="danger" message="Cannot get the tests list, please try again later"></alert-component>`
    }
  }
}

customElements.define('tests-viewer', TestsViewer);