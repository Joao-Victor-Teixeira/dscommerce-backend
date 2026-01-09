package com.joaodev.dscommerce.tests;

import com.joaodev.dscommerce.dto.ProductDTO;
import com.joaodev.dscommerce.entities.Category;
import com.joaodev.dscommerce.entities.Product;

public class ProductFactory {

      public static Product createProduct(){
        Product product = new Product(1L, "Playstation", "Ótimo video game", 5000.0, "http//img");
        Category cat = CategoryFactory.createCategory(1L, "Games");
        product.getCategories().add(cat);
        return product;
    }

    public static Product createProduct(Long id, String name, String description, Double price, String imgUrl){
        Product product = new Product(id, name, description, price, imgUrl);
        Category cat = CategoryFactory.createCategory(1L, "Games");
        product.getCategories().add(cat);
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product prod = createProduct();
        return new ProductDTO(prod);
    }
}
