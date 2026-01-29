export class QuizRunner extends HTMLElement {
  connectedCallback() {
    this.quizAttemptId = this.getAttribute("quiz-attempt-id");
    this.render();
    this.loadData();
  }

  get quizTitle() {
    return this.querySelector("#quiz-title");
  }

  get questionRunnerSpace() {
    return this.querySelector("#question-runner");
  }

  render() {
    this.innerHTML = `
    <div class="card text-center">
        <div class="card-header" style="padding: 20px">
            <h4 id="quiz-title"></h4>
        </div>
        <div class="card-body" id="question-runner">
        </div>
    </div>
    `;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");
    const response = await fetch(`http://localhost:8080/api/quiz/${this.quizAttemptId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const quizData = await response.json();
        console.log(quizData);
    } else {
        this.questionRunnerSpace.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot load the test, please try again later
        </div>
        `
    }
  }
}

customElements.define('quiz-runner', QuizRunner);