window.onload = () => {
  const loginPage = "/login/";
  if (!window.localStorage.getItem("token")) {
    window.location = loginPage;
  }
};
