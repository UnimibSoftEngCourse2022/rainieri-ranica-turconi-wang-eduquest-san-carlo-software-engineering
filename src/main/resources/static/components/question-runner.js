import { BaseComponent } from "./base-component.js";
import "./shared/alert.js";

export class QuestionRunner extends BaseComponent {
  render() {
    if (!this._question) { return; }

    this.innerHTML = ``;
    const questionContainer = document.createElement("div");
    questionContainer.classList.add("container");
    questionContainer.innerHTML = `<h4>${this._question.text}</h4>`
    
    if (this._question?.multimedia?.url) {
        const media = this._question.multimedia;
        const mediaContainer = document.createElement("div");
        mediaContainer.classList.add("text-center", "my-3");

        let mediaHtml = "";

        if (media.type === "IMAGE") {
            mediaHtml = `
            <img src="${media.url}" 
                  class="img-fluid rounded shadow-sm" 
                  style="max-height: 400px; object-fit: contain;" 
                  alt="Question Image">
            `;
        } 
        
        else if (media.type === "VIDEO") {
            
            if (media.isYoutube) {
                mediaHtml = `
                    <div class="ratio ratio-16x9 mx-auto" style="max-width: 800px;">
                        <iframe src="${media.url}" 
                                title="YouTube video player" 
                                allowfullscreen
                                class="rounded shadow-sm">
                        </iframe>
                    </div>
                `;
            } 

            else {
                mediaHtml = `
                    <video controls class="rounded shadow-sm" style="max-width: 100%; max-height: 400px;">
                        <source src="${media.url}" type="video/mp4">
                        <source src="${media.url}" type="video/webm">
                        Your browser does not support the video tag.
                    </video>
                `;
            }
        }

        mediaContainer.innerHTML = mediaHtml;
        questionContainer.appendChild(mediaContainer);
    }

    if (this.question.questionType == "OPENED") {
      const input = document.createElement("input");
      input.classList.add("form-control");
      input.type = "text";
      input.id = "open-answer-input";
      if (this._previousAnswer) {
        input.value = this._previousAnswer;
      }
      questionContainer.appendChild(input);
    } else if (this.question.questionType == "CLOSED") {
      this.question.closedQuestionOptions.forEach(option => {
        const selection = document.createElement("label");
        selection.class = "list-group-item list-group-item-action d-flex align-items-center gap-3 py-3";
        selection.style = "cursor: pointer;";
        selection.innerHTML = `
        <input class="form-check-input flex-shrink-0" type="radio" 
                name="question-${this._question.id}" 
                value="${option.id}" 
                id="closed-option-${option.id}" 
                style="font-size: 1.375em;"
                ${this._previousAnswer == option.id ? `checked` : ``}>
        <span class="pt-1">${option.text}</span>
        `;
        questionContainer.appendChild(selection);
      });
    }

    this.appendChild(questionContainer);
  }

  set question(data) {
    this._question = data;
    this.render();
  }

  set answer(answer) {
    this._previousAnswer = answer;
  }

  get question() {
    return this._question;
  }

  get answer() {
    if (this.question.questionType == "OPENED") {
      return this.querySelector("#open-answer-input").value;
    } else if (this.question.questionType == "CLOSED") {
      const selectedOption = this.querySelector(`input[name="question-${this._question.id}"]:checked`);
      if (selectedOption) {
        return selectedOption.value;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
}

customElements.define('question-runner', QuestionRunner);