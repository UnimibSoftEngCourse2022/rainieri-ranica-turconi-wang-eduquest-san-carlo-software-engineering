import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class AttemptsService {
    async getAttemptById(quizAttemptId) {
        try {
            const response = await callApi(`${endpoints.attempts}/${quizAttemptId}`, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {
        }
    }

    async getAttemptSessionById(quizAttemptId) {
        try {
            const response = await callApi(`${endpoints.attempts}/${quizAttemptId}/session`, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {
        }
    }

    async getAttemptsByStudentId(studentId) {
        try {
            const response = await callApi(`${endpoints.attempts}?studentId=${studentId}`, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {
        }
    }

    async addAttempt(quizId, userId) {
        try {
            const response = await callApi(`${endpoints.attempts}?quizId=${quizId}&studentId=${userId}`, "POST");
            const quizzes = await response.json();
            return quizzes;
        } catch (e) {
        }
    }

    async saveAttemptAnswer(attemptId, answer) {
        try {
            const response = await callApi(`${endpoints.attempts}/${attemptId}/answers`, "PUT", answer);
            const r = await response.json();
            return r;
        } catch (e) {
        }
    }

    async completeAttemptAnswer(attemptId) {
        try {
            const response = await callApi(`${endpoints.attempts}/${attemptId}/complete`, "POST");
            const r = await response.json();
            return r;
        } catch (e) {
        }
    }
}