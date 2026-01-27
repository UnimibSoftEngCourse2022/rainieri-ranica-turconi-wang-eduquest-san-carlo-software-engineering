import { verifyUser } from "../js/auth.js";
import { Quiz } from "../components/quiz-item.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js";
import { AddQuiz } from "../components/add-quiz.js";

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