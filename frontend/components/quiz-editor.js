export class QuizEditor extends HTMLElement {
  connectedCallback() {
    const quizId = this.getAttribute("id");
    this.renderInitialStructure();
    this.loadData();
  }

  renderInitialStructure() {
    this.innerHTML = `
    <div class="container my-5 text-center">
        <h1>Quiz editor</h1>
        <form id="quiz-editor-general-info-form">
            <div class="mb-3">
                <label for="title-input" class="form-label">
                    Title
                </label>
                <input
                    type="text"
                    class="form-control"
                    id="title-input"
                    aria-describedby="emailHelp"
                />
            </div>
            <div class="mb-3">
                <label for="description-input" class="form-label">
                    Description
                </label>
                <input
                    type="text"
                    class="form-control"
                    id="description-input"
                />
            </div>
            <button type="submit" class="btn btn-primary">Save changes</button>
        </form>
    </div>
    `;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");
    const response = await fetch("http://localhost:8080/api/quiz", {
        method: "PUT",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });
  }
}

customElements.define('quiz-editor', QuizEditor);