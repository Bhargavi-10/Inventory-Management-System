package com.inventorymanage.InventoryManagement.Model;


import jakarta.persistence.*;

@Entity
public class InventoryProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(nullable = false)//tells hibernate that this column should not be null . this must be provided . if it is true it need not be provided
    private String name ;
    private double price;
    private int quantity;
    private int reorderLevel;


    //GETTERS :
    public Integer getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    //SETTERS :

    public void setId(Integer id) {
        Id = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}
