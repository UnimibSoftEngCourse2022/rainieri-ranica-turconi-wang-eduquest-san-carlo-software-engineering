package it.bicocca.eduquest.dto.user;

public class UserLoginResponseDTO {
    private String token;

    public UserLoginResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}