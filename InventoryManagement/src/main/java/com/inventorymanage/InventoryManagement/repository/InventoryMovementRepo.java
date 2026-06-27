package com.inventorymanage.InventoryManagement.repository;

import com.inventorymanage.InventoryManagement.Model.DailyMovementDTO;
import com.inventorymanage.InventoryManagement.Model.Inventory;
import com.inventorymanage.InventoryManagement.Model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepo extends JpaRepository<InventoryMovement  , Integer> {


    List<InventoryMovement> findTop5ByOrderByCreatedAtDesc();


    @Query(value = """
           SELECT DATE(created_at) as date,
            SUM(CASE WHEN type = 'IN' THEN moved_quantity ELSE 0 END) AS totalIn,
            SUM(CASE WHEN type = 'OUT' THEN  moved_quantity ELSE 0 END) AS totalOut,
            Count(CASE WHEN type='IN' THEN 1 END) AS totalInCount,
            Count(CASE WHEN type='OUT' THEN 1 END) AS totalInCount
            FROM inventory_movement
            WHERE created_at>=CURRENT_DATE - INTERVAL '7 days'
            GROUP BY DATE(created_at)
            ORDER BY DATE(created_at)""" , nativeQuery = true)

    List<Object[]> getDailyMovements();


    @Query(value = """
           SELECT DATE(created_at) as date,
            SUM(CASE WHEN type = 'IN' THEN moved_quantity ELSE 0 END) AS totalIn,
            SUM(CASE WHEN type = 'OUT' THEN  moved_quantity ELSE 0 END) AS totalOut,
            Count(CASE WHEN type='IN' THEN 1 END) AS totalInCount,
            Count(CASE WHEN type='OUT' THEN 1 END) AS totalInCount
            FROM inventory_movement
            GROUP BY DATE(created_at)
            ORDER BY DATE(created_at)""" , nativeQuery = true)
    List<Object[]> getFullMovement();

    List<InventoryMovement> findAllByOrderByCreatedAtDesc();

    @Query(value= """
       SELECT 
             TO_CHAR(created_at , 'YYYY-MM') as month,
             SUM(moved_quantity) as totalSold
             FROM inventory_movement
             WHERE type='OUT'
             GROUP BY TO_CHAR(created_at,'YYYY-MM')
             ORDER BY month""" , nativeQuery = true)
    List<Object[]> getMonthlySalesTrend();

    @Query(value= """
        SELECT p.name , SUM(m.moved_quantity) as totalSold
                FROM inventory_movement m
                JOIN product p ON m.product_id = p.id
                WHERE m.type='OUT' 
                GROUP BY p.name
                ORDER BY totalSold DESC""" , nativeQuery = true)
    List<Object[]> getProductSales();
}
