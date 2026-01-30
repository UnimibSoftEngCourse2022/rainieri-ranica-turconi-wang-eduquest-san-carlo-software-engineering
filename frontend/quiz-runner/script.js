import { QuizEditor } from "../components/quiz-editor.js";
import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "../login/";

let userData = null;

window.onload = async () => {
  userData = await verifyUser("STUDENT");

  if (userData) {
    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";
  } else {
    window.location = LOGIN_PAGE;
    return;
  }

  // FIXME if id doesn't exit, go to another page
  const url = new URL(window.location);
  const quizId = url.searchParams.get("quizAttemptId");
  if (quizId == null) {
    window.location = LOGIN_PAGE;
    return;
  }

  document.getElementById("page").innerHTML = `
  <quiz-runner quiz-attempt-id="${quizId}"></quiz-runner>
  `
};