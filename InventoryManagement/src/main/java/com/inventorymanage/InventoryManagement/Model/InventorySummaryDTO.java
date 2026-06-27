package com.inventorymanage.InventoryManagement.Model;

public class InventorySummaryDTO {
    private int totalProducts;
    private int totalQuantity;
    private double totalValue;

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public InventorySummaryDTO(int totalProducts, int totalQuantity, double totalValue) {
        this.totalProducts = totalProducts;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }
}
