import { verifyUser } from "../js/auth.js";
import "../components/quiz-item.js";
import "../components/quizzes-viewer.js";
import "../components/tests-viewer.js";
import "../components/add-quiz.js";
import "../components/add-test.js";


const LOGIN_PAGE = "../login/index.html";
const TEACHER_ROLE = "TEACHER";

let userData = null;

window.onload = async () => {
    userData = await verifyUser(TEACHER_ROLE);

    if (userData) {
        const pageDiv = document.getElementById("page");
        pageDiv.style.display = "block";

        const titleElement = document.getElementById("title");
        titleElement.innerHTML += " - Welcome " + userData.name;

        const showAddQuestionBtn = document.getElementById("show-add-question-btn");
        if (showAddQuestionBtn) {
            showAddQuestionBtn.addEventListener('click', () => {
                document.getElementById('page').style.display = 'none';
                document.getElementById('add-question-container').style.display = 'block';
            });
        }

        const addQuestionComponent = document.querySelector('add-question');
        if (addQuestionComponent) {
            addQuestionComponent.addEventListener('operation-completed', () => {
                document.getElementById('add-question-container').style.display = 'none';
                document.getElementById('page').style.display = 'block';
            });
        }

        document.getElementById("quizzes-container").innerHTML = `
        <h1>Your quizzes</h1>
        <quizzes-viewer user-id=${userData.id} role="TEACHER"></quizzes-viewer>
        `

        document.getElementById("tests-container").innerHTML = `
        <h1>Your tests</h1>
        <tests-viewer user-id=${userData.id} role="TEACHER"></tests-viewer>
        `
    } else {
        globalThis.location.href = LOGIN_PAGE;
    }
};