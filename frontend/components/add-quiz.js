export class AddQuiz extends HTMLElement {
  connectedCallback() {
    this.render();
    document.getElementById("add-quiz-button").addEventListener("click", (e) => this.handleAddQuiz(e));
  }

  get quizTitle() {
    return this.querySelector("#quiz-title-input");
  }

  get quizDescription() {
    return this.querySelector("#quiz-description-input");
  }

  get addQuizResult() {
    return document.getElementById("add-quiz-result");
  }

  render() {
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
    const title = this.quizTitle.value;
    const description = this.quizDescription.value;
    
    const requestBody = {
        title, description
    };
    
    this.submitData(requestBody);
  }

  async submitData(requestBody) {
        const jwt = window.localStorage.getItem("token");    
        const response = await fetch("http://localhost:8080/api/quiz", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        },
        body: JSON.stringify(requestBody)
    });

    if (response.ok) {
        const r = await response.json();
        this.addQuizResult.innerHTML = `
        <div class="alert alert-success" role="alert">
            Quiz created successfully
        </div>
        `

        this.dispatchEvent(new CustomEvent("quiz-created", {
            bubbles: true,
            composed: true
        }))
    } else {
        this.addQuizResult.innerHTML = `
        <div class="alert alert-warning" role="alert">
            Error during the quiz creation
        </div>
        `
    }
  }
}

customElements.define('add-quiz', AddQuiz);