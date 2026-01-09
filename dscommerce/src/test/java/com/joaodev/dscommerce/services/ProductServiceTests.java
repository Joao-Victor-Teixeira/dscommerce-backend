package com.joaodev.dscommerce.services;

import static org.mockito.ArgumentMatchers.notNull;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joaodev.dscommerce.dto.ProductDTO;
import com.joaodev.dscommerce.entities.Product;
import com.joaodev.dscommerce.repositories.ProductRepository;
import com.joaodev.dscommerce.services.exceptions.ResourceNotFoundException;
import com.joaodev.dscommerce.tests.ProductFactory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Product product;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception{
        
        existingId = 1L;
        nonExistingId = 2L;

        product = ProductFactory.createProduct();

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){
       
        ProductDTO result = service.findById(existingId);
        
        Assertions.assertEquals(result.getId(), 1L);
        Assertions.assertEquals(result.getName(), "Playstation");
        Assertions.assertEquals(result.getDescription(), "Ótimo video game");
        Assertions.assertEquals(result.getPrice(), 5000.0);
        Assertions.assertEquals(result.getImgUrl(), "http//img");

        Assertions.assertEquals(result.getCategories().get(0).getId(), 1L);
        Assertions.assertEquals(result.getCategories().get(0).getName(), "Games");
    }    
}
