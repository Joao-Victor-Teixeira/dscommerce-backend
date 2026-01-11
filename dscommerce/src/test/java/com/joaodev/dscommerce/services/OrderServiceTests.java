package com.joaodev.dscommerce.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joaodev.dscommerce.dto.OrderDTO;
import com.joaodev.dscommerce.entities.Order;
import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.entities.enums.OrderStatus;
import com.joaodev.dscommerce.repositories.OrderRepository;
import com.joaodev.dscommerce.services.exceptions.ResourceNotFoundException;
import com.joaodev.dscommerce.tests.OrderFactory;
import com.joaodev.dscommerce.tests.UserFactory;
import com.joaodev.dscommerce.util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    OrderService service;

    @Mock
    private UserService userService;

    @Mock
    OrderRepository repository;

    @Mock
    AuthService authService;

    @Mock
    private CustomUserUtil userUtil;

    private Order order;

    private OrderDTO dto;

    private User user;

    private Long existingId, nonExistingId;

    @BeforeEach
    void setUp() throws Exception{

        existingId = 1L;
        nonExistingId = 2L;

        user = UserFactory.creatClientUser();
        order = OrderFactory.createOrder(user);
        dto = OrderFactory.createDTO();

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        Mockito.doNothing().when(authService).validateSelfOrAdmin(Mockito.anyLong());
        
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExists(){

        OrderDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), 1L);
        Assertions.assertEquals(result.getStatus(), OrderStatus.PAID );
        Assertions.assertEquals(result.getClient().getName(), "Maria");
    }

}
