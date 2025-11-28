package com.joaodev.dscommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joaodev.dscommerce.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
