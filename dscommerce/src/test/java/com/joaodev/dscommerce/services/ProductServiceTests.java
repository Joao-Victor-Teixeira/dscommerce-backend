package com.joaodev.dscommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.joaodev.dscommerce.dto.ProductDTO;
import com.joaodev.dscommerce.dto.ProductMinDTO;
import com.joaodev.dscommerce.entities.Product;
import com.joaodev.dscommerce.repositories.ProductRepository;
import com.joaodev.dscommerce.services.exceptions.DatabaseException;
import com.joaodev.dscommerce.services.exceptions.ResourceNotFoundException;
import com.joaodev.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Page<Product> page;

    private Product product;

    private ProductDTO productDTO;

    private Long existingId, nonExistingId, dependentId;


    @BeforeEach
    void setUp() throws Exception{
        
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        product = ProductFactory.createProduct();

        productDTO = ProductFactory.createProductDTO();

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        List<Product> list = List.of(product);
        page = new PageImpl<>(list);
        Mockito.when(repository.searchByName(ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
}

    @Test
    public void deleteShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(()-> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
            service.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void updateShouldReturnProductDTO(){

        ProductDTO dto = ProductFactory.createProductDTO();
        
        ProductDTO result = service.update(existingId, dto);
        
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(result.getId(), productDTO.getId());
        Assertions.assertEquals(result.getName(), productDTO.getName());
        Assertions.assertEquals(result.getDescription(), productDTO.getDescription());
        Assertions.assertEquals(result.getPrice(), productDTO.getPrice());
        Assertions.assertEquals(result.getImgUrl(), productDTO.getImgUrl());

        Assertions.assertFalse(result.getCategories().isEmpty());
        Assertions.assertEquals(result.getCategories().get(0).getId(), productDTO.getCategories().get(0).getId());
    }

    @Test
    public void insertShouldReturnProductDTO(){
        
        ProductDTO dto =  ProductFactory.createProductDTO();

        ProductDTO result = service.insert(dto);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(result.getId(), productDTO.getId());
        Assertions.assertEquals(result.getName(), productDTO.getName());
        Assertions.assertFalse(result.getCategories().isEmpty());
        Assertions.assertEquals(result.getCategories().get(0).getName(), productDTO.getCategories().get(0).getName());
    }

    @Test
    public void findAllShouldReturnPageProductMinDTO(){
        Pageable pageable = PageRequest.of(0, 10);
        String name = "Playstation";
        
        Page<ProductMinDTO> result = service.findAll(name, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getContent().get(0).getId(), page.getContent().get(0).getId());
        Assertions.assertEquals(result.getContent().get(0).getName(), page.getContent().get(0).getName());
        Assertions.assertEquals(result.getContent().get(0).getPrice(), page.getContent().get(0).getPrice());
        Assertions.assertEquals(result.getContent().get(0).getImgUrl(), page.getContent().get(0).getImgUrl());

        Assertions.assertFalse(page.getContent().get(0).getCategories().isEmpty());
          
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
        
        Assertions.assertEquals(result.getId(), product.getId());
        Assertions.assertEquals(result.getName(), product.getName());
        Assertions.assertEquals(result.getDescription(), product.getDescription());
        Assertions.assertEquals(result.getPrice(), product.getPrice());
        Assertions.assertEquals(result.getImgUrl(), product.getImgUrl());

        Assertions.assertEquals(result.getCategories().get(0).getId(), 1L);
        Assertions.assertEquals(result.getCategories().get(0).getName(), "Games");
    } 
    
    
}
