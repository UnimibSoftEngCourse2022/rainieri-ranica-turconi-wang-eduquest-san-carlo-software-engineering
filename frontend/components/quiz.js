export class Quiz extends HTMLElement {
  connectedCallback() {
    const title = this.getAttribute('title');
    const desc = this.getAttribute('description');
    const role = this.getAttribute('role') || "STUDENT";

    let button = "";
    if (role === "STUDENT") {
        button = `<button class="btn btn-sm btn-primary">Run quiz</button>`;
    } else if (role === "TEACHER") {
        button = `<button class="btn btn-sm btn-primary">Edit quiz</button>`;
    }

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