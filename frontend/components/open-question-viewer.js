export class OpenQuestionViewer extends HTMLElement {
  connectedCallback() {
    this.authorId = this.getAttribute("id");
    this.role = this.getAttribute("role");
    this.render();
    this.loadData();
  }

  get questions() {
    return this.querySelector("#questions");
  }

  render() {
    this.innerHTML = `ciao`;
  }

  async loadData() {
    const jwt = window.localStorage.getItem("token");

    let questionsEndpoint = "http://localhost:8080/api/quiz/question";
    const response = await fetch(questionsEndpoint, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwt
        }
    });
  }
}

customElements.define('open-question-viewer', OpenQuestionViewer);