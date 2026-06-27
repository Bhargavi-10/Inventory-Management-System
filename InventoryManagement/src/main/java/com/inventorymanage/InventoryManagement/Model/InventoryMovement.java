package com.inventorymanage.InventoryManagement.Model;

import com.inventorymanage.InventoryManagement.Model.enums.MovementType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity

public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    private int movedQuantity;

    private String reason ;//SALE , DAMAGE, RETURN , RESTOCK

    private LocalDateTime createdAt;

//GETTER & SETTER:
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }



    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }



    public int getMovedQuantity() {
        return movedQuantity;
    }

    public void setMovedQuantity(int movedQuantity) {
        this.movedQuantity = movedQuantity;
    }



    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
