package it.bicocca.eduquest.dto.user;

public class UserLoginDTO {
    private String email;
    private String password;

    // Getter e Setter
    public UserLoginDTO() {}
    
    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}