import { callApi, endpoints } from "../js/api.js";

export class QuizService {
    async getQuizzes() {
        const response = await callApi(endpoints.quizzes, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const quizzes = await response.json();
        return quizzes;
    }

    async getQuizzesByAuthorId(authorId) {
        const response = await callApi(endpoints.quizzes+`?authorId=${authorId}`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const quizzes = await response.json();
        return quizzes;
    }

    async getQuizById(quizId) {
        const response = await callApi(`${endpoints.quizzes}/${quizId}`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const quiz = await response.json();
        return quiz;
    }

    async createQuiz(quizData) {
        const response = await callApi(endpoints.quizzes, "POST", quizData);
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        if (response.ok) {
            return response;
        }
        return await response.json();
    }

    async modifyQuiz(quizId, quizData) {
        const response = await callApi(endpoints.quizzes+`/${quizId}`, "PUT", quizData);
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return await response.json();
    }

    async addQuestionToQuiz(quizId, questionId) {
        const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "POST");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return await response.json();
    }

    async removeQuestionFromQuiz(quizId, questionId) {
        const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "DELETE");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        return await response.json();
    }
}