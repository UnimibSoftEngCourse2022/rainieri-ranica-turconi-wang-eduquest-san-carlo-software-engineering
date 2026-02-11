import "../../components/quiz-editor.js";
import { verifyUser } from "../../js/auth.js";

const LOGIN_PAGE = "/login/";

const TEACHER_ROLE = "TEACHER";

let userData = null;

window.onload = async () => {
  userData = await verifyUser(TEACHER_ROLE);

  const pageDiv = document.getElementById("page");
  if (userData) {
    pageDiv.style.display = "block";
  } else {
    globalThis.location = LOGIN_PAGE;
  }

  const url = new URL(globalThis.location);
  const quizId = url.searchParams.get("id");
  if (quizId == null) {
    globalThis.location = LOGIN_PAGE;
    return;
  }

  const quizEditorElement = document.createElement("quiz-editor");
  quizEditorElement.setAttribute("id", quizId);
  pageDiv.appendChild(quizEditorElement);
};