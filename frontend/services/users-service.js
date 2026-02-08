import { callApi, endpoints } from "../js/api.js";

export class UsersService {
    async getUserInfoById(userId) {
        try {
            const response = await callApi(`${endpoints.users}/${userId}`, "GET");
            const userData = await response.json();
            return userData;
        } catch (e) {}
    }

    async getMyUserInfo() {
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