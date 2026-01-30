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

  get previousQuestionButton() {
    return this.querySelector("#button-prev-question");
  }

  get nextQuestionButton() {
    return this.querySelector("#button-next-question");
  }

  get saveAnswerButton() {
    return this.querySelector("#save-answer-button");
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
    const response = await fetch(`http://localhost:8080/api/quiz-attempts/${this.quizAttemptId}`, {
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
    const response = await fetch(`http://localhost:8080/api/quizzes/${quizData.quizId}`, {
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
    
    let questionsViewerHTML = `
    ${this.getQuestionHTML(currentQuestion)} <br>
    <p>Domanda ${this.currentQuestionIndex + 1}/${this.quizQuestions.length} <br>
    <button id="button-prev-question" class="btn btn-primary" ${this.currentQuestionIndex == 0                             ? "disabled" : ""}>Previous</button>
    <button id="button-next-question" class="btn btn-primary" ${this.currentQuestionIndex == this.quizQuestions.length - 1 ? "disabled" : ""}>Next</button>
    `
    
    this.questionsViewer.innerHTML = questionsViewerHTML;
    this.previousQuestionButton.addEventListener("click", () => {
      this.currentQuestionIndex--;
      this.updateQuestionsViewer();
    })
    this.nextQuestionButton.addEventListener("click", () => {
      this.currentQuestionIndex++;
      this.updateQuestionsViewer();
    })
    this.saveAnswerButton.addEventListener("click", () => { 
      this.handleSaveAnswerToCurrentQuestion();
    });
  }

  getQuestionHTML(question) {
    console.log(question);
    let html = ``;
    html += `<h1>${question.text}</h1>`;
    if (question.questionType == "OPENED") {
      html += `<input class="form-control" placeholder="Write here your answer..."></input><br>`
    } else if (question.questionType == "CLOSED") {
      // TODO
    }
    html += `<button class="btn btn-primary" id="save-answer-button">Save answer</button><br>`
    return html;
  }

  async handleSaveAnswerToCurrentQuestion() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];
    const answerInput = this.questionsViewer.querySelector("input");
    const answerText = answerInput.value;

    console.log("salvo la domanda " + currentQuestion.id + " con risposta: " + answerText);

    const jwt = window.localStorage.getItem("token");
    const requestBody = {
      questionId: currentQuestion.id,
      textOpenAnswer: answerText
    }
    const response = await fetch(`http://localhost:8080/api/quiz-attempts/${this.quizAttemptId}/answers`, {
      method: "PUT",
      headers: {
          "Accept": "application/json",
          "Content-Type": "application/json",
          "Authorization": "Bearer " + jwt
      },
      body: JSON.stringify(requestBody)
    });
  }
}

customElements.define('quiz-runner', QuizRunner);