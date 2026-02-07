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

    async getTestsByAuthorId(authorId) {
        try {
            const response = await callApi(endpoints.tests+`?authorId=${authorId}`, "GET");
            const tests = await response.json();
            return tests;
        } catch (e) {
            return [];
        }
    }

    async createTest(testData) {
        try {
            const response = await callApi(endpoints.tests, "POST", testData);
            if (response.ok) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            return false;
        }
    }

    async deleteTest(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}`, "DELETE");
            if (response.ok) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            return false;
        }
    }
}