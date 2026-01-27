export class AddQuestionButton extends HTMLElement {
  connectedCallback() {
    
    const link = "../add-question/"; 
    const label = "Add question";

    this.innerHTML = `
      <div class="text-center my-4">
        <a href="${link}" class="text-decoration-none">
            <button class="btn btn-primary">
                ${label}
            </button>
        </a>
      </div>
    `;
  }
}

customElements.define('add-question-button', AddQuestionButton);