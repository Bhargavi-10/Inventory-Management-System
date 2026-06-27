package com.inventorymanage.InventoryManagement.repository;

import com.inventorymanage.InventoryManagement.Model.DailyMovementDTO;
import com.inventorymanage.InventoryManagement.Model.Inventory;
import com.inventorymanage.InventoryManagement.Model.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {


    List<Product> findByQuantityLessThan(int threshold);

    Inventory findByProductId(Integer id);


    //LOW STOCK
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    List<Inventory> findLowStock();

    //OUT OF STOCK PRODUCTS
    List<Inventory> findByQuantity(int quantity);

}
