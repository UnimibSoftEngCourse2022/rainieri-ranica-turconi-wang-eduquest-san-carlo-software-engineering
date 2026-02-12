import { callApi, endpoints } from "../js/api.js";

export class GamificationService {
    async getUserMissionsProgresses() {
        const response = await callApi(endpoints.missionsProgresses, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const attempts = await response.json();
        return attempts;
    }

    async getUserCompletedMissions(userId) {
        const response = await callApi(endpoints.missionsProgresses+`/${userId}?onlyCompleted=true`);
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const completedMissions = await response.json();
        return completedMissions;
    }

    async getRankingByCompletedQuizzes() {
        const response = await callApi(endpoints.rankings.byCompletedQuizzes, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const attempts = await response.json();
        return attempts;
    }

    async getRankingByAverageScore() {
        const response = await callApi(endpoints.rankings.byAverageScore, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const attempts = await response.json();
        return attempts;
    }

    async getRankingByCorrectAnswers() {
        const response = await callApi(endpoints.rankings.byCorrectAnswers, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const attempts = await response.json();
        return attempts;
    }

    async getMyChallenges() {
        const response = await callApi(endpoints.challenges, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const challenges = await response.json();
        return challenges;
    }

    async addChallenge(opponentId, quizId, durationInHours) {
        const requestBody = {opponentId, quizId, durationInHours};
        const response = await callApi(endpoints.challenges, "POST", requestBody);
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        const challenges = await response.json();
        return challenges;
    }

    async getUserBadges(userId) {
        const url = `${endpoints.badges}/${userId}`;
        const response = await callApi(url, "GET");
        if (!response.ok) {
            const responseBody = await response.text();
            throw new Error(responseBody);
        }
        return await response.json();
    }
}