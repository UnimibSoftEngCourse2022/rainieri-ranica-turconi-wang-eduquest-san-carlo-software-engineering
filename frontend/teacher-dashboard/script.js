import { verifyUser } from "../js/auth.js";
import { Quiz } from "../components/quiz.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js";

const LOGIN_PAGE = "/login/";

const TEACHER_ROLE = "TEACHER";

const ADD_QUIZ_ENDPOINT = "http://localhost:8080/api/quiz";

const addQuizSuccessDiv = "<div class='alert alert-success' role='alert'>Quiz created succesfully!</div>";
const addQuizErrorDiv = "<div class='alert alert-danger' role='alert'>An error occoured during the creation of the quiz</div>";

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
};

document.getElementById("add-quiz-button").addEventListener('click', async () => {
  const jwt = window.localStorage.getItem("token");
  const title = document.getElementById("quiz-title-input").value;
  const description = document.getElementById("quiz-description-input").value;
  
  const requestBody = {
    title, description
  };
  console.log(requestBody);
  const response = await fetch(ADD_QUIZ_ENDPOINT, {
    method: "POST",
    headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Authorization": "Bearer " + jwt
    },
    body: JSON.stringify(requestBody)
  });

  const addQuizResultElement = document.getElementById("add-quiz-result");
  if (response.ok) {
    const r = await response.json();
    console.log(r);
    addQuizResultElement.innerHTML = addQuizSuccessDiv;
  } else {
    addQuizResultElement.innerHTML = addQuizErrorDiv;
  }
})
