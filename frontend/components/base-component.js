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
        this.cleanup();
    }

    setupComponent() {};
    attachEventListeners() {};

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
}