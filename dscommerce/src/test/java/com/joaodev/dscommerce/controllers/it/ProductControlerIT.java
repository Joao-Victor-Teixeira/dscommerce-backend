package com.joaodev.dscommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaodev.dscommerce.dto.ProductDTO;
import com.joaodev.dscommerce.tests.ProductFactory;
import com.joaodev.dscommerce.tests.TokenUtil;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControlerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

   private Long existingId, nonExistingId, dependentId;
     
   private String productName;

    @BeforeEach
    void setUp() throws Exception {
        
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 1L;
        productName = "Macbook Pro"; 
    }


    @Test
    public void updateShouldReturnProductDTOWhenAdminLoggedAndIdExists() throws Exception {
        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");
        ProductDTO dto = ProductFactory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        result.andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    public void updateShouldReturnNotFoundWhenAdminLoggedAndNonExistingId() throws Exception {
        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");
        ProductDTO dto = ProductFactory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidData() throws Exception {
        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");
        ProductDTO dto = ProductFactory.createProductDTO();
        dto.setName(""); 
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnForbiddenWhenClientLogged() throws Exception {
        String clientToken = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");
        ProductDTO dto = ProductFactory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
                .header("Authorization", "Bearer " + clientToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnOrderWhenSelfClientLogged() throws Exception {
    
        String clientToken = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");

        ResultActions result = mockMvc.perform(get("/orders/{id}", 1L)
            .header("Authorization", "Bearer " + clientToken)
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(1L));
}

    @Test
    public void findByIdShouldReturnForbiddenWhenClientLoggedButNotOwner() throws Exception {
        
        String clientToken = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");

        Long orderId = 2L;

        ResultActions result = mockMvc.perform(get("/orders/{id}", orderId)
                .header("Authorization", "Bearer " + clientToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenAdminLoggedAndNonExistingId() throws Exception {

        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");

        ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId)
             .header("Authorization", "Bearer " + adminToken));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenAdminLoggedAndIdExists() throws Exception {

        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");

        ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
             .header("Authorization", "Bearer " + adminToken));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception{

        String clientToken = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");

        ProductDTO dto = ProductFactory.createProductDTO();

        String jsonBody = objectMapper.writeValueAsString(dto);
        
        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + clientToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        
        result.andExpect(status().isForbidden()); 
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndNegativePrice() throws Exception{

        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");

        ProductDTO dto = ProductFactory.createProductDTO();
        dto.setPrice(-1.0);
        
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        
        result.andExpect(status().isUnprocessableEntity());        
    }

    @Test
    public void insertShouldReturnCreatedWhenAdminLogged() throws Exception{

        String adminToken = tokenUtil.obtainAccessToken(mockMvc, "alex@gmail.com", "123456");

        ProductDTO dto = ProductFactory.createProductDTO(); 

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    public void findAllShouldReturnProductsWhenNameExists() throws Exception {
  
        mockMvc.perform(get("/products")
                .param("name", productName) 
                .accept(MediaType.APPLICATION_JSON)) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Macbook Pro")) 
            .andExpect(jsonPath("$.content[0].id").exists());
    }
}
