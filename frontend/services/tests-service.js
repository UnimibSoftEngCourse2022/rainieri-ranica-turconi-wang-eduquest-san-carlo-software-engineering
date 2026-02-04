import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class TestsService {
    async getTests() {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(endpoints.tests, "GET");
            const tests = await response.json();
            appStore.updateAppState({ loading: false });
            return tests;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
            return [];
        }
    }

    async createTest(testData) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(endpoints.tests, "POST", testData);
            if (response.ok) {
                appStore.updateAppState({ loading: false });
                return true;
            } else {
                appStore.updateAppState({ loading: false, error: true });
                return false;
            }
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
            return false;
        }
    }

    async deleteTest(testId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.tests}/${testId}`, "DELETE");
            if (response.ok) {
                appStore.updateAppState({ loading: false });
                return true;
            } else {
                appStore.updateAppState({ loading: false, error: true });
                return false;
            }
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
            return false;
        }
    }
}