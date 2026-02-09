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

    async getTestById(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}`, "GET");
            if (!response.ok) {
                throw new Error("Impossible to fetch test details");
            }
            return await response.json();
        } catch (e) {
            console.error("Error in getTestById:", e);
            throw e; 
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

    async getMyAttempts(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}/my-attempts`, "GET");
            
            if (response.ok) {
                return await response.json();
            } else {
                return [];
            }
        } catch (e) {
            console.error("Errore fetching tentativi test:", e);
            return [];
        }
    }
}