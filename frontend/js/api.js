export const API_BASE = "http://localhost:8080/api";

export const endpoints = {
    auth: {
        me: `${API_BASE}/users/me`,
        login: `${API_BASE}/auth/login`,
        register: `${API_BASE}/auth/register`,
    },
    users: `${API_BASE}/users`,
    quizzes: `${API_BASE}/quizzes`,
    questions: `${API_BASE}/questions`,
    tests: `${API_BASE}/tests`,
    attempts: `${API_BASE}/quiz-attempts`,
    missions: `${API_BASE}/gamification/missions`,
    missionsProgresses: `${API_BASE}/gamification/missions/progresses`,
    rankings: {
        byCompletedQuizzes: `${API_BASE}/gamification/ranking/quizzesCompleted`,
        byAverageScore: `${API_BASE}/gamification/ranking/averageScore`,
        byCorrectAnswers: `${API_BASE}/gamification/ranking/correctAnswers`
    },
    challenges: `${API_BASE}/gamification/challenges`
}

export const callApi = async(url, method, body = null) => {
    const token = globalThis.localStorage.getItem("token");
    const headers = {
        Accept: "application/json",
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    };

    const options = {
        method,
        headers,
    }
    if (body) {
        options.body = JSON.stringify(body);
    }

    return fetch(url, options);
}