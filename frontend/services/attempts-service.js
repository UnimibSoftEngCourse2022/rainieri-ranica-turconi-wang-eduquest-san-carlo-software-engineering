import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class AttemptsService {
    async getAttemptById(quizAttemptId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.attempts}/${quizAttemptId}`, "GET");
            const attempts = await response.json();
            appStore.updateAppState({ loading: false });
            return attempts;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async getAttemptsByStudentId(studentId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.attempts}?studentId=${studentId}`, "GET");
            const attempts = await response.json();
            appStore.updateAppState({ loading: false });
            return attempts;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async addAttempt(quizId, userId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.attempts}?quizId=${quizId}&studentId=${userId}`, "POST");
            const quizzes = await response.json();
            appStore.updateAppState({ loading: false });
            return quizzes;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async saveAttemptAnswer(attemptId, answer) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.attempts}/${attemptId}/answers`, "PUT", answer);
            const r = await response.json();
            appStore.updateAppState({ loading: false });
            return r;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async completeAttemptAnswer(attemptId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.attempts}/${attemptId}/complete`, "POST");
            const r = await response.json();
            appStore.updateAppState({ loading: false });
            return r;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }
}