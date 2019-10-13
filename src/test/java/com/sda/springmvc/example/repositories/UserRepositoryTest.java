package com.sda.springmvc.example.repositories;

import com.sda.springmvc.example.entities.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("dev")
public class UserRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void entity_manager_must_not_be_null(){
        Assertions.assertThat(em).isNotNull();
}

    @Test
    public void should_save_new_user(){
        User user = new User("John", "box@email.com", "UK");
        Assertions.assertThat(user.getId()).isNotNull();
        System.out.println(user);
    }

    @Test(expected = Exception.class)
    public void should_not_save_two_users_with_same_email(){
        final String email = "box@email.com";
        userRepository.save(new User("John", email, "UK"));
        userRepository.save(new User("Mike", email, "EE"));

        em.flush();
    }

    @Test
    public void should_find_user_by_email(){
        String email = "alice@gmail.com";

        Optional<User> maybeAlice = userRepository.findByEmail(email);

        Assertions.assertThat(maybeAlice).isPresent();
    }

}
