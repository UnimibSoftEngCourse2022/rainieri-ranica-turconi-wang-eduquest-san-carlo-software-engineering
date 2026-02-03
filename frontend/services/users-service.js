import { callApi, endpoints } from "../js/api.js";
import { appStore } from "../js/store.js";

export class UsersService {
    async getUserInfoById(userId) {
        appStore.updateAppState({ loading: true });
        try {
            const response = await callApi(`${endpoints.users}/${userId}`, "GET");
            const userData = await response.json();
            appStore.updateAppState({ loading: false });
            return userData;
        } catch (e) {
            appStore.updateAppState({ loading: false, error: true });
        }
    }
}