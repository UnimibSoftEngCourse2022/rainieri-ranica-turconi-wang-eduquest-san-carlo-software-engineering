class AppStore {
    constructor() {
        this.state = {
            user: null,
            token: null,
            loading: false,
            error: false
        }
        this.listeners = [];
    }

    getState() {
        return this.state;
    }

    updateAppState(newStateUpdates) {
        this.state = {  ...this.state, ...newStateUpdates };
        this.listeners.forEach(listenerCallback => listenerCallback(this.state));
    }

    subscribe(listenerCallback) {
        this.listeners.push(listenerCallback);
        return this.unsubscribe;
    }

    unsubscribe(listenerCallback) {
        this.listeners = this.listeners.filter(cb => cb !== listenerCallback);
    }
}

export const appStore = new AppStore();
