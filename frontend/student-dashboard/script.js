import { verifyUser } from "../js/auth.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js";
import { TestsViewer } from "../components/tests-viewer.js";
import "../components/quiz-runner.js"; 

const LOGIN_PAGE = "../login/index.html";
const STUDENT_ROLE = "STUDENT";

let userData = null;

window.onload = async () => {
    userData = await verifyUser(STUDENT_ROLE);

    if (!userData) {
        window.location.href = LOGIN_PAGE;
        return;
    }

    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";

    const urlParams = new URLSearchParams(window.location.search);
    const view = urlParams.get('view');

    if (view === 'runner') {
        pageDiv.innerHTML = "";

        const runner = document.createElement("quiz-runner");
        runner.setAttribute("student-id", userData.id);
        pageDiv.appendChild(runner);

        const backContainer = document.createElement("div");
        backContainer.className = "container mt-3 text-center";
        backContainer.innerHTML = `<button class="btn btn-secondary">Back to Dashboard</button>`;
        backContainer.onclick = () => {
            window.location.href = window.location.pathname; 
        };
        pageDiv.appendChild(backContainer);

    } else {
        const titleElement = document.getElementById("title");
        if (titleElement) {
            titleElement.innerHTML += " - Welcome " + userData.name;
        }

        const quizzesDiv = document.getElementById("quizzes");
        if (quizzesDiv) {
            quizzesDiv.innerHTML = `
            <h1>All quizzes</h1>
            <quizzes-viewer role="STUDENT" user-id="${userData.id}"></quizzes-viewer>
            `;
        }

        const testsDiv = document.getElementById("tests-container");
        if (testsDiv) {
            testsDiv.innerHTML = `
            <h1>All tests</h1>
            <tests-viewer role="STUDENT" user-id="${userData.id}"></tests-viewer>
            `;
        }

        const attemptsDiv = document.getElementById("quizzes-attempts");
        if (attemptsDiv) {
            attemptsDiv.innerHTML = `
            <h1>Your quizzes attempts</h1>
            <quizzes-attempts-viewer user-id="${userData.id}"></quizzes-attempts-viewer>
            `;
        }
    }
};