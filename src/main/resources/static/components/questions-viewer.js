import { QuestionsService } from "../services/questions-service.js";
import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import "./shared/collapsible-panel.js";

export class QuestionsViewer extends BaseComponent {
  setupComponent() {
    this.quizId = this.getAttribute("quizId");
    this.authorId = this.getAttribute("authorId");
    this.userRole = this.getAttribute("userRole");
    this.allQuestions = [];

    this.questionsService = new QuestionsService();
    this.quizService = new QuizService();
    this.render();
    this.loadData();
  }

  attachEventListeners() {
    document.addEventListener("question-added", () => {
      this.loadData();
    });

    const searchInput = this.querySelector("#search-input");
    if (searchInput) {
        searchInput.addEventListener("input", (e) => {
            const searchTerm = e.target.value.toLowerCase();
            const filteredQuestions = this.allQuestions.filter(question => 
                question.text.toLowerCase().includes(searchTerm)
            );
            this.showQuestions(filteredQuestions);
        });
    }
  }

  get questions() {
    return this.querySelector("#questions");
  }

  render() {
    this.innerHTML = `
    <collapsible-panel title=" " open>
          <div class="row mb-3">
              <div class="col-md-6 mx-auto">
                 <input type="text" id="search-input" class="form-control" placeholder="Search question...">
              </div>
          </div>
          <div id="questions" class="row g-4"></div>
    </collapsible-panel>
    `;
  }

  async loadData() {
    try {
      let questions = null;
      if (this.authorId) {
        questions = await this.questionsService.getQuestionByAuthorId(this.authorId);
      } else {
        questions = await this.questionsService.getQuestions();
      }      
      this.allQuestions = questions || [];
      this.showQuestions(questions)
    } catch (e) {
      console.error(e);
      this.questions.innerHTML = `
      <alert-component type="danger" message="Cannot get questions, please try again"></alert-component>
      `;
    }
  }

  showQuestions(questions) {
    const container = this.questions;
    if (!container) return;

    container.innerHTML = "";

    questions.forEach(question => {
      this.questions.appendChild(this.getQuestionElement(question));
    });

    this.querySelectorAll('.add-question-to-quiz-button').forEach(btn => {
      btn.onclick = () => this.addQuestionToQuiz(btn.dataset.id);
    });
  }

  getQuestionElement(question) {
    let difficultyBannerHTML = `<span class="badge text-bg-secondary">${question.difficulty}</span>`

    let answers = ''
    if (question.questionType == "OPENED") {
      answers = question.validAnswersOpenQuestion.join(",")
    } else if (question.questionType == "CLOSED") {
      answers = []
      question.closedQuestionOptions.forEach(option => answers.push(option.text))
    }

	const rawRate = question.stats.totalAnswers > 0 
	        ? (question.stats.correctAnswer / question.stats.totalAnswers) * 100 
	        : 0;
    const questionSuccessRate = rawRate.toFixed(2);

    const questionElement = document.createElement("div");
    questionElement.classList.add("card", "col-12", "col-md-6", "col-lg-4");
    questionElement.innerHTML = `
    <div class="card-body">
        <h5 class="card-title">${question.text}</h5>
        ${difficultyBannerHTML} <br>
        Answers: ${answers} <br>
        ${this.userRole == "TEACHER" ? `<a href="#" class="btn btn-primary add-question-to-quiz-button" data-id="${question.id}">Add to quiz</a>` : ``}
        <div id="add-question-${question.id}-result"></div>
        <hr>
        <p>Number of given answers: ${question.stats.totalAnswers} | Success rate: ${questionSuccessRate}%</p>
    </div>
    `;

    return questionElement;
  }

  async addQuestionToQuiz(questionId) {
    try {
      await this.quizService.addQuestionToQuiz(this.quizId, questionId);
      this.dispatchCustomEvent("question-added-to-quiz");
    } catch(e) {
      console.error(e);
      const addQuestionResult = this.querySelector(`#add-question-${questionId}-result`);
      addQuestionResult.innerHTML = `
      <alert-component type="danger" message="Error adding question" timeout="3000"></alert-component>
      `
    }
  }
}

customElements.define('questions-viewer', QuestionsViewer);