import { callApi, endpoints } from "../js/api.js";

export class AttemptsService {
    async getAttemptById(quizAttemptId) {
        try {
            const response = await callApi(`${endpoints.attempts}/${quizAttemptId}`, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            return await response.json();
        } catch (e) {
            throw new Error(e);
        }
    }

    async getAttemptSessionById(quizAttemptId) {
        try {
            const response = await callApi(`${endpoints.attempts}/${quizAttemptId}/session`, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            return await response.json();
        } catch (e) {
            throw new Error(e);
        }
    }

    async getAttemptsByStudentId(studentId) {
        try {
            const response = await callApi(`${endpoints.attempts}?studentId=${studentId}`, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            return await response.json();
        } catch (e) {
            throw new Error(e);
        }
    }

    async addAttempt(quizId, userId, testId = null) {
        let url = `${endpoints.attempts}?quizId=${quizId}&studentId=${userId}`;
        
        if (testId) {
            url += `&testId=${testId}`;
        }

        try {
            const response = await callApi(url, "POST");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            return await response.json();
        } catch(e) {
            throw new Error(e);
        }
    }

    async saveAttemptAnswer(attemptId, answer) {
        try {
            const response = await callApi(`${endpoints.attempts}/${attemptId}/answers`, "PUT", answer);
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            return await response.json();
        } catch (e) {
            throw new Error(e);
        }
    }

    async completeAttemptAnswer(attemptId) {
        try {
            const response = await callApi(`${endpoints.attempts}/${attemptId}/complete`, "POST");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            return await response.json();
        } catch (e) {
            throw new Error(e);
        }
    }
}