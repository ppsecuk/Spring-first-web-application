package com.sda.springmvc.example.controllers;

import com.sda.springmvc.example.entities.User;
import com.sda.springmvc.example.events.UserSignupEvent;
import com.sda.springmvc.example.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, ApplicationEventPublisher eventPublisher){
        this.userRepository = userRepository;
        this.applicationEventPublisher = eventPublisher;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @GetMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("user", new User());
        return "user-add";
    }

    @PostMapping("/adduser")
    public String create(@Valid User newUser, BindingResult result, Model model){
        LOG.info("Creating user {}", newUser);

        if(result.hasErrors()) {
            return "user-add";
        }
        userRepository.save(newUser);
        LOG.info("Sending a signup event on thread {}", Thread.currentThread().getName());
        applicationEventPublisher.publishEvent(UserSignupEvent.newInstance(newUser));

        return "redirect:/";
    }

    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable long userId, Model model){
        LOG.info("Editing user {}", userId);

        return userRepository.findById(userId)
                .map(user -> getEditView(model, user))
                .orElseGet(() -> "user-not-found");
    }

    private String getEditView(Model model, User user) {
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/update/{userId}")
    public String update(@PathVariable long userId, @Valid User existingUser, BindingResult result) {

        if(result.hasErrors()) {
            return "user-edit";
        }

        existingUser.setId(userId);
        userRepository.save(existingUser);

        return "redirect:/";
    }

    @GetMapping("/delete/{userId}")
    public String confirmDeletion(@PathVariable long userId, Model model){
        return userRepository.findById(userId)
                .map(user -> getDeleteView(model, user))
                .orElseGet(() -> "user-not-found");
    }

    private String getDeleteView(Model model, User user) {
        model.addAttribute("user", user);
        return "user-delete";
    }

    @PostMapping("/delete/{userId}")
    public String delete(@PathVariable long userId, User user){
        userRepository.deleteById(userId);
        return "redirect:/";
    }
}
