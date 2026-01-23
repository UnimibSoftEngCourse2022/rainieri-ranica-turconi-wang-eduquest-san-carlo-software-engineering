package it.bicocca.eduquest.controller;

import it.bicocca.eduquest.dto.UserDTO;
import it.bicocca.eduquest.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServices userServices;

    // Create user and save it in the database
   
    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO inputPayload) {
        // Chiamiamo il servizio vero che hai appena scritto!
        return userServices.createUser(inputPayload.getName(), inputPayload.getEmail());
    }
}