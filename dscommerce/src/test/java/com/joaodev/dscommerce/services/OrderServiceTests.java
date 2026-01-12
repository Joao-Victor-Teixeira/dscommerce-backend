package com.joaodev.dscommerce.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.ArrayList;
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
import com.joaodev.dscommerce.dto.OrderItemDTO;
import com.joaodev.dscommerce.entities.Order;
import com.joaodev.dscommerce.entities.Product;
import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.entities.enums.OrderStatus;
import com.joaodev.dscommerce.repositories.OrderItemRepositoy;
import com.joaodev.dscommerce.repositories.OrderRepository;
import com.joaodev.dscommerce.repositories.ProductRepository;
import com.joaodev.dscommerce.services.exceptions.ResourceNotFoundException;
import com.joaodev.dscommerce.tests.OrderFactory;
import com.joaodev.dscommerce.tests.ProductFactory;
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

    @Mock
    private OrderItemRepositoy orderItemRepositoy;

    @Mock
    private ProductRepository productRepository;

    private Product product;

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

        product = ProductFactory.createProduct();

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        Mockito.doNothing().when(authService).validateSelfOrAdmin(Mockito.anyLong());
        
        Mockito.when(userService.autenticated()).thenReturn(user);
        Mockito.when(productRepository.getReferenceById(anyLong())).thenReturn(product);
        Mockito.when(repository.save(any())).thenReturn(order);
        Mockito.when(orderItemRepositoy.saveAll(any())).thenReturn(new ArrayList<>());
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAdminLogged(){

        User user = UserFactory.creatAdminUser();
        Mockito.when(userService.autenticated()).thenReturn(user);
        
        dto.getItems().add(new OrderItemDTO(1L, "Iphone", 100.0, 2, "img"));

        OrderDTO result = service.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStatus(), OrderStatus.WAINTING_PAYMENT);
        Assertions.assertEquals(result.getClient().getName(), user.getName());
        
        Assertions.assertEquals(result.getTotal(), 10000.0);
        Assertions.assertEquals(result.getItems().size(), 1);
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
