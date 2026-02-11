import { callApi, endpoints } from "../js/api.js";

export class TestsService {
    async getTests() {
        try {
            const response = await callApi(endpoints.tests, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            const tests = await response.json();
            return tests;
        } catch (e) {
            throw e;
        }
    }

    async getTestsByAuthorId(authorId) {
        try {
            const response = await callApi(endpoints.tests+`?authorId=${authorId}`, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            const tests = await response.json();
            return tests;
        } catch (e) {
            throw e;
        }
    }

    async getTestById(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}`, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
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
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            return response.ok;
        } catch (e) {
            throw e;
        }
    }

    async deleteTest(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}`, "DELETE");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            return response.ok;
        } catch (e) {
            throw e;
        }
    }

    async getMyAttempts(testId) {
        try {
            const response = await callApi(`${endpoints.tests}/${testId}/my-attempts`, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            return await response.json();
        } catch (e) {
            console.error("Errore fetching tentativi test:", e);
            throw e;
        }
    }
}