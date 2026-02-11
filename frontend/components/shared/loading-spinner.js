export class LoadingSpinner extends HTMLElement {
    async connectedCallback() {
        this.innerHTML = `
        <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
        </div>
        `
    }
}
customElements.define('loading-spinner', LoadingSpinner);