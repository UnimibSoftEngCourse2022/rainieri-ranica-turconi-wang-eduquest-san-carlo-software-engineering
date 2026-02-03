import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class MissionsService {
    async getUserMissionsProgresses(quizAttemptId) {
        try {
            const response = await callApi(endpoints.missionsProgresses, "GET");
            const attempts = await response.json();
            return attempts;
        } catch (e) {

        }
    }
}