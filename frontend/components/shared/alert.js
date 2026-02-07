export class Alert extends HTMLElement {
    async connectedCallback() {
        this.type = this.getAttribute("type") || "success";
        this.message = this.getAttribute('message') || "";
        this.timeout = this.getAttribute("timeout");

        this.innerHTML = `
        <div class="alert alert-${this.type}" role="alert">
            ${this.message}
        </div>
        `

        if (this.timeout) {
            const ms = parseInt(this.timeout);
            setTimeout(() => {
                this.remove(); 
            }, ms);
        }
    }
}
customElements.define('alert-component', Alert);