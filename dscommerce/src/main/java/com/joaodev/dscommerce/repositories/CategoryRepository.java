package com.joaodev.dscommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joaodev.dscommerce.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
