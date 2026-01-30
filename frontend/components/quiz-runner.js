export class QuizRunner extends HTMLElement {
  connectedCallback() {
    this.quizAttemptId = this.getAttribute("quiz-attempt-id");
    this.render();
    this.loadData();

    this.quizQuestions = [];
    this.currentQuestionIndex = -1;
    this.currentQuestionType;
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

  get saveAnswerButton() {
    return this.querySelector("#save-answer-button");
  }

  get openQuestionAnswer() {
    return this.querySelector("#open-question-answer");
  }

  get closedQuestionAnswer() {
    return this.querySelector('input[type="radio"]:checked');
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
      this.currentQuestionType = quizData.questions[0].questionType;
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
    `
    
    this.questionsViewer.innerHTML = questionsViewerHTML;
    this.saveAnswerButton.addEventListener("click", () => { 
      this.handleSaveAnswerToCurrentQuestion();
    });
  }

  getQuestionHTML(question) {
    console.log(question);
    let html = ``;
    html += `<h1>${question.text}</h1>`;
    if (question.questionType == "OPENED") {
      html += `<input class="form-control" placeholder="Write here your answer..." id="open-question-answer"></input><br>`
    } else if (question.questionType == "CLOSED") {
      html += `<div class="input-group mb-3">`
      question.closedQuestionOptions.forEach(option => {
        html += `
        <div class="input-group-text">
            <input class="form-check-input mt-0" name="question-${question.id}" type="radio" value="${option.id}" id="closed-option-${option.id}">
        </div>
        <label class="form-control" for="closed-option-${option.id}">${option.text}</label>
        `
      })
      html += `</div>`
    }
    html += `<button class="btn btn-primary" id="save-answer-button">Save answer</button><br>`
    return html;
  }

  async handleSaveAnswerToCurrentQuestion() {
    const currentQuestion = this.quizQuestions[this.currentQuestionIndex];

    const requestBody = {
      questionId: currentQuestion.id
    }

    let answer;
    if (this.currentQuestionType == "OPEN") {
      answer = this.openQuestionAnswer.value;
      requestBody.openQuestionAnswer = answer;
    } else if (this.currentQuestionType == "CLOSED") {
      if (!this.closedQuestionAnswer) {
        return;
      }
      answer = parseInt(this.closedQuestionAnswer.value);
      requestBody.selectedOptionId = answer;
    }
    
    const jwt = window.localStorage.getItem("token");
    const response = await fetch(`http://localhost:8080/api/quiz-attempts/${this.quizAttemptId}/answers`, {
      method: "PUT",
      headers: {
          "Accept": "application/json",
          "Content-Type": "application/json",
          "Authorization": "Bearer " + jwt
      },
      body: JSON.stringify(requestBody)
    });

    if (response.ok) {
      this.currentQuestionIndex++;
      this.currentQuestionType = this.quizQuestions[this.currentQuestionIndex].questionType;
      this.updateQuestionsViewer();
    } else {
      this.quizErrorSpace.innerHTML = `
      <div class="alert alert-danger" role="alert">
        Error sending your answer, please try again later
      </div>
      `
    }
  }
}

customElements.define('quiz-runner', QuizRunner);