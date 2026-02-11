import { callApi, endpoints } from "../js/api.js";

export class QuestionsService {
    async getQuestions() {
        const response = await callApi(endpoints.questions, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const questions = await response.json();
        return questions;
    }

    async getQuestionByAuthorId(authorId) {
        const response = await callApi(`${endpoints.questions}?authorId=${authorId}`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const quizzes = await response.json();
        return quizzes;
    }

    async createQuestion(formData) {
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
    }
}