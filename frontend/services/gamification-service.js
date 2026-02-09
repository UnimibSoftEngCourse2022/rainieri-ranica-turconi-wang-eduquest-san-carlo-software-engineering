import { callApi, endpoints } from "../js/api.js";

export class GamificationService {
    async getUserMissionsProgresses() {
        try {
            const response = await callApi(endpoints.missionsProgresses, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            const attempts = await response.json();
            return attempts;
        } catch (e) {
            throw new Error(e);
        }
    }

    async getUserCompletedMissions(userId) {
        try {
            const response = await callApi(endpoints.missionsProgresses+`/${userId}?onlyCompleted=true`);
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            const completedMissions = await response.json();
            return completedMissions;
        } catch (e) {
            throw new Error(e);
        }
    }

    async getRankingByCompletedQuizzes() {
        try {
            const response = await callApi(endpoints.rankings.byCompletedQuizzes, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            const attempts = await response.json();
            return attempts;
        } catch (e) {
            throw new Error(e);
        }
    }

    async getRankingByAverageScore() {
        try {
            const response = await callApi(endpoints.rankings.byAverageScore, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            const attempts = await response.json();
            return attempts;
        } catch (e) {
            throw new Error(e);
        }
    }

    async getMyChallenges() {
        try {
            const response = await callApi(endpoints.challenges, "GET");
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            const challenges = await response.json();
            return challenges;
        } catch (e) {
            throw new Error(e);
        }
    }

    async addChallenge(opponentId, quizId, durationInHours) {
        try {
            const requestBody = {opponentId, quizId, durationInHours};
            const response = await callApi(endpoints.challenges, "POST", requestBody);
            if (!response.ok) {
                const responseBody = await response.text();
                throw new Error(responseBody);
            }
            const challenges = await response.json();
            return challenges;
        } catch (e) {
            throw new Error(e);
        }
    }
}