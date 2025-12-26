package com.joaodev.dscommerce.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joaodev.dscommerce.dto.OrderDTO;
import com.joaodev.dscommerce.dto.OrderItemDTO;
import com.joaodev.dscommerce.entities.Order;
import com.joaodev.dscommerce.entities.OrderItem;
import com.joaodev.dscommerce.entities.Product;
import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.entities.enums.OrderStatus;
import com.joaodev.dscommerce.repositories.OrderItemRepositoy;
import com.joaodev.dscommerce.repositories.OrderRepository;
import com.joaodev.dscommerce.repositories.ProductRepository;
import com.joaodev.dscommerce.services.exceptions.ResourceNotFoundException;


@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepositoy orderItemRepositoy;

    @Autowired 
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id){
        Order order = repository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Recurso não encontrado"));
        authService.validateSelfOrAdmin(order.getClient().getId());    
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        
        Order order = new Order();

        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAINTING_PAYMENT);
        
        User user = userService.autenticated();
        order.setClient(user);

        for (OrderItemDTO itemDTO : dto.getItems()) {
           Product product = productRepository.getReferenceById(itemDTO.getProductId());
           OrderItem item = new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice());
           order.getItems().add(item); 
        }

        repository.save(order);
        orderItemRepositoy.saveAll(order.getItems());
        return new OrderDTO(order);
    }

}
