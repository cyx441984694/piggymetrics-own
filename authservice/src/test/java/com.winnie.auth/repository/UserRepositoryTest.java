package com.winnie.auth.repository;


import com.netflix.discovery.converters.Auto;
import com.winnie.auth.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@DataMongoTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveAndFindUserByName(){

        User user = new User();
        user.setUsername("test");
        user.setPassword("password");

        userRepository.save(user);

        Optional<User> found = userRepository.findById(user.getUsername());
        assertTrue(found.isPresent());
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals(user.getPassword(), found.get().getPassword());
    }
}
