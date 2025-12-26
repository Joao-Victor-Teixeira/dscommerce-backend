package com.joaodev.dscommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joaodev.dscommerce.entities.OrderItem;
import com.joaodev.dscommerce.entities.OrderItemPK;

public interface OrderItemRepositoy extends JpaRepository<OrderItem, OrderItemPK> {

}
