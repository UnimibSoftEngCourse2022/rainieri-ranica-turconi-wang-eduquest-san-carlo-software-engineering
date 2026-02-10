import { UsersService } from "../services/users-service.js";

const LOGIN_ENDPOINT_URL = "http://localhost:8080/api/auth/login";

const LOGIN_ERROR_DIV =
  "<div class='alert alert-danger' role='alert'>Email or password are wrong</div>";

const STUDENT_ROLE = "STUDENT";
const TEACHER_ROLE = "TEACHER";

const usersService = new UsersService();

const loginForm = document.querySelector("#login-form");
loginForm.addEventListener("submit", (event) => handleLoginSubmit(event));

const handleLoginSubmit = async (event) => {
  event.preventDefault();

  const email = document.getElementById("email-input").value;
  const password = document.getElementById("password-input").value;

  try {
    const response = await usersService.login(email, password);
    const jwtToken = response.token;
    globalThis.localStorage.setItem("token", jwtToken);

    let destination;
    if (response.role == STUDENT_ROLE) {
      destination = "../student-dashboard/";
    } else if (response.role == TEACHER_ROLE) {
      destination = "../teacher-dashboard/";
    }
    globalThis.location = destination;
  } catch (e) {
    console.error(e);
    const resultDiv = document.getElementById("login-result");
    resultDiv.innerHTML = LOGIN_ERROR_DIV;
    setTimeout(() => {
        resultDiv.innerHTML = "";
    }, 3000);
  }
};
