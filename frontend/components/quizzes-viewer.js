export class QuizzesViewer extends HTMLElement {
  async connectedCallback() {
    const userId = this.getAttribute('userId')
    const role = this.getAttribute('role') || "STUDENT";

    this.innerHTML = `
    <div class="container my-5 text-center">
        <div class="spinner-border" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>
    `;

    try {
        const quizzes = await this.getQuizzes(userId);
        quizzes.forEach(quiz => {
            this.innerHTML += `
            <quiz-item title=${quiz.title} description=${quiz.description} role="TEACHER"></quiz-item>
            `
        });
    } catch (e) {
        this.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot get the quizzes list, please try again later
        </div>
        `
        console.log(e)
    }
  }

  async getQuizzes(userId) {
    let endpoint;
    if (userId == null) {
        endpoint = "http://localhost:8080/api/quiz";
    } else {
        endpoint = "http://localhost:8080/api/quiz?authorId=" + userId;
    }

    const jwt = window.localStorage.getItem("token");
    const response = await fetch(endpoint, {
    method: "GET",
    headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Authorization": "Bearer " + jwt
    }
    });

    if (response.ok) {
        const quizzes = await response.json();
        return quizzes;
    } else {
        return [];
    }
  }
}
customElements.define('quizzes-viewer', QuizzesViewer);