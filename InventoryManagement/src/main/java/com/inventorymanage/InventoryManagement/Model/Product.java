package com.inventorymanage.InventoryManagement.Model;


import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)//tells hibernate that this column should not be null . this must be provided . if it is true it need not be provided
    private String name ;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private String description;


    //GETTERS :
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getBrand(){
        return brand;
    }
    public String getDescription() {
        return description;
    }
//SETTERS :


    public void setBrand(String brand){
        this.brand = brand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Product() {
    }
}
