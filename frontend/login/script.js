const EMAIL_INPUT_TAG_ID = "email-input";
const PASSWORD_INPUT_TAG_ID = "password-input";
const LOGIN_RESULT_TAG_ID = "login-result";

const LOGIN_ENDPOINT_URL = "http://localhost:8080/auth/login";

const LOGIN_ERROR_DIV =
  "<div class='alert alert-danger' role='alert'>Email or password are wrong</div>";

const handleLoginSubmit = async (event) => {
  event.preventDefault();

  email = document.getElementById(EMAIL_INPUT_TAG_ID).value;
  password = document.getElementById(PASSWORD_INPUT_TAG_ID).value;

  requestBody = { email: email, password: password };
  const response = await fetch(LOGIN_ENDPOINT_URL, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestBody),
  });

  if (response.status == 403) {
    document.getElementById(LOGIN_RESULT_TAG_ID).innerHTML = LOGIN_ERROR_DIV;
  }
};
