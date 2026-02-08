import { verifyUser } from "../js/auth.js";
import "../components/quizzes-viewer.js";
import "../components/tests-viewer.js";
import "../components/quiz-runner.js"; 
import "../components/quizzes-attempts-viewer.js";
import "../components/questions-viewer.js";
import "../components/add-question.js";
import "../components/missions-viewer.js";
import "../components/challenges-viewer.js";  
import "../components/add-challenge.js";  
import "../components/shared/navbar.js";

const LOGIN_PAGE = "../login/index.html";
const STUDENT_ROLE = "STUDENT";

let userData = null;

const handleNavigation = () => {
    const pageDiv = document.getElementById("page");
    const isRunner = globalThis.location.hash === '#quiz-runner';
    
    Array.from(pageDiv.children).forEach(child => {
        if (child.tagName !== 'QUIZ-RUNNER') {
            child.style.display = isRunner ? 'none' : '';
        }
    });

    const oldRunner = pageDiv.querySelector("quiz-runner");
    if (oldRunner) oldRunner.remove();

    if (isRunner) {
        const runner = document.createElement("quiz-runner");
        runner.setAttribute("student-id", userData.id);
        pageDiv.appendChild(runner);
    }
};

window.onload = async () => {
    userData = await verifyUser(STUDENT_ROLE);
    if (!userData) { globalThis.location.href = LOGIN_PAGE; return; }

    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";
    
    const title = document.getElementById("title");
    if(title) title.innerHTML += ` - Welcome ${userData.name}`;

    const quizzesDiv = document.getElementById("quizzes");
    if (quizzesDiv) quizzesDiv.innerHTML = `<h1>All quizzes</h1><quizzes-viewer role="STUDENT" user-id="${userData.id}"></quizzes-viewer>`;

    const testsDiv = document.getElementById("tests-container");
    if (testsDiv) testsDiv.innerHTML = `<h1>All tests</h1><tests-viewer role="STUDENT" user-id="${userData.id}"></tests-viewer>`;

    const attemptsDiv = document.getElementById("quizzes-attempts");
    if (attemptsDiv) attemptsDiv.innerHTML = `<h1>Your quizzes attempts</h1><quizzes-attempts-viewer user-id="${userData.id}"></quizzes-attempts-viewer>`;

    handleNavigation();
    window.addEventListener("hashchange", handleNavigation);
};