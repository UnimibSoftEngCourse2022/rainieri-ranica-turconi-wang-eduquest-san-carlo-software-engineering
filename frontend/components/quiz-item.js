export class Quiz extends HTMLElement {
  connectedCallback() {
    const id = this.getAttribute('id');
    const title = this.getAttribute('title');
    const desc = this.getAttribute('description');
    const role = this.getAttribute('role') || "STUDENT";

    let buttonText = "";
    let buttonLink = "";
    if (role === "STUDENT") {
      buttonText = "Run quiz"
      buttonLink = `../quiz-execution?id=${this.id}`
    } else if (role === "TEACHER") {
      buttonText = "Edit quiz"
      buttonLink = `../quiz-editor?id=${this.id}`
    }
    const button = `
    <a href="${buttonLink}">
      <button class="btn btn-sm btn-primary">${buttonText}</button>
    </a>
    `;

    this.innerHTML = `
      <div class="card my-2" style="border: 1px solid #ccc; padding: 10px;">
        <h3>${title}</h3>
        <p>${desc}</p>
        ${button}
    </div>
    `;
  }
}
customElements.define('quiz-item', Quiz);