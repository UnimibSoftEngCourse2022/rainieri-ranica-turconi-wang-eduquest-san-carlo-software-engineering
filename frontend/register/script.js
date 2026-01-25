const NAME_INPUT_TAG_ID = "name-input";
const SURNAME_INPUT_TAG_ID = "surname-input";
const EMAIL_INPUT_TAG_ID = "email-input";
const PASSWORD_INPUT_TAG_ID = "password-input";
const REGISTER_RESULT_TAG_ID = "register-result";

const REGISTER_ENDPOINT_URL = "http://localhost:8080/auth/register";

const REGISTER_SUCCESS_DIV =
  "<div class='alert alert-success' role='alert'>Registration has completed successfully! Now you can <a href='../login/'>login</a></div>";
const REGISTER_ERROR_DIV =
  "<div class='alert alert-danger' role='alert'>Registration error, please check your data</div>";
const REGISTER_CONNECTION_ERROR_DIV =
  "<div class='alert alert-danger' role='alert'>Connection to the server failed, please try again later</div>";

const handleRegisterSubmit = async (event) => {
  event.preventDefault();

  const name = document.getElementById(NAME_INPUT_TAG_ID).value;
  const surname = document.getElementById(SURNAME_INPUT_TAG_ID).value;
  const email = document.getElementById(EMAIL_INPUT_TAG_ID).value;
  const password = document.getElementById(PASSWORD_INPUT_TAG_ID).value;
  const role = document.querySelector("input[name='user-role']:checked").value;

  const resultContainer = document.getElementById(REGISTER_RESULT_TAG_ID);

  const requestBody = {
    name,
    surname,
    email,
    password,
    role,
  };

  try {
    const response = await fetch(REGISTER_ENDPOINT_URL, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestBody),
    });

    if (response.ok) {
      resultContainer.innerHTML = REGISTER_SUCCESS_DIV;
    } else {
      resultContainer.innerHTML = REGISTER_ERROR_DIV;
    }
  } catch (error) {}
};
