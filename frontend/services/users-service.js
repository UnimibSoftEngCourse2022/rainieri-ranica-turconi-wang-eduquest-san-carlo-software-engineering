import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class UsersService {
    async getUserInfoById(userId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.users}/${userId}`, "GET");
            const userData = await response.json();
            return userData;
        } catch (e) {}
    }

    async getMyUserInfo() {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.users}/me`, "GET");
            const userData = await response.json();
            return userData;
        } catch (e) {}
    }

    async register(userData) {
        try {
            const response = await callApi(`${endpoints.auth.register}`, "POST", userData);
            const responseData = await response.json();
            return responseData;
        } catch (e) {}
    }
}