package com.vanguardghana.atomzpay.models;

public class Vehicle {
    public Vehicle(int id, String name, String type_code, String amount) {
        this.id = id;
        this.type_code = type_code;
        this.name = name;
        this.amount = amount;
    }

    private int id;
    private String type_code, name, amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
