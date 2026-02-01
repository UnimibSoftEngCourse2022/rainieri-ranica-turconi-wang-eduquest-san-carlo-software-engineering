import { QuizService } from "../services/quiz-service.js";
import { QuestionsViewer } from "./questions-viewer.js";
import { Alert } from "./shared/alert.js";

export class QuizEditor extends HTMLElement {
  connectedCallback() {
    this.quizId = this.getAttribute("id");
    this.quizService = new QuizService();

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
    try {
        const quizData = await this.quizService.getQuizById(this.quizId);
        
        this.quizTitle.value = quizData.title;
        this.quizDescription.value = quizData.description;
    
        if (quizData.questions.length == 0) {
            this.quizQuestions.innerHTML = `
            <alert-component type="info" message="No questions added to the quiz yet"></alert-component>
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
    } catch (e) {
        console.log(e);
        this.innerHTML = `
        <alert-component type="danger" message="Error trying to show the quiz, please try again later"></alert-component>
        `
    }    
  }

  async removeQuestionFromQuiz(questionId) {
    const response = await this.quizService.removeQuestionFromQuiz(this.quizId, questionId);

    if (response) {
        this.loadData();
    }
  }
}

customElements.define('quiz-editor', QuizEditor);