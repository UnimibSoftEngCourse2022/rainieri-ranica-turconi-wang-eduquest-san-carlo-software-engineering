import { QuizEditor } from "../components/quiz-editor.js";
import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "../login/";

let userData = null;

window.onload = async () => {
  userData = await verifyUser();

  if (userData) {
    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";
  } else {
    window.location = LOGIN_PAGE;
    return;
  }
};