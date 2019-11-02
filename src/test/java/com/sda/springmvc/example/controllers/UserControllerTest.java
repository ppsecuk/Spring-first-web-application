package com.sda.springmvc.example.controllers;

import com.sda.springmvc.example.entities.User;
import com.sda.springmvc.example.repositories.UserRepository;
import com.sda.springmvc.example.validation.AgeValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AgeValidationService ageValidationService;

    @Test
    public void should_return_main_page() throws Exception {
        mockMvc
                .perform(get("/"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("users"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    public void should_return_signup_page() throws Exception{
        mockMvc
                .perform(get("/signup"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.view().name("user-add"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void should_not_register_user_with_invalid_age() throws Exception{
        mockMvc
                .perform(post("/adduser")
                        .param("dateOfBirth", "2011-12-03"))
                .andExpect(MockMvcResultMatchers.view().name("user-age-not-valid"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(ageValidationService, Mockito.times(1))
                .isValid(any(User.class));
    }

}