package com.joaodev.dscommerce.services;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joaodev.dscommerce.dto.UserDTO;
import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.projections.UserDetailsProjection;
import com.joaodev.dscommerce.repositories.UserRepository;
import com.joaodev.dscommerce.tests.UserDetailsFactory;
import com.joaodev.dscommerce.tests.UserFactory;
import com.joaodev.dscommerce.util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String existingUsername, nonExistingUsername;

    private User user;

    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp() throws Exception{

        existingUsername = "maria@gmail.com";
        nonExistingUsername = "user@gemail.com";

        user = UserFactory.createCustomClientUser(1L, existingUsername);
        
        userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);

        Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());

        Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserNotAuthenticated(){

        Mockito.when(userUtil.getLoggedUserName()).thenThrow(ClassCastException.class);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.getMe();
        });
    }
    
    @Test
    public void getMeShouldReturnUserDTOWhenUserAuthenticated() {

    Mockito.when(userUtil.getLoggedUserName()).thenReturn("maria@gmail.com");
    
    Mockito.when(repository.findByEmail(anyString())).thenReturn(Optional.of(user));

    UserDTO result = service.getMe();

    Assertions.assertNotNull(result);
    Assertions.assertEquals(result.getEmail(), existingUsername);
}

    @Test
    public void autenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist(){

        Mockito.when(userUtil.getLoggedUserName()).thenThrow(ClassCastException.class);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.autenticated();
        });
    }

    @Test
    public void authenticadeShouldReturnUserWhenUserExists(){

        Mockito.when(userUtil.getLoggedUserName()).thenReturn(existingUsername);

        User result = service.autenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), 1L);
        Assertions.assertEquals(result.getUsername(), existingUsername);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists(){

        UserDetails result = service.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingUsername);
    }

    @Test
    public void loadUserByUsernameShouldThrowUserNotFoundExceptionWhenUserDoesNotExist(){

        Assertions.assertThrows(UsernameNotFoundException.class, () ->{
            service.loadUserByUsername(nonExistingUsername);
        });
    }
}
