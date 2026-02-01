export class Alert extends HTMLElement {
    async connectedCallback() {
        this.type = this.getAttribute("type") || "success";
        this.message = this.getAttribute('message') || "";

        this.innerHTML = `
        <div class="alert alert-${this.type}" role="alert">
            ${this.message}
        </div>
        `
    }
}
customElements.define('alert-component', Alert);