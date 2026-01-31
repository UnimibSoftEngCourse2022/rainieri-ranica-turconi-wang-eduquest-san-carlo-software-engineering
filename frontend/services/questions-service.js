import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class QuestionsService {
    async getQuestions() {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(endpoints.questions, "GET");
            const questions = await response.json();
            appStore.updateAppState({ loading: false });
            return questions;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async getQuestionByAuthorId(authorId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.questions}?authorId=${authorId}`, "GET");
            const quizzes = await response.json();
            appStore.updateAppState({ loading: false });
            return quizzes;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async createQuestion(questionData) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(endpoints.questions, "POST", questionData);

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