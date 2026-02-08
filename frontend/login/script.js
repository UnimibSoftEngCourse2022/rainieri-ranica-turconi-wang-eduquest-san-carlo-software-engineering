const EMAIL_INPUT_TAG_ID = "email-input";
const PASSWORD_INPUT_TAG_ID = "password-input";
const LOGIN_RESULT_TAG_ID = "login-result";

const LOGIN_ENDPOINT_URL = "http://localhost:8080/api/auth/login";

const LOGIN_ERROR_DIV =
  "<div class='alert alert-danger' role='alert'>Email or password are wrong</div>";

const STUDENT_ROLE = "STUDENT";
const TEACHER_ROLE = "TEACHER";

const handleLoginSubmit = async (event) => {
  event.preventDefault();

  const email = document.getElementById(EMAIL_INPUT_TAG_ID).value;
  const password = document.getElementById(PASSWORD_INPUT_TAG_ID).value;

  const requestBody = { email: email, password: password };
  const response = await fetch(LOGIN_ENDPOINT_URL, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestBody),
  });

  if (response.ok) {
    const data = await response.json();
    const jwtToken = data.token;
    globalThis.localStorage.setItem("token", jwtToken);

    let destination;
    if (data.role == STUDENT_ROLE) {
      destination = "../student-dashboard/";
    } else if (data.role == TEACHER_ROLE) {
      destination = "../teacher-dashboard/";
    }
    globalThis.location = destination;
  } else {
      const resultDiv = document.getElementById(LOGIN_RESULT_TAG_ID);
      resultDiv.innerHTML = LOGIN_ERROR_DIV;
      setTimeout(() => {
          resultDiv.innerHTML = "";
      }, 3000);
  }
};
