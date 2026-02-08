import "../components/quiz-editor.js";
import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "/login/";

let userData = null;

window.onload = async () => {
  userData = await verifyUser();

  if (userData) {
    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";
  } else {
    globalThis.location = LOGIN_PAGE;
  }

  const rankingElement = document.querySelector("ranking-viewer");

  const rankingButtons = document.querySelectorAll('button[id^="button-"]');
  rankingButtons.forEach(button => {
    const rankingType = button.id.replace('button-', '');
    button.addEventListener("click", () => {
      rankingElement.rankingType = rankingType;
    });
  });
};
