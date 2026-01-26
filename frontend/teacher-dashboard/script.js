const USER_DATA_ENDPOINT_URL = "http://localhost:8080/auth/me";
const LOGIN_PAGE = "/login/";

const TEACHER_ROLE = "TEACHER";

window.onload = async () => {
  const token = window.localStorage.getItem("token");
  if (!token) {
    window.location = LOGIN_PAGE;
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

    if (userData.role != TEACHER_ROLE) {
      window.location = LOGIN_PAGE;
      return;
    }

    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";

    const titleElement = document.getElementById("title");
    titleElement.innerHTML += " - Welcome " + userData.name;
  } else {
    window.location = LOGIN_PAGE;
  }
};
