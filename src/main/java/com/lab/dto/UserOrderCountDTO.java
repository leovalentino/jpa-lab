package com.lab.dto;

public class UserOrderCountDTO {
    private final String userName;
    private final Long orderCount;
    
    public UserOrderCountDTO(String userName, Long orderCount) {
        this.userName = userName;
        this.orderCount = orderCount;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public Long getOrderCount() {
        return orderCount;
    }
}
