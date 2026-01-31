export class AddQuestion extends HTMLElement {
  connectedCallback() {
    this.render();
    this.setupEventListeners();
    this.updateQuestionFields();

    this.nClosedQuestionOptions = 4;
  }

  get questionType() {
    return this.querySelector("#type-input");
  }

  get addQuestionForm() {
    return this.querySelector("#add-question-form");
  }

  get questionText() {
    return this.querySelector("#text-input");
  }

  get questionTopic() {
    return this.querySelector("#topic-input");
  }

  get questionDifficulty() {
    return this.querySelector("#difficulty-input");
  }

  get validAnswers() {
    return this.querySelector("#valid-answers-input");
  }

  get addQuestionResult() {
    return this.querySelector("#add-question-result")
  }

  render() {
    this.innerHTML = `
    <div class="container text-center">
        <form id="add-question-form">
            <div class="mb-3">
                <label for="text-input" class="form-label">
                    Text
                </label>
                <input
                    type="text"
                    class="form-control"
                    id="text-input"
                />
            </div>
            <div class="mb-3">
                <label for="topic-input" class="form-label">
                    Topic
                </label>
                <input
                    type="text"
                    class="form-control"
                    id="topic-input"
                />
            </div>
            <div class="mb-3">
                <label for="difficulty-input" class="form-label">
                    Difficulty
                </label>
                <select class="form-select" id="difficulty-input">
                    <option selected disabled>Select difficulty...</option>
                    <option value="EASY">Easy</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HARD">Hard</option>
                </select>
            </div>
            <div class="mb-3">
                <label for="type-input" class="form-label">
                    Question type
                </label>
                <select class="form-select" id="type-input">
                    <option selected value="OPENED">Opened</option>
                    <option value="CLOSED">Closed</option>
                </select>
            </div>
            <div id="other-fields"></div>
            <button type="submit" class="btn btn-primary" id="add-question-button">Add question</button>
            <div class="container my-2" id="add-question-result"></div>
        </form>
    </div>
    `;
  }

  setupEventListeners() {
    this.questionType.addEventListener("change", () => this.updateQuestionFields());
    this.addQuestionForm.addEventListener("submit", (e) => this.handleAddQuestion(e));
  }

  updateQuestionFields() {
    const otherFieldsDiv = this.querySelector("#other-fields");
    const questionType = this.questionType.value.toUpperCase();

    if (questionType == "OPENED") {
        otherFieldsDiv.innerHTML = `
        <div class="mb-3">
            <label for="valid-answers-input" class="form-label">
                Accepted answers (separated by ',')
            </label>
            <input
                type="text"
                class="form-control"
                id="valid-answers-input"
            />
        </div>
        `;
    } else {
        let otherFieldsHTML = ``;
        for (let i = 0; i < this.nClosedQuestionOptions; i++) {
            otherFieldsHTML += `
            <div class="input-group mb-3">
                <input id="closed-option-${i}-text" type="text" class="form-control" placeholder="Option...">
                <div class="input-group-text">
                    <input class="form-check-input mt-0" type="checkbox" value="" id="closed-option-${i}-is-true">
                </div>
            </div>
            `;
        }
        otherFieldsDiv.innerHTML = otherFieldsHTML;
    }
  }

  async handleAddQuestion(event) {
    event.preventDefault();

    const text = this.questionText.value;
    const topic = this.questionTopic.value;
    const difficulty = this.questionDifficulty.value;
    const questionType = this.questionType.value.toUpperCase();

    const requestBody = {
        text, topic, difficulty, questionType
    }

    if (questionType == "OPENED") {
        const validAnswersOpenQuestionRaw = this.querySelector("#valid-answers-input").value.split(",");
        const validAnswersOpenQuestion = validAnswersOpenQuestionRaw.map(s => s.trim());
        requestBody.validAnswersOpenQuestion = validAnswersOpenQuestion;
    } else {
        const closedOptions = []
        for (let i = 0; i < this.nClosedQuestionOptions; i++) {
            const textInput = this.querySelector(`#closed-option-${i}-text`);
            const checkbox = this.querySelector(`#closed-option-${i}-is-true`);
            if (textInput && checkbox) {
                const text = textInput.value.trim();
                const isTrue = checkbox.checked;
                if (text !== "") { 
                    closedOptions.push({ text, isTrue });
                }
            }
        }
        requestBody.closedQuestionOptions = closedOptions
    }

    this.submitData(requestBody);
  }

  async submitData(requestBody) {
    try {
        const jwt = window.localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/api/questions", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
                "Authorization": "Bearer " + jwt
            },
            body: JSON.stringify(requestBody)
        });
    
        if (response.ok) {
            this.addQuestionResult.innerHTML = `
            <div class="alert alert-success" role="alert">
                Question added successfully
            </div>
            `;
        } else {
            this.addQuestionResult.innerHTML = `
            <div class="alert alert-danger" role="alert">
                Error creating question
            </div>
            `
        }
    } catch (e) {
        this.addQuestionResult.innerHTML = `
        <div class="alert alert-danger" role="alert">
            Error creating question, please try again later
        </div>
        `
    }
  }
}

customElements.define('add-question', AddQuestion);