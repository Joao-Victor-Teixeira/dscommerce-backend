package com.joaodev.dscommerce.tests;

import java.time.Instant;

import com.joaodev.dscommerce.dto.OrderDTO;
import com.joaodev.dscommerce.entities.Order;
import com.joaodev.dscommerce.entities.Payment;
import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.entities.enums.OrderStatus;

public class OrderFactory {

    public static Order createOrder(User client) {
        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, client, null);
        Payment payment = new Payment(1L, Instant.now(), order);
        order.setPayment(payment);

        return order;
    }

    public static OrderDTO createDTO(){

        User client = UserFactory.creatClientUser();
        Order order = createOrder(client);
        return new OrderDTO(order);
    }
}
