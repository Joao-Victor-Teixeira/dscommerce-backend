package com.joaodev.dscommerce.dto;

import com.joaodev.dscommerce.entities.OrderItem;

public class OrderItemDTO {

    private Long producId;
    private String name;
    private Double price;
    private Integer quantity;

    public OrderItemDTO(){
    }

    public OrderItemDTO(Long producId, String name, Double price, Integer quantity) {
        this.producId = producId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItemDTO(OrderItem entity) {
        producId = entity.getProduct().getId();
        name = entity.getProduct().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
    }

    public Long getProducId() {
        return producId;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getSubTotal(){
        return price * quantity;
    }
}
