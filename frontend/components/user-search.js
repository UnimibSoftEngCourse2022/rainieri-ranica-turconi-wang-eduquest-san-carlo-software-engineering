import { UsersService } from "../services/users-service.js";

export class UserSearch extends HTMLElement {
 
    connectedCallback() {
      this.renderInitialStructure();
      this.userService = new UsersService();
      this.attachEvents(); 
    }

    renderInitialStructure() {
      this.innerHTML = `
      <div style="position: relative;">
          <div class="input-group input-group-sm">
              <input type="number" class="form-control" id="search-input" placeholder="ID Utente">
              <button class="btn btn-primary" id="btn-search" type="button">
                  <i class="bi bi-search"></i>
              </button>
          </div>
          
          <div id="error-msg" class="text-danger small mt-1" style="display: none; position: absolute; top: 100%; right: 0; background: white; padding: 2px;"></div>
  
          <div class="card shadow-sm" id="detail-card" style="display: none; position: absolute; z-index: 1000; width: 250px; right: 0; top: 40px;">
              <div class="card-header bg-dark text-white py-1">
                  <h6 class="mb-0 small">utente trovato<span style="float:right; cursor:pointer;" id="close-card">×</span></h6>
              </div>
              <div class="card-body p-2">
                  <div class="mb-1"><input type="text" class="form-control form-control-sm" id="input-name" readonly></div>
                  <div class="mb-1"><input type="text" class="form-control form-control-sm" id="input-surname" readonly></div>
                  
                  <div class="mb-1"><input type="text" class="form-control form-control-sm" id="input-email" readonly></div>
                  
                  <div class="mb-1"><input type="text" class="form-control form-control-sm" id="input-role" readonly></div>
              </div>
          </div>
      </div>
      `;
    }

    attachEvents() {
        const btn = this.querySelector("#btn-search");
        const input = this.querySelector("#search-input");
        const closeBtn = this.querySelector("#close-card");

        if(closeBtn) {
            closeBtn.addEventListener("click", () => {
                this.querySelector("#detail-card").style.display = "none";
            });
        }

        btn.addEventListener("click", () => {
            this.searchById();
        });

        input.addEventListener("keypress", (e) => {
            if (e.key === "Enter") {
                this.searchById();
            }
        });
    }

    async searchById() {
      const input = this.querySelector("#search-input");
      const id = input.value.trim();
      const errorMsg = this.querySelector("#error-msg");
      const card = this.querySelector("#detail-card");
      
      errorMsg.style.display = "none";
      card.style.display = "none";
  
      if (!id) return; 
  
      try {
          const userData = await this.userService.getUserInfoById(id);
          
          if (userData) {
              this.populateForm(userData); 
          } else {
              this.showError("User not found");
          }
  
      } catch (error) {
          console.error(error);
          this.showError("Errore connessione");
      }
    }
 
    populateForm(data) {
      this.querySelector("#input-name").value = data.name;
      this.querySelector("#input-surname").value = data.surname;
      this.querySelector("#input-email").value = data.email;     
      this.querySelector("#input-role").value = data.role;     
      this.querySelector("#detail-card").style.display = "block";
    }
    
    showError(msg) {
      const errorDiv = this.querySelector("#error-msg");
      errorDiv.textContent = msg;
      errorDiv.style.display = "block";
      setTimeout(() => { errorDiv.style.display = "none"; }, 3000);
    }
}

customElements.define('user-search', UserSearch);