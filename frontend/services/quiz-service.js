import { callApi, endpoints } from "../js/api.js";

export class QuizService {
    async getQuizzes() {
        try {
            const response = await callApi(endpoints.quizzes, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            const quizzes = await response.json();
            return quizzes;
        } catch (e) {
            throw e;
        }
    }

    async getQuizzesByAuthorId(authorId) {
       try {
            const response = await callApi(endpoints.quizzes+`?authorId=${authorId}`, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            const quizzes = await response.json();
            return quizzes;
        } catch (e) {
            throw e;
        }
    }

    async getQuizById(quizId) {
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}`, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            const quiz = await response.json();
            return quiz;
        } catch (e) {
            throw e;
        }
    }

    async createQuiz(quizData) {
        try {
            const response = await callApi(endpoints.quizzes, "POST", quizData);
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            if (response.ok) {
                return response;
            }
            return await response.json();
        } catch (e) {
            throw e;
        }
    }

    async modifyQuiz(quizId, quizData) {
        try {
            const response = await callApi(endpoints.quizzes+`/${quizId}`, "PUT", quizData);
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            return await response.json();
        } catch (e) {
            throw e;
        }
    }

    async addQuestionToQuiz(quizId, questionId) {
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "POST");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            return await response.json();
        } catch (e) {
            throw e;
        }
    }

    async removeQuestionFromQuiz(quizId, questionId) {
        try {
            const response = await callApi(`${endpoints.quizzes}/${quizId}/questions/${questionId}`, "DELETE");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            return await response.json();
        } catch (e) {
            throw e;
        }
    }
}