import { callApi, endpoints } from "../js/api.js";

export class TestsService {
    async getTests() {
        const response = await callApi(endpoints.tests, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const tests = await response.json();
        return tests;
    }

    async getTestsByAuthorId(authorId) {
        const response = await callApi(endpoints.tests+`?authorId=${authorId}`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const tests = await response.json();
        return tests;
    }

    async getTestById(testId) {
        const response = await callApi(`${endpoints.tests}/${testId}`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return await response.json();
    }

    async createTest(testData) {
        const response = await callApi(endpoints.tests, "POST", testData);
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return response.ok;
    }

    async deleteTest(testId) {
        const response = await callApi(`${endpoints.tests}/${testId}`, "DELETE");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return response.ok;
    }

    async getMyAttempts(testId) {
        const response = await callApi(`${endpoints.tests}/${testId}/my-attempts`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return await response.json();
    }
}