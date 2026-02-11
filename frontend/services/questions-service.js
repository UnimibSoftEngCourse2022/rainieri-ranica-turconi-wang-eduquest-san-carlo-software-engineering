import { callApi, endpoints } from "../js/api.js";

export class QuestionsService {
    async getQuestions() {
        try {
            const response = await callApi(endpoints.questions, "GET");
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody);
            }
            const questions = await response.json();
            return questions;
        } catch (e) {
            throw e;
        }
    }

    async getQuestionByAuthorId(authorId) {
        try {
            const response = await callApi(`${endpoints.questions}?authorId=${authorId}`, "GET");
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

    async createQuestion(formData) {
        try {
            const token = localStorage.getItem("token"); 

            const response = await fetch(endpoints.questions, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}` 
                },
                body: formData
            });
            
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