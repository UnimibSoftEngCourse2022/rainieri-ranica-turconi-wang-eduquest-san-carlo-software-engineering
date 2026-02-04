import { verifyUser } from "../js/auth.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js";
import { TestsViewer } from "../components/tests-viewer.js";

const LOGIN_PAGE = "../login/index.html";
const STUDENT_ROLE = "STUDENT";

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
    <h1>All quizzes</h1>
    <quizzes-viewer role="STUDENT" user-id="${userData.id}"></quizzes-viewer>
    `

    document.getElementById("tests-container").innerHTML = `
    <h1>All tests</h1>
    <tests-viewer role="STUDENT" user-id="${userData.id}"></tests-viewer>
    `

    document.getElementById("quizzes-attempts").innerHTML = `
    <h1>Your quizzes attempts</h1>
    <quizzes-attempts-viewer user-id="${userData.id}"></quizzes-attempts-viewer>
    `
};