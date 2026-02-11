import "../../components/quiz-editor.js";
import { verifyUser } from "../../js/auth.js";
import { UsersService } from "../../services/users-service.js";

const LOGIN_PAGE = "../login/";

window.onload = async () => {
  const loggedUserData = await verifyUser();

  const pageDiv = document.getElementById("page");
  if (loggedUserData) {
    pageDiv.style.display = "block";
  } else {
    globalThis.location = LOGIN_PAGE;
    return;
  }

  createSearchBar(pageDiv);

  let url = new URL(globalThis.location);
  let searchedUserId = url.searchParams.get("id");

  try {
    const searchedUserData = await new UsersService().getUserInfoById(searchedUserId);
    const profileViewer = document.createElement("profile-viewer");
    pageDiv.appendChild(profileViewer);
    profileViewer.userData =  searchedUserData;
  } catch (e) {
    console.error(e);
    const alert = document.createElement("alert-component");
    alert.setAttribute("type", "danger text-center");
    alert.setAttribute("message", `Cannot find an user with ID '${searchedUserId}'`);
    pageDiv.appendChild(alert);
    return;
  }
};

const createSearchBar = (root) => {
  const form = document.createElement("form");
  form.classList.add("form-control");

  const searchBar = document.createElement("input")
  searchBar.classList.add("form-control")
  searchBar.setAttribute("placeholder", "User ID")

  form.appendChild(searchBar);

  root.appendChild(form);

  form.addEventListener("submit", (event) => {
    event.preventDefault();

    const newUserId = searchBar.value.trim();
    if (!newUserId) {
      return;
    }

    const url = new URL(globalThis.location.href);
    url.searchParams.set("id", newUserId);

    globalThis.location.href = url.toString();
  })
}