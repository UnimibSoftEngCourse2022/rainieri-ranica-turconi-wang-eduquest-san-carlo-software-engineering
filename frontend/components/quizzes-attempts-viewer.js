export class QuizzesAttemptsViewer extends HTMLElement {
  connectedCallback() {
    this.userId = this.getAttribute("user-id");
    this.render();
    this.loadData();

    document.addEventListener("quiz-attempt-started", () => this.loadData());
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
    const response = await fetch(`http://localhost:8080/api/quiz-attempts?studentId=${this.userId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const quizzesAttempts = await response.json();
        if (quizzesAttempts.length == 0) {
          this.quizzesAttempts.innerHTML = `
          <div class="alert alert-warning" role="alert">
            You haven't started a quiz yet!
          </div>
          `
        } else {
          let quizzesAttemptsHTML = `<div class="list-group">`
          quizzesAttempts.forEach(quizAttempt => {
              quizzesAttemptsHTML += `
              <a class="list-group-item list-group" href="../quiz-runner/?quizAttemptId=${quizAttempt.id}">${quizAttempt.quizTitle}</a>
              `
          })
          quizzesAttemptsHTML += `</div>`
          this.quizzesAttempts.innerHTML = quizzesAttemptsHTML;
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