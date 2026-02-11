export class CollapsiblePanel extends HTMLElement {
    connectedCallback() {
        const originalContent = this.innerHTML;
        const title = this.getAttribute('title') || 'Menu';
        const startOpen = this.hasAttribute('open');
        const uniqueId = 'collapse-' + Math.random().toString(36).substring(2, 9);
        const showClass = startOpen ? 'show' : '';
        const expandedAttr = startOpen ? 'true' : 'false';

        this.innerHTML = `
            <div class="mb-4">
                <div class="d-flex justify-content-between align-items-center border-bottom pb-2"
                     style="cursor: pointer;"
                     data-bs-toggle="collapse"
                     data-bs-target="#${uniqueId}"
                     aria-expanded="${expandedAttr}">
                    
                    <h3 class="m-0 fs-4">${title}</h3>

                    <div class="ms-2">
                        <svg class="arrow-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                    </div>
                </div>

                <div id="${uniqueId}" class="collapse ${showClass} mt-3">
                    ${originalContent}
                </div>
            </div>
        `;
    }
}

customElements.define('collapsible-panel', CollapsiblePanel);