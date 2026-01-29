export class QuizzesAttemptsViewer extends HTMLElement {
  connectedCallback() {
    this.userId = this.getAttribute("user-id");
    this.render();
    this.loadData();

    this.addEventListener("quiz-attempt-started", () => this.loadData());
  }

  get quizzesAttempts() {
    return this.querySelector("#quizzes-attempts");
  }

  render() {
    this.innerHTML = `
    <div id="quizzes-attempts" class="container"></div>
    `;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");
    const response = await fetch(`http://localhost:8080/api/quizAttempt?studentId=${this.userId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const quizzesAttempts = response.json();
        if (quizzesAttempts.length == 0) {
            quizzesAttempts.forEach(quizAttempt => {
                console.log(quizAttempt);
            })
        } else {
            this.quizzesAttempts.innerHTML = `
            <div class="alert alert-warning" role="alert">
              You haven't started a quiz yet!
            </div>
            `
        }
    } else {
        this.quizzesAttempts.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Error trying to access the quizzes attempts, please try again later
        </div>
        `
    }
  };
}

customElements.define('quizzes-attempts-viewer', QuizzesAttemptsViewer);