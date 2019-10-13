package com.sda.springmvc.example.entities;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @Length(min=2, max = 4)
    private String country;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    public User(String name, String email, String country) {
        this.name = name;
        this.email = email;
        this.country = country;
    }

}
