import { TestsService } from "../services/tests-service.js";
import { BaseComponent } from "./base-component.js";
import { TestItem } from "./test-item.js"; 
import { Alert } from "./shared/alert.js";

export class TestsViewer extends BaseComponent {
  setupComponent() {
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');
    this.testsService = new TestsService();
    this.allTests = [];

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

    const searchInput = this.querySelector("#search-input");
    if (searchInput) {
        searchInput.addEventListener("input", (e) => {
            const searchTerm = e.target.value.toLowerCase();
            const filteredTests = this.allTests.filter(test => 
                test.title.toLowerCase().includes(searchTerm)
            );
            this.displayTests(filteredTests);
        });
    }
  }

  render() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <div class="row justify-content-center mb-4">
            <div class="col-md-6">
                <input type="text" id="search-input" class="form-control" placeholder="Test Title">
            </div>
        </div>
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
        <div id="message-container"></div>
        <div class="row g-4" id="tests-container"></div>
    </div>
    `;
  }

  async loadData() {
    const loader = this.querySelector(".spinner-border");
    const messageContainer = this.querySelector("#message-container");
    try {
        const tests = await this.testsService.getTestsByAuthorId(this.userId);
        
        if (!tests || tests.length == 0) {
            //this.innerHTML = `<alert-component type="warning" message="There are no active tests to display"></alert-component>`
            //messageContainer.innerHTML = `<alert-component type="warning" message="There are no active tests to display"></alert-component>`
            if (messageContainer) {
                messageContainer.innerHTML = `<alert-component type="warning" message="There are no active tests to display"></alert-component>`;
            }
        } else {
            /*this.innerHTML = `<div class="row g-4" id="tests-inner-container"></div>`;
            const testsContainer = this.querySelector("#tests-inner-container");
            
            tests.forEach(test => {
              const testItem = document.createElement("test-item");
              testItem.classList.add("col-12", "col-md-6", "col-lg-4");

              testItem.testData = test;
              testItem.role = this.role;
              testItem.userId = this.userId;

              testsContainer.appendChild(testItem);
            })*/
           this.displayTests(this.allTests);
        }
    } catch (e) {
      console.log(e);
        this.innerHTML = `<alert-component type="danger" message="Cannot get the tests list, please try again later"></alert-component>`
    }
  }

  displayTests(testsList) {
      const container = this.querySelector("#tests-container");
      const messageContainer = this.querySelector("#message-container");
      container.innerHTML = "";
      messageContainer.innerHTML = "";

      if (testsList.length === 0) {
          messageContainer.innerHTML = `<div class="alert alert-light">No tests found matching your search.</div>`;
          return;
      }
      testsList.forEach(test => {
          const testItem = document.createElement("test-item");
          testItem.classList.add("col-12", "col-md-6", "col-lg-4");
          testItem.testData = test;
          testItem.role = this.role;
          testItem.userId = this.userId;
          container.appendChild(testItem);
      });
  }
}

customElements.define('tests-viewer', TestsViewer);