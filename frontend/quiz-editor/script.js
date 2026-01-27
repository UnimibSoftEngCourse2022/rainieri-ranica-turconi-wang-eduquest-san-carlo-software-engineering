import { QuizEditor } from "../components/quiz-editor.js";
import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "/login/";

const TEACHER_ROLE = "TEACHER";

let userData = null;

window.onload = async () => {
  userData = await verifyUser(TEACHER_ROLE);

  if (userData) {
    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";
  } else {
    window.location = LOGIN_PAGE;
  }
};