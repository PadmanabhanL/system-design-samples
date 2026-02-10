package com.practice.microservices.SampleEventDrivenApplication.dto;

public class OrderRequest {
    
    private String orderId;

    public OrderRequest() {
    }

    public OrderRequest(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}