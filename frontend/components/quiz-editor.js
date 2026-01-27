export class QuizEditor extends HTMLElement {
  connectedCallback() {
    this.quizId = this.getAttribute("id");
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
        </form>
    </div>
    `;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");
    const quizInfoEndpoint = "http://localhost:8080/api/quiz/"+this.quizId;
    const response = await fetch(quizInfoEndpoint, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const quizData = await response.json();
        
        document.getElementById("title-input").value = quizData.title;
        document.getElementById("description-input").value = quizData.description;

        if (quizData.questions.length == 0) {
            this.innerHTML += `
            <div class="alert alert-warning" role="alert">
                This quiz doesn't have any question yet!
            </div>
            `;
        } else {
            this.innerHTML += `<div class="list-group">`;
            quizData.questions.forEach(question => {
                this.innerHTML += `
                <a href="#" class="list-group-item list-group-item-action">${question.text}</a>
                `
            });
            this.innerHTML += `</div>`;
        }
    } else {
        // TODO show an error
        console.log("error");
    }
  }
}

customElements.define('quiz-editor', QuizEditor);