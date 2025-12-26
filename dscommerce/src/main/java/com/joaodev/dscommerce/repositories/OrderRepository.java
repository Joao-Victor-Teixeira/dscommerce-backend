package com.joaodev.dscommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joaodev.dscommerce.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
