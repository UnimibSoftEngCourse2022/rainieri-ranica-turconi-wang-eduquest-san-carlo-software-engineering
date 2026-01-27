import { verifyUser } from "../js/auth.js";
import { Quiz } from "../components/quiz-item.js";
import { QuizzesViewer } from "../components/quizzes-viewer.js";
import { AddQuiz } from "../components/add-quiz.js";

const LOGIN_PAGE = "/login/index.html";
const TEACHER_ROLE = "TEACHER";
const ADD_QUIZ_ENDPOINT = "http://localhost:8080/api/quiz";

const addQuizSuccessDiv = "<div class='alert alert-success' role='alert'>Quiz created successfully!</div>";
const addQuizErrorDiv = "<div class='alert alert-danger' role='alert'>An error occurred during the creation of the quiz</div>";

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
    } else {
        window.location.href = LOGIN_PAGE;
    }
};

document.getElementById("add-quiz-button").addEventListener('click', async () => {
    const jwt = window.localStorage.getItem("token");
    const title = document.getElementById("quiz-title-input").value;
    const description = document.getElementById("quiz-description-input").value;

    const requestBody = {
        title,
        description
    };

    try {
        const response = await fetch(ADD_QUIZ_ENDPOINT, {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
                "Authorization": "Bearer " + jwt
            },
            body: JSON.stringify(requestBody)
        });

        const addQuizResultElement = document.getElementById("add-quiz-result");
        if (response.ok) {
            addQuizResultElement.innerHTML = addQuizSuccessDiv;
        } else {
            addQuizResultElement.innerHTML = addQuizErrorDiv;
        }
    } catch (e) {
        console.error(e);
        document.getElementById("add-quiz-result").innerHTML = addQuizErrorDiv;
    }
});

