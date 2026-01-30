const USER_DATA_ENDPOINT_URL = "http://localhost:8080/api/users/me";

export const verifyUser = async (role) => {
  const token = window.localStorage.getItem("token");
  if (!token) {
    window.location = LOGIN_PAGE;
    return;
  }

  const response = await fetch(USER_DATA_ENDPOINT_URL, {
    method: "GET",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    },
  });

  if (response.ok) {
    const userData = await response.json();

    if (userData.role != role) {
      return false;
    } else {
      return userData;
    }
  }
};
