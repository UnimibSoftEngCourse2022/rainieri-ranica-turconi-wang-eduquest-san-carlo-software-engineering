import { verifyUser } from "../js/auth.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js"

const LOGIN_PAGE = "../login/index.html";
const STUDENT_ROLE = "STUDENT";
const ALL_QUIZZES_ENDPOINT = "http://localhost:8080/api/quiz";

const errorDiv = "<div class='alert alert-danger' role='alert'>An error occurred during the loading of quizzes. Please try again later.</div>";
const emptyQuizListDiv = "<div class='alert alert-info' role='alert'>No quizzes available at the moment. Check back later!</div>";

let userData = null;

window.onload = async () => {
    userData = await verifyUser(STUDENT_ROLE);

    if (userData) {
        const pageDiv = document.getElementById("page");
        pageDiv.style.display = "block";

        const titleElement = document.getElementById("title");
        titleElement.innerHTML += " - Welcome " + userData.name;
    } else {
        window.location.href = LOGIN_PAGE;
    }

    document.getElementById("quizzes").innerHTML = `
    <quizzes-viewer role="STUDENT" user-id="${userData.id}"></quizzes-viewer>
    `

    document.getElementById("quizzes-attempts").innerHTML = `
    <quizzes-attempts-viewer user-id="${userData.id}"></quizzes-attempts-viewer>
    `
};
