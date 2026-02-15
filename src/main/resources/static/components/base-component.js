export class BaseComponent extends HTMLElement {
    constructor() {
        super();
        this.listeners = [];
    }

    connectedCallback() {
        this.setupComponent();
        this.attachEventListeners();
    }

    disconnectedCallback() {
        this.cleanUp();
    }

    setupComponent() {
        // This method must be implemented in the subclass
    };
    attachEventListeners() {
        // This method must be implemented in the subclass
    };

    addEventListenerWithTracking(selector, event, handler) {
        const element = this.querySelector(selector);
        if (element) {
            element.addEventListener(event, handler);
            this.listeners.push({ element, event, handler });
        } else {
            console.warn(`Cannot find component with selector '${selector}'`);
        }
    }

    cleanUp() {
        this.listeners.forEach(({ element, event, handler }) => {
            element.removeEventListener(event, handler);
        });
        this.listeners = [];
    }

    renderWithState(renderFunction, loading = false, error = false) {
        if (loading) {
            this.innerHTML = `<loading-spinner></loading-spinner>`;
            return;
        }
        if (error) {
            this.innerHTML = `<alert-component type="error" message="An error occurred. Please try again later."></alert-component>`;
            return;
        }
        this.innerHTML = renderFunction();
    }

    dispatchCustomEvent(eventName) {
        this.dispatchEvent(new CustomEvent(eventName, {
            bubbles: true,
            composed: true
        }));
    }
}