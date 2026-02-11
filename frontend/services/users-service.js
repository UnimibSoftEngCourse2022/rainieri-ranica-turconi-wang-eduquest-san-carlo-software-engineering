import { callApi, endpoints } from "../js/api.js";

export class UsersService {
    async getUserInfoById(userId) {
        const response = await callApi(`${endpoints.users}/${userId}`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const userData = await response.json();
        return userData;
    }

    async getMyUserInfo() {
        const response = await callApi(`${endpoints.users}/me`, "GET");
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const userData = await response.json();
        return userData;
    }

    async register(userData) {
        const response = await callApi(`${endpoints.auth.register}`, "POST", userData);
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const responseData = await response.json();
        return responseData;
    }

    async login(email, password) {
        const requestBody = { email: email, password: password };
        const response = await callApi(`${endpoints.auth.login}`, "POST", requestBody);
        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }
        const responseData = await response.json();
        return responseData;
    }
}