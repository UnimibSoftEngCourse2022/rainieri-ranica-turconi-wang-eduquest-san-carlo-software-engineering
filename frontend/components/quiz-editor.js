import { QuestionsViewer } from "./questions-viewer.js";

export class QuizEditor extends HTMLElement {
  connectedCallback() {
    this.quizId = this.getAttribute("id");
    this.render();
    this.loadData();

    this.addEventListener("question-added-to-quiz", () => this.loadData());
  }

  get quizTitle() {
    return this.querySelector("#title-input");
  }

  get quizDescription() {
    return this.querySelector("#description-input");
  }

  get quizQuestions() {
    return this.querySelector("#questions");
  }

  render() {
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
        <div id="questions"></div>
        <hr>
        <h3>Add a question</h3>
        <questions-viewer role="TEACHER" quizId="${this.quizId}"></questions-viewer>
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
        
        this.quizTitle.value = quizData.title;
        this.quizDescription.value = quizData.description;

        if (quizData.questions.length == 0) {
            this.quizQuestions.innerHTML = `
            <div class="alert alert-warning" role="alert">
                This quiz doesn't have any question yet!
            </div>
            `;
        } else {
            let questionsDiv = `
            <h3>Quiz questions</h3>
            <div class="list-group">
            `;
            quizData.questions.forEach(question => {
                questionsDiv += `
                <a class="list-group-item list-group">${question.text}<button class="btn remove-question-from-quiz-button" data-id="${question.id}">🗑️</button></a>
                `
            });
            questionsDiv += `</div>`;
            this.quizQuestions.innerHTML = questionsDiv;
            this.quizQuestions.querySelectorAll(".remove-question-from-quiz-button").forEach(button => {
                button.addEventListener("click", (event) => {
                    const questionId = event.target.getAttribute("data-id");
                    this.removeQuestionFromQuiz(questionId);
                })
            })
        }
    } else {
        this.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Error trying to show the quiz, please try again later
        </div>
        `
    }
  }

  async removeQuestionFromQuiz(questionId) {
    const jwt = window.localStorage.getItem("token");
    const removeQuestionEndpoint = `http://localhost:8080/api/quiz/${this.quizId}/remove-question/${questionId}`;
    const response = await fetch(removeQuestionEndpoint, {
        method: "DELETE",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        this.loadData();
    }
  }
}

customElements.define('quiz-editor', QuizEditor);