package com.winnie.auth.service.security;

import com.winnie.auth.domain.User;
import com.winnie.auth.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MongoUserDetailsServiceTest {

    @InjectMocks
    private MongoUserDetailsService mongoUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setup(){
        initMocks(this);
    }

    @Test
    public void shouldLoadUserByUserName(){
        final User user = new User();
        user.setUsername("test");

        when(userRepository.findById("test")).thenReturn(Optional.of(user));
        UserDetails loaded =  mongoUserDetailsService.loadUserByUsername("test");
        assertEquals(user,loaded);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldFailToLoadByUserNameIfUserNotExists() {
        mongoUserDetailsService.loadUserByUsername("");
    }


}
