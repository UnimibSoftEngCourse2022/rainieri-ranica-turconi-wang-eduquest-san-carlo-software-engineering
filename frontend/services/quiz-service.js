import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class QuizService {
    async getQuizzes() {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(endpoints.quizzes, "GET");
            const quizzes = await response.json();
            appStore.updateAppState({ loading: false });
            return quizzes;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async getQuizById(quizId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}`, "GET");
            const quiz = await response.json();
            appStore.updateAppState({ loading: false });
            return quiz;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async createQuiz(quizData) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(endpoints.quizzes, "POST", quizData);

            if (response.ok) {
                appStore.updateAppState({ loading: false });
            } else {
                appStore.updateAppState({ loading: false, error: true });
            }
            return await response.json();
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async addQuestionToQuiz(quizId, questionId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "POST");

            if (response.ok) {
                appStore.updateAppState({ loading: false });
            } else {
                appStore.updateAppState({ loading: false, error: true });
            }
            return await response.json();
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async removeQuestionFromQuiz(quizId, questionId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "DELETE");

            if (response.ok) {
                appStore.updateAppState({ loading: false });
            } else {
                appStore.updateAppState({ loading: false, error: true });
            }
            
            return await response.json();
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }
}