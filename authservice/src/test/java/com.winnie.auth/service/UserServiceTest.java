package com.winnie.auth.service;


import com.winnie.auth.domain.User;
import com.winnie.auth.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void createUserName() {


        final User saved = new User();
        saved.setUsername("test");
        saved.setPassword("password");

        userService.create(saved);
        verify(userRepository, times(1)).save(saved);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenUserAlreadyExists(){

        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(new User()));
        userService.create(user);
    }
}
