import { callApi, endpoints } from "../js/api.js";

export class GamificationService {
    async getUserMissionsProgresses() {
        try {
            const response = await callApi(endpoints.missionsProgresses, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {

        }
    }

    async getUserCompletedMissions(userId) {
        try {
            const response = await callApi(endpoints.missionsProgresses+`/${userId}?onlyCompleted=true`);
            const completedMissions = await response.json();
            return completedMissions;
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

    async getMyChallenges() {
        try {
            const response = await callApi(endpoints.challenges, "GET");
            const challenges = await response.json();
            return challenges;
        } catch (e) {

        }
    }

    async addChallenge(opponentId, quizId, durationInHours) {
        try {
            const requestBody = {opponentId, quizId, durationInHours};
            const response = await callApi(endpoints.challenges, "POST", requestBody);
            const challenges = await response.json();
            return challenges;
        } catch (e) {

        }
    }
}