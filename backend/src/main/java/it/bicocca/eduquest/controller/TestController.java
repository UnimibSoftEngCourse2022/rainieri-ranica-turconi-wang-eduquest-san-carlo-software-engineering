package it.bicocca.eduquest.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "Pong! EduQuest è attivo.";
    }

    @PostMapping("/user")
    public Map<String, String> createUser(@RequestBody Map<String, String> userData) {
        String nome = userData.get("name");
        System.out.println("Ricevuto utente: " + nome);
        
        return Map.of(
            "status", "success",
            "message", "Utente " + nome + " creato (finto!)"
        );
    }
}