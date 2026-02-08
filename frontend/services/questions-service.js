import { callApi, endpoints } from "../js/api.js";

export class QuestionsService {
    async getQuestions() {
        try {
            const response = await callApi(endpoints.questions, "GET");
            const questions = await response.json();
            return questions;
        } catch (e) {
        }
    }

    async getQuestionByAuthorId(authorId) {
        try {
            const response = await callApi(`${endpoints.questions}?authorId=${authorId}`, "GET");
            const quizzes = await response.json();
            return quizzes;
        } catch (e) {
        }
    }

    /* async createQuestion(questionData) {
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
    } */

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
            if (response.ok) {
            } else {
                console.error("Errore server:", await response.text())
            }
            return await response.json();
        } catch (e) {
        }
    }
}