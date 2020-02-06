package com.github.gpor0.business.model;

import java.util.List;

public class Reservation {

    private String name;

    private List<String> customers;

    private Integer total;
    private Integer available;

    public Reservation(String name, List<String> customers, Integer total, Integer available) {
        this.name = name;
        this.customers = customers;
        this.total = total;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCustomers() {
        return customers;
    }

    public void setCustomers(List<String> customers) {
        this.customers = customers;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}
