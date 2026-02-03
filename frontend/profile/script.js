import { QuizEditor } from "../components/quiz-editor.js";
import { verifyUser } from "../js/auth.js";
import { UsersService } from "../services/users-service.js";

const LOGIN_PAGE = "../login/";

let userData = null;

window.onload = async () => {
  const loggedUserData = await verifyUser();

  const pageDiv = document.getElementById("page");
  if (loggedUserData) {
    pageDiv.style.display = "block";
  } else {
    window.location = LOGIN_PAGE;
    return;
  }

  var url = new URL(window.location);
  var searchedUserId = url.searchParams.get("id");

  const searchedUserData = await new UsersService().getUserInfoById(searchedUserId);

  createSearchBar(pageDiv);

  const profileViewer = document.createElement("profile-viewer");
  pageDiv.appendChild(profileViewer);
  profileViewer.userData =  searchedUserData;
};

const createSearchBar = (root) => {
  const form = document.createElement("form");
  form.classList.add("form-control");

  const searchBar = document.createElement("input")
  searchBar.classList.add("form-control")
  searchBar.setAttribute("placeholder", "User ID")
  
  form.appendChild(searchBar);

  root.appendChild(searchBar);
}