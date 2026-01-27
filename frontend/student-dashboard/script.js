import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "/login/index.html";
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

        const showAddBtn = document.getElementById("show-add-question-btn");
        if (showAddBtn) {
            showAddBtn.addEventListener('click', () => {
                document.getElementById('page').style.display = 'none';
                document.getElementById('add-question-container').style.display = 'block';
            });
        }

        const addQuestionComponent = document.querySelector('add-question');
        if (addQuestionComponent) {
            addQuestionComponent.addEventListener('operation-completed', () => {
                document.getElementById('add-question-container').style.display = 'none';
                document.getElementById('page').style.display = 'block';
                
                getAllQuizzes(); 
            });
        }

        await getAllQuizzes();
    } else {
        window.location.href = LOGIN_PAGE;
    }
};

const getAllQuizzes = async () => {
    const jwt = window.localStorage.getItem("token");
    const quizzesElement = document.getElementById("quizzes");
    const endpoint = ALL_QUIZZES_ENDPOINT; 

    try {
        const response = await fetch(endpoint, {
            method: "GET",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
                "Authorization": "Bearer " + jwt
            }
        });

        if (response.ok) {
            const quizzes = await response.json();
            if (quizzes.length === 0) {
                quizzesElement.innerHTML = emptyQuizListDiv;
            } else {
                quizzesElement.innerHTML = getQuizzesElement(quizzes);
            }
        } else {
            quizzesElement.innerHTML = errorDiv;
        }
    } catch (e) {
        console.error(e);
        quizzesElement.innerHTML = errorDiv;
    }
};

const getQuizzesElement = (quizzes) => {
    let element = "<h2>Available Quizzes</h2><div class='list-group'>";
    
    quizzes.forEach(quiz => {
        element += `
            <div class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="mb-1">${quiz.title}</h5>
                    <p class="mb-1">${quiz.description || "No description provided."}</p>
                </div>
                <button class="btn btn-success" onclick="startQuiz(${quiz.id})">
                    ▶ Start
                </button>
            </div>`;
    });
    
    element += "</div>";
    return element;
};

window.startQuiz = (quizId) => {
    console.log("Starting quiz ID:", quizId);
    alert("Start Quiz feature: Redirecting to quiz " + quizId);
};