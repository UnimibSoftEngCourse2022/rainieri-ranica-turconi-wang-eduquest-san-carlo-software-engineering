export class AddQuiz extends HTMLElement {
  connectedCallback() {
    this.renderInitialStructure();
    document.getElementById("add-quiz-button").addEventListener("click", (e) => this.handleAddQuiz(e));
  }

  renderInitialStructure() {
    this.innerHTML = `
    <div class="mb-3">
        <label for="quiz-title-input" class="form-label">
            Name
        </label>
        <input
            type="text"
            class="form-control"
            id="quiz-title-input"
        />
    </div>
    <div class="mb-3">
        <label for="quiz-description-input" class="form-label">
            Description
        </label>
        <input
            type="text"
            class="form-control"
            id="quiz-description-input"
        />
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" id="add-quiz-button">Add</button>
    </div>
    <div id="add-quiz-result" class="container"></div>
    `;
  }

  async handleAddQuiz(event) {
    const jwt = window.localStorage.getItem("token");
    const title = document.getElementById("quiz-title-input").value;
    const description = document.getElementById("quiz-description-input").value;
    
    const requestBody = {
        title, description
    };
    const response = await fetch("http://localhost:8080/api/quiz", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        },
        body: JSON.stringify(requestBody)
    });

    const resultDiv = document.getElementById("add-quiz-result")
    if (response.ok) {
        const r = await response.json();
        resultDiv.innerHTML = `
        <div class="alert alert-success" role="alert">
            Quiz created successfully
        </div>
        `

        this.dispatchEvent(new CustomEvent("quiz-created", {
            bubbles: true,   // Permette all'evento di risalire fino al body/document
            composed: true   // Permette all'evento di attraversare lo Shadow DOM
        }))
    } else {
        resultDiv.innerHTML = `
        <div class="alert alert-warning" role="alert">
            Error during the quiz creation
        </div>
        `
    }
  }
}

customElements.define('add-quiz', AddQuiz);