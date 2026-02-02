import { AttemptsService } from "../services/attempts-service.js";
import { QuizService } from "../services/quiz-service.js";
import { BaseComponent } from "./base-component.js";
import { Alert } from "./shared/alert.js";

export class QuestionRunner extends BaseComponent {
  setupComponent() {
    
  }

  render() {
    if (!this._question) { return; }

    let html = `<div class="container"><h4>${this._question.text}</h4>`

    
    if (this.question.questionType == "OPENED") {
      html += `<input class="form-control" type="text" id="open-answer-input" />`
    } else if (this.question.questionType == "CLOSED") {
      this.question.closedQuestionOptions.forEach(option => {
        html += `
        <label class="list-group-item list-group-item-action d-flex align-items-center gap-3 py-3" style="cursor: pointer;">
          <input class="form-check-input flex-shrink-0" type="radio" 
                 name="question-${this._question.id}" 
                 value="${option.id}" 
                 id="closed-option-${option.id}" 
                 style="font-size: 1.375em;">
          <span class="pt-1">${option.text}</span>
        </label>
        `;
      });
    }

    html += `</div>`;
    this.innerHTML = html;
  }

  set question(data) {
    this._question = data;
    this.render();
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
      return selectedOption;
    }
  }
}

customElements.define('question-runner', QuestionRunner);