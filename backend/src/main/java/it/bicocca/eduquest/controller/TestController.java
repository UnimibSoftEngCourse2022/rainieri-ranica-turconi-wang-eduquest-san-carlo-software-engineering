package it.bicocca.eduquest.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

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