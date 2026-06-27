package com.inventorymanage.InventoryManagement.Model;

public class ProductInventoryDTO {
    private Integer id;
    private String name;
    private String brand;
    private int quantity;
    private double price;
    private int reorderLevel;
    private String description;


    //non parameterized constructor
    public ProductInventoryDTO(){

    }

    public ProductInventoryDTO(String name, Integer id, String brand, double price, String description, int quantity, int reorderLevel ) {
        this.name =name;
        this.brand = brand;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.description = description;
    }

    //GETTERS AND SETTERS


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }



    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}
