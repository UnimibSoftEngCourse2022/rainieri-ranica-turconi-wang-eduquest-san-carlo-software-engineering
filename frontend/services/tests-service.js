import { callApi, endpoints } from "../js/api.js";

export class TestsService {
    async getTests() {
        try {
            const response = await callApi(endpoints.tests, "GET");
            const tests = await response.json();
            return tests;
        } catch (e) {
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
            return response.ok;
        } catch (e) {
            return false;
        }
    }

    async deleteTest(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}`, "DELETE");
            return response.ok;
        } catch (e) {
            return false;
        }
    }
}