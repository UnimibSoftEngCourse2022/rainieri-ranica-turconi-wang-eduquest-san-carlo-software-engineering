import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class GamificationService {
    async getUserMissionsProgresses(quizAttemptId) {
        try {
            const response = await callApi(endpoints.missionsProgresses, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {

        }
    }

    async getRankingByCompletedQuizzes() {
        try {
            const response = await callApi(endpoints.rankings.byCompletedQuizzes, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {

        }
    }

    async getRankingByAverageScore() {
        try {
            const response = await callApi(endpoints.rankings.byAverageScore, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {

        }
    }
}