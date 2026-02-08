import "../components/quiz-editor.js";
import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "../login/";

let userData = null;

window.onload = async () => {
  userData = await verifyUser("STUDENT");

  const pageDiv = document.getElementById("page");
  if (userData) {
    pageDiv.style.display = "block";
  } else {
    globalThis.location = LOGIN_PAGE;
    return;
  }

  const url = new URL(globalThis.location);
  const quizId = url.searchParams.get("quizAttemptId");
  if (quizId == null) {
    globalThis.location = LOGIN_PAGE;
    return;
  }
  
  const quizEditorElement = document.createElement("quiz-runner");
  quizEditorElement.setAttribute("quiz-attempt-id", quizId);
  pageDiv.appendChild(quizEditorElement);
};