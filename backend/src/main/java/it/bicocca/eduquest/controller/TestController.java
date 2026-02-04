package it.bicocca.eduquest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import it.bicocca.eduquest.dto.quiz.TestAddDTO;
import it.bicocca.eduquest.services.TestServices;

@RestController
@RequestMapping("/api/tests") 
public class TestController {

    private final TestServices testServices;

    public TestController(TestServices testServices) {
        this.testServices = testServices;
    }

    @PostMapping
    public ResponseEntity<Object> createTest(@RequestBody TestAddDTO testAddDTO, Authentication authentication) {
        try {
            String userIdString = authentication.getName();
            long teacherId = Long.parseLong(userIdString);
            
            return ResponseEntity.ok(testServices.createTest(testAddDTO, teacherId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore del server: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllTests() {
        return ResponseEntity.ok(testServices.getAllTests());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTestById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(testServices.getTestById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Object> getTestsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(testServices.getTestsByTeacherId(teacherId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTest(@PathVariable Long id) {
        try {
            testServices.deleteTest(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'eliminazione: " + e.getMessage());
        }
    }
}