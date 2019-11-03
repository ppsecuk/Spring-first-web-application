package com.sda.springmvc.example.controllers;


import com.sda.springmvc.example.entities.User;
import com.sda.springmvc.example.repositories.UserRepository;
import com.sda.springmvc.example.validation.AgeValidationService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
public class RestUserController {

    @Data
    private static class ErrorMessage{
        private final int status;
        private final String error;
        private final String message;
    }

    private final UserRepository userRepository;
    private final AgeValidationService ageValidationService;

    public RestUserController(UserRepository userRepository, AgeValidationService ageValidationService) {
        this.userRepository = userRepository;
        this.ageValidationService = ageValidationService;
    }

    @GetMapping("/greet")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/users")
    public List<User> fetchAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchUserById(@PathVariable long id){
        return userRepository.findById(id)
                .map(this::ok)
                .orElseGet(this::userNotFound);
    }

    private ResponseEntity<User> ok(User user){
        return ResponseEntity.ok(user);
    }

    private ResponseEntity<User> userNotFound(){
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable long id){
        return userRepository.findById(id)
                .map(this::delete)
                .orElseGet(this::userNotFound);
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNewUser(@RequestBody @Valid User user){
        return ageValidationService.isValid(user)
                ? userCreatedResponse(user)
                : invalidAgeResponse();
    }

    @PutMapping(value = "/users/{id}", consumes =  MediaType.APPLICATION_JSON_VALUE)
    public void createOrUpdateExistingUser(@PathVariable long id, @RequestBody @Valid User user){
        user.setId(id);
        userRepository.save(user);
    }

    private ResponseEntity<User> userCreatedResponse(@RequestBody @Valid User user){
        final User savedUser = userRepository.save(user);
        return ResponseEntity.status(CREATED).body(savedUser);
    }

    private ResponseEntity<ErrorMessage> invalidAgeResponse() {
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorMessage(
                        BAD_REQUEST.value(),
                        BAD_REQUEST.getReasonPhrase(),
                        "Age is not valid"));
    }

    private ResponseEntity<User> delete(User user){
        userRepository.delete(user);
        return ResponseEntity.ok(user);
    }
}
