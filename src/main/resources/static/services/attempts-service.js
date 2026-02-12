import { callApi, endpoints } from "../js/api.js";

export class AttemptsService {
    async getAttemptById(quizAttemptId) {
        const response = await callApi(`${endpoints.attempts}/${quizAttemptId}`, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }

    async getAttemptSessionById(quizAttemptId) {
        const response = await callApi(`${endpoints.attempts}/${quizAttemptId}/session`, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }

    async getAttemptsByStudentId(studentId) {
        const response = await callApi(`${endpoints.attempts}?studentId=${studentId}`, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }

    async addAttempt(quizId, userId, testId = null) {
        let url = `${endpoints.attempts}?quizId=${quizId}&studentId=${userId}`;
        
        if (testId) {
            url += `&testId=${testId}`;
        }

        const response = await callApi(url, "POST");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }

    async saveAttemptAnswer(attemptId, answer) {
        const response = await callApi(`${endpoints.attempts}/${attemptId}/answers`, "PUT", answer);
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }

    async completeAttemptAnswer(attemptId) {
        const response = await callApi(`${endpoints.attempts}/${attemptId}/complete`, "POST");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }
}