import { callApi, endpoints } from "./api.js";

export const verifyUser = async (role) => {
  const response = await callApi(endpoints.auth.me, "GET");

  if (response.ok) {
    const userData = await response.json();

    if (role && userData.role != role) {
      return false;
    } else {
      return userData;
    }
  }
};
