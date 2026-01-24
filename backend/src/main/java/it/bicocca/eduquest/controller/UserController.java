package it.bicocca.eduquest.controller;

import org.springframework.web.bind.annotation.*;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.services.UserServices;
import java.util.List;

@RestController
@RequestMapping("/api/users") // Base URL for user-related operations
public class UserController {

    private final UserServices userService;

    // Constructor Injection
    public UserController(UserServices userService) {
        this.userService = userService;
    }

    // register a new user
    @PostMapping("/register")
    public User registerUser(@RequestBody User newUser) {
        // We use the instance 'userService', not the class name
        return userService.registerUser(newUser);
    }

    // Endpoint to GET ALL users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}