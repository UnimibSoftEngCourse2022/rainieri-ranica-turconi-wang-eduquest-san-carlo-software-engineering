import { verifyUser } from "../js/auth.js";

const LOGIN_PAGE = "/login/";

const TEACHER_ROLE = "TEACHER";

const ALL_QUIZZES_ENDPOINT = "http://localhost:8080/api/quiz";
const ADD_QUIZ_ENDPOINT = "http://localhost:8080/api/quiz";

const errorDiv = "<div class='alert alert-danger' role='alert'>An error occoured during the loading of your quizzes, please try again layer</div>";
const emptyQuizListDiv = "<div class='alert alert-warning' role='alert'>You don't have any quiz... create one now!</div>";

const addQuizSuccessDiv = "<div class='alert alert-success' role='alert'>Quiz created succesfully!</div>";
const addQuizErrorDiv = "<div class='alert alert-danger' role='alert'>An error occoured during the creation of the quiz</div>";

let userData = null;

window.onload = async () => {
  userData = await verifyUser(TEACHER_ROLE);

  if (userData) {
    const pageDiv = document.getElementById("page");
    pageDiv.style.display = "block";

    const titleElement = document.getElementById("title");
    titleElement.innerHTML += " - Welcome " + userData.name;
  } else {
    window.location = LOGIN_PAGE;
  }

  await getTeacherQuizzes();
};

const getTeacherQuizzes = async () => {
  const jwt = window.localStorage.getItem("token");
  const teacher_quizzes_endpoint = ALL_QUIZZES_ENDPOINT + "?authorId=" + userData.id
  const quizzesElement = document.getElementById("quizzes")
  try {
    const response = await fetch(teacher_quizzes_endpoint, {
      method: "GET",
      headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Authorization": "Bearer " + jwt
      }
    });

    if (response.ok) {
      const quizzes = await response.json();
      if (quizzes.length == 0) {
        quizzesElement.innerHTML = emptyQuizListDiv;
      } else {
        quizzesElement.innerHTML = getQuizzesElement(quizzes);
      }
    } else {
      quizzesElement.innerHTML = errorDiv;
    }
  } catch (e) {
    console.log(e)
    quizzesElement.innerHTML = errorDiv;
  }
}

const getQuizzesElement = (quizzes) => {
  let element = "<h1>Your quizzes</h1>"
  quizzes.forEach(quiz => {
    console.log(quiz);
    element += `<p>${quiz.title}</h1><button class="btn" onclick="">✏️</button>`
  });
  return element
}

document.getElementById("add-quiz-button").addEventListener('click', async () => {
  const jwt = window.localStorage.getItem("token");
  const title = document.getElementById("quiz-title-input").value;
  const description = document.getElementById("quiz-description-input").value;
  
  const requestBody = {
    title, description
  };
  console.log(requestBody);
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
    const r = await response.json();
    console.log(r);
    addQuizResultElement.innerHTML = addQuizSuccessDiv;
  } else {
    addQuizResultElement.innerHTML = addQuizErrorDiv;
  }
})
