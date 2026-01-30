import { Quiz } from "./quiz-item.js"

export class QuizzesViewer extends HTMLElement {
  async connectedCallback() {
    this.role = this.getAttribute('role') || "STUDENT";
    this.userId = this.getAttribute('user-id');

    this.render();
    this.loadData();

    document.addEventListener("quiz-created", () => {
        this.loadData();
    })
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
    this.render();
    try {
        const quizzes = await this.getQuizzes();
        if (quizzes.length == 0) {
            let message = "";
            if (this.role == "STUDENT") {
                message = "There isn't any quiz yet! Wait until your teacher will create one!"
            } else if (this.role == "TEACHER") {
                message = "You don't have any quiz yet! Create one to start!";
            }
            this.innerHTML = `
            <div class="alert alert-warning" role="alert">
                ${message}
            </div>
            `
        } else {
            let quizzesHTML = ''
            quizzes.forEach(quiz => {
                quizzesHTML += `
                <quiz-item id="${quiz.id}" title="${quiz.title}" description="${quiz.description}" role="${this.role}" user-id="${this.userId}"></quiz-item>
                `
            });
            this.innerHTML = quizzesHTML;
        }
    } catch (e) {
        console.log(e);
        this.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot get the quizzes list, please try again later
        </div>
        `
    }
  }

  async getQuizzes() {
    let endpoint;
    if (this.role == "STUDENT") {
        endpoint = "http://localhost:8080/api/quizzes";
    } else {
        endpoint = "http://localhost:8080/api/quiz?authorId=" + this.userId;
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