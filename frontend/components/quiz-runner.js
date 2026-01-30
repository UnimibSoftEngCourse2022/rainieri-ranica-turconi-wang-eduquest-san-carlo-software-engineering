export class QuizRunner extends HTMLElement {
  connectedCallback() {
    this.quizAttemptId = this.getAttribute("quiz-attempt-id");
    this.render();
    this.loadData();

    this.quizQuestions = [];
    this.currentQuestionIndex = -1;
  }

  get upperSpace() {
    return this.querySelector("#quiz-title");
  }

  get quizErrorSpace() {
    return this.querySelector("#quiz-error");
  }

  get questionsViewer() {
    return this.querySelector("#questions-viewer");
  }

  render() {
    this.innerHTML = `
    <div class="card text-center">
        <div class="card-header" style="padding: 20px">
            <div id="quiz-title"></div>
        </div>
        <div class="card-body" id="questions-viewer">
        </div>
        <div id="quiz-error"></div>
    </div>
    `;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");
    const response = await fetch(`http://localhost:8080/api/quizAttempt/${this.quizAttemptId}`, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });

    if (response.ok) {
        const quizData = await response.json();
        this.showQuizData(quizData);
    } else {
        this.quizErrorSpace.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Cannot load the test, please try again later
        </div>
        `
    }
  }

  async showQuizData(quizData) {
    const [startDate, completeStartTime] = quizData.startedAt.split("T")
    const [startTime, _] = completeStartTime.split(".")
    this.upperSpace.innerHTML = `
    <h4>${quizData.quizTitle}</h4>
    Started at: ${startTime}, ${startDate}
    `

    const jwt = window.localStorage.getItem("token");
    const response = await fetch(`http://localhost:8080/api/quiz/${quizData.quizId}`, {
      method: "GET",
      headers: {
          "Accept": "application/json",
          "Content-Type": "application/json",
          "Authorization": "Bearer " + jwt
      }
    });
    if (response.ok) {
      const quizData = await response.json();
      this.quizQuestions = quizData.questions;
      this.currentQuestionIndex = 0;
      this.updateQuestionsViewer();
    } else {
      this.quizErrorSpace.innerHTML = `
      <div class="alert alert-danger" role="alert">
            Cannot load the test, please try again later
        </div>
      `
    }
  }

  async updateQuestionsViewer() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    let currentQuestionHTML = `
    <h4>${currentQuestion.text}</h4>
    `

    let questionsViewerHTML = `
    ${currentQuestionHTML}
    <p>Domanda ${this.currentQuestionIndex + 1}/${this.quizQuestions.length}
    `
    this.questionsViewer.innerHTML = questionsViewerHTML;
  }
}

customElements.define('quiz-runner', QuizRunner);