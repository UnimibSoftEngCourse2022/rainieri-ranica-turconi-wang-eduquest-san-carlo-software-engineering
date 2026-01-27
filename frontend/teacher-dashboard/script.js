import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "/login/";

const TEACHER_ROLE = "TEACHER";

const ALL_QUIZZES_ENDPOINT = "http://localhost:8080/api/quiz";

const errorDiv = "<div class='alert alert-danger' role='alert'>An error occoured during the loading of your quizzes, please try again layer</div>";
const emptyQuizListDiv = "<div class='alert alert-warning' role='alert'>You don't have any quiz... create one now!</div>";

let userData = null;

window.onload = async () => {
  userData = await verifyUser(TEACHER_ROLE);

  if (userData) {
    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";

    const titleElement = document.getElementById("title");
    titleElement.innerHTML += " - Welcome " + userData.name;
  } else {
    window.location = LOGIN_PAGE;
  }

  await getTeacherQuizzes();
};

const getTeacherQuizzes = async () => {
  const jwt = window.localStorage.getItem("token");
  const teacher_quizzes_endpoint = ALL_QUIZZES_ENDPOINT + "?authorId=" + userData.id
  const quizzesElement = document.getElementById("quizzes")
  try {
    const response = await fetch(teacher_quizzes_endpoint, {
      method: "GET",
      headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Authorization": "Bearer " + jwt
      }
    });

    if (response.ok) {
      const quizzes = await response.json();
      console.log(quizzes)
      if (quizzes.length == 0) {
        quizzesElement.innerHTML = emptyQuizListDiv;
      }
    } else {
      quizzesElement.innerHTML = errorDiv;
    }
  } catch (e) {
    console.log(e)
    quizzesElement.innerHTML = errorDiv;
  }
}
