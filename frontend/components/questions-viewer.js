export class QuestionsViewer extends HTMLElement {
  connectedCallback() {
    this.quizId = this.getAttribute("quizId");
    this.authorId = this.getAttribute("authorId");
    this.role = this.getAttribute("role");
    this.render();
    this.loadData();
  }

  get questions() {
    return this.querySelector("#questions");
  }

  render() {
    this.innerHTML = `<div id="questions" class="container"></div>`;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");

    let questionsEndpoint = "http://localhost:8080/api/questions";
    if (this.authorId) {
        questionsEndpoint += `?authorId=${this.authorId}`
    }
    const response = await fetch(questionsEndpoint, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const questions = await response.json();
        this.showQuestions(questions)
    } else {
        this.questions.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot get questions, please try again
        </div>
        `
    }
  }

  async showQuestions(questions) {
    let questionsHTML = '';
    questions.forEach(question => {
        let difficultyBannerHTML = `
        <span class="badge text-bg-secondary">${question.difficulty}</span></h6>
        `
        let answers = ''
        if (question.questionType == "OPENED") {
          answers = question.validAnswersOpenQuestion.join(",")
        } else if (question.questionType == "CLOSED") {
          answers = []
          question.closedQuestionOptions.forEach(option => answers.push(option.text))
        }

        questionsHTML += `
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">${question.text}</h5>
                ${difficultyBannerHTML} <br>
                Answers: ${answers} <br>
                ${this.role == "TEACHER" ? `<a href="#" class="btn btn-primary add-question-to-quiz-button" data-id="${question.id}">Add to quiz</a>` : ``}
                <div id="add-question-${question.id}-result"></div>
            </div>
        </div>
        `
    })
    this.questions.innerHTML = questionsHTML
    this.questions.querySelectorAll(".add-question-to-quiz-button").forEach(button => {
      button.addEventListener("click", (event) => {
        const questionId = event.target.getAttribute("data-id");
        this.addQuestionToQuiz(questionId);
      })
    })
  }

  async addQuestionToQuiz(questionId) {
    const jwt = window.localStorage.getItem("token");
    const response = await fetch(`http://localhost:8080/api/quiz/${this.quizId}/add-question/${questionId}`, {
      method: "POST",
      headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
      }
    });

    const addQuestionResult = this.querySelector(`#add-question-${questionId}-result`);
    if (response.ok) {
      this.dispatchEvent(new CustomEvent("question-added-to-quiz", {
        bubbles: true,
        composed: true
      }))
    } else {
      addQuestionResult.innerHTML = `
      <div class="alert alert-danger" role="alert">
            Error adding question
      </div>
      `
    }
  }
}

customElements.define('questions-viewer', QuestionsViewer);