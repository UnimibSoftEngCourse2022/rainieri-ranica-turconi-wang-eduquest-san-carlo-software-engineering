import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class QuizService {
    async getQuizzes() {
        try {
            const response = await callApi(endpoints.quizzes, "GET");
            const quizzes = await response.json();
            return quizzes;
        } catch (e) {
        }
    }

    async getQuizzesByAuthorId(authorId) {
       try {
            const response = await callApi(endpoints.quizzes+`?authorId=${authorId}`, "GET");
            const quizzes = await response.json();
            return quizzes;
        } catch (e) {
        }
    }

    async getQuizById(quizId) {
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}`, "GET");
            const quiz = await response.json();
            return quiz;
        } catch (e) {
        }
    }

    async createQuiz(quizData) {
        try {
            const response = await callApi(endpoints.quizzes, "POST", quizData);
            if (response.ok) {
                return response;
            }
            return await response.json();
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }

    async modifyQuiz(quizId, quizData) {
        try {
            const response = await callApi(endpoints.quizzes+`/${quizId}`, "PUT", quizData);
            if (response.ok) {
                return await response.json();
            }
        } catch (e) {}
    }

    async addQuestionToQuiz(quizId, questionId) {
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "POST");

            if (response.ok) {
                return response;
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
                return response;
            }
            
            return await response.json();
        } catch (e) {
        }
    }
}