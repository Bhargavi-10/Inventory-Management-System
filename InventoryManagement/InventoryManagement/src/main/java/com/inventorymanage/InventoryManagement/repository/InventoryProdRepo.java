package com.inventorymanage.InventoryManagement.repository;

import com.inventorymanage.InventoryManagement.Model.InventoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryProdRepo extends JpaRepository<InventoryProduct , Integer> {

    List<InventoryProduct> findByNameIgnoreCase(String name);

    List<InventoryProduct> findByNameContainingIgnoreCase(String name);
}
