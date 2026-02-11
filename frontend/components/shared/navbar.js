import { BaseComponent } from "../base-component.js";
import { UsersService } from "../../services/users-service.js";

export class Navbar extends BaseComponent {
    setupComponent() {
      this.usersService = new UsersService();
      this.render();
    }

    async render() {
      let user;
      try {
        user = await this.usersService.getMyUserInfo();
      } catch (e) {
        console.error(e);
        globalThis.location = `../`;
        return;
      }

      const homeUrl = user.role == "TEACHER" ? `../teacher-dashboard` : `../student-dashboard`;
      const profileUrl = `../profile/?id=${user.id}`
      const rankingsUrl = `../rankings/`

      this.innerHTML = `
      <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">EduQuest</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
              <li class="nav-item">
                <a class="nav-link active" aria-current="page" href="${homeUrl}">Home</a>
              </li>
              <li class="nav-item">
                <a class="nav-link active" aria-current="page" href=${profileUrl}>Profile</a>
              </li>
              <li class="nav-item">
                <a class="nav-link active" aria-current="page" href=${rankingsUrl}>Rankings</a>
              </li>
              <li class="nav-item">
                <a class="nav-link active" aria-current="page" href="#" id="logout-button">Logout</a>
              </li>
            </ul>
          </div>
        </div>
      </nav>
      `
    }

    attachEventListeners() {
      this.addEventListener("click", (event) => {
        if (event.target.closest("#logout-button")) {
          this.handleLogout();
        }
      })
    }

    handleLogout() {
      globalThis.localStorage.removeItem("token");
      globalThis.location = `../`;
    }
}
customElements.define('navbar-item', Navbar);