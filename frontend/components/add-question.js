import { QuestionsService } from "../services/questions-service.js";
import { BaseComponent } from "./base-component.js";
import { Alert } from "./shared/alert.js";

export class AddQuestion extends BaseComponent {
  setupComponent() {
    this.questionsService = new QuestionsService();
    this.render();

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

  get mediaTypeSelect() { 
    return this.querySelector("#media-type-select"); 
  }

  get mediaFileInput() { 
    return this.querySelector("#media-file-input");
  }

  get mediaUrlInput() {
    return this.querySelector("#media-url-input");
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
            <div class="mb-3 p-3 border rounded bg-light text-start">
                <label class="form-label fw-bold">Multimedia (Optional)</label>
                
                <div class="input-group mb-2">
                    <select class="form-select" id="media-type-select" style="max-width: 140px;">
                        <option value="NONE" selected>None</option>
                        <option value="IMAGE">Image</option>
                        <option value="VIDEO">Video</option>
                        <option value="YOUTUBE">YouTube</option>
                    </select>

                    <input type="file" class="form-control" id="media-file-input" disabled>
                    
                    <input type="text" class="form-control" id="media-url-input" placeholder="Paste YouTube link here..." style="display: none;">
                </div>
                <div class="form-text" id="media-help-text">Select a media type to attach content.</div>
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

  attachEventListeners() {
    this.questionType.addEventListener("change", () => this.updateQuestionFields());
    this.addQuestionForm.addEventListener("submit", (e) => this.handleAddQuestion(e));
    if (this.mediaTypeSelect) {
        this.mediaTypeSelect.addEventListener("change", () => this.updateMediaFields());
    }
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
    const mediaType = this.mediaTypeSelect.value;
    const fileInput = this.mediaFileInput;
    const urlInput = this.mediaUrlInput;

    const requestBody = {
        text, topic, difficulty, questionType
    }

    let fileToSend = null;

    if (mediaType !== "NONE") {
        if (mediaType === "YOUTUBE") {
            const rawLink = urlInput.value.trim();
            if (rawLink) {
                requestBody.multimediaType = "VIDEO";
                requestBody.isYoutube = true;
                requestBody.multimediaUrl = this.convertToEmbedUrl(rawLink);
            }
        } 
        else if (mediaType === "VIDEO" && fileInput.files.length > 0) {
            requestBody.multimediaType = "VIDEO";
            requestBody.isYoutube = false;
            fileToSend = fileInput.files[0];
        }
        else if (mediaType === "IMAGE" && fileInput.files.length > 0) {
            requestBody.multimediaType = "IMAGE";
            fileToSend = fileInput.files[0];
        }
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

    const formData = new FormData();

    formData.append("question", new Blob([JSON.stringify(requestBody)], {
        type: "application/json"
    }))

    if (fileToSend) {
        formData.append("file", fileToSend);
    }

    this.submitData(formData);
  }

  async submitData(formData) {
    const response = await this.questionsService.createQuestion(formData);
    if (response) {
        this.addQuestionResult.innerHTML = `
        <alert-component type="success" message="Question added successfully"></alert-component>
        `;
        this.addQuestionForm.reset(); // Pulisce il form dopo l'invio
        this.dispatchCustomEvent("question-added");
    } else {
        this.addQuestionResult.innerHTML = `
        <alert-component type="danger" message="Error creating question"></alert-component>
        `;
    }
  }

  updateMediaFields() {
    const type = this.mediaTypeSelect.value;
    const fileInput = this.mediaFileInput;
    const urlInput = this.mediaUrlInput;

    fileInput.value = "";
    urlInput.value = "";

    if (type === "NONE") {
        fileInput.style.display = "block";
        fileInput.disabled = true;
        urlInput.style.display = "none";
    } 
    else if (type === "YOUTUBE") {
        fileInput.style.display = "none";
        urlInput.style.display = "block";
    } 
    else {
        urlInput.style.display = "none";
        fileInput.style.display = "block";
        fileInput.disabled = false;
        
        fileInput.accept = (type === "IMAGE") ? "image/*" : "video/*";
    }
  }

  convertToEmbedUrl(url) {
      if (!url) return null;
      const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
      const match = url.match(regExp);

      if (match && match[2].length === 11) {
          return `https://www.youtube.com/embed/${match[2]}`;
      } else {
          return url;
      }
  }
}

customElements.define('add-question', AddQuestion);