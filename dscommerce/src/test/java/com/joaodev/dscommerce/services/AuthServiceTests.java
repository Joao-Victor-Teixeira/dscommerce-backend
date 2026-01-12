package com.joaodev.dscommerce.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.services.exceptions.ForbiddenException;
import com.joaodev.dscommerce.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService service;

    @Mock
    private UserService userService;

    private User admin, selfUser, otherUser;

    @BeforeEach
    void setUp() throws Exception {
        admin = UserFactory.creatAdminUser(); 
        selfUser = UserFactory.creatClientUser();
        otherUser = UserFactory.creatClientUser();
        otherUser.setId(3L); 
    }


    @Test
    public void validateSelfOrAdminShouldDoNothingWhenAdminLogged() {
       
        User admin = UserFactory.creatAdminUser(); 
        Mockito.when(userService.autenticated()).thenReturn(admin);

        Assertions.assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(99L); 
        });
    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {
   
        User me = UserFactory.creatClientUser();
        long myId = me.getId();
        Mockito.when(userService.autenticated()).thenReturn(me);

        Assertions.assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(myId); 
        });
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenOtherLogged() {
        
        User me = UserFactory.creatClientUser();
        me.setId(1L); 
        long otherId = 2L;
        Mockito.when(userService.autenticated()).thenReturn(me);

        Assertions.assertThrows(ForbiddenException.class, () -> {
            service.validateSelfOrAdmin(otherId); 
        });
    }
}
