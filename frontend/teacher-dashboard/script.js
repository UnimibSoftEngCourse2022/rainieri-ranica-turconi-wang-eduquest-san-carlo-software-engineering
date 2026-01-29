import { verifyUser } from "../js/auth.js";
import { Quiz } from "../components/quiz-item.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js";
import { AddQuiz } from "../components/add-quiz.js";


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
        <quizzes-viewer user-id=${userData.id} role="TEACHER"></quizzes-viewer>
        `
    } else {
        window.location.href = LOGIN_PAGE;
    }
};
