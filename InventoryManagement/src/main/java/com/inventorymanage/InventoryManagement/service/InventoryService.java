package com.inventorymanage.InventoryManagement.service;


import com.inventorymanage.InventoryManagement.Model.*;
import com.inventorymanage.InventoryManagement.Model.enums.MovementType;
import com.inventorymanage.InventoryManagement.repository.InventoryMovementRepo;
import com.inventorymanage.InventoryManagement.repository.InventoryRepo;
import com.inventorymanage.InventoryManagement.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepo invtRepo;
    @Autowired
    private ProductRepo pdtRepo;
    @Autowired
    private InventoryMovementRepo invtMoveRepo;


    //GET ALL PRODUCTS
    public List<Inventory> getAllProducts() {
        return invtRepo.findAll();
    }


    //GET PRODUCTS THAT ARE BELOW A THRESHOLD
    public List<Inventory> findByThreshold() {
        List<Inventory> allInvt = invtRepo.findAll();
        List<Inventory> belowThreshold = new ArrayList<>();

        for (Inventory inv : allInvt) {
            if (inv.getQuantity() <= inv.getReorderLevel()) {
                belowThreshold.add(inv);
            }
        }
        return belowThreshold;
    }


    //RESTOCK
    public void restock(Integer id, int addedStock) {
        if (id < 0) {
            throw new RuntimeException("invalid id");
        }

        if (addedStock <= 0) {
            throw new RuntimeException("Added stock must be greater than zero");
        }
        Optional<Product> pdt = pdtRepo.findById(id);

        if (pdt.isEmpty()) {
            throw new RuntimeException("product with id " + id + " does not exist");
        }
        Product prod = pdt.get();/*assigning optional product to original inventory product .
        we cannot work with optional product as it does not access the properties that we have designed for inventoryproduct*/


        Optional<Inventory> inventory = Optional.ofNullable(invtRepo.findByProductId(prod.getId()));

        if (inventory.isPresent()) {
            moveStock(id,addedStock,MovementType.IN,"restock");
        } else {
            throw new RuntimeException("Inventory not found");
        }
    }


    //LOW STOCK PRODUCT

    public List<ProductInventoryDTO> getLowStockProducts() {
        List<Inventory> allInventory = invtRepo.findAll();
        List<Inventory> lowstockList = new ArrayList<>();
        for (Inventory inv : allInventory) {
            if (inv.getQuantity() <= inv.getReorderLevel()) {
                lowstockList.add(inv);
            }
        }
        List<ProductInventoryDTO> dtoList = new ArrayList<>();

        for (Inventory inv : lowstockList) {
            Product p = inv.getProduct();
            dtoList.add(new ProductInventoryDTO(p.getName(), p.getId(), p.getBrand(), p.getPrice(),p.getDescription() ,inv.getQuantity(), inv.getReorderLevel()));
        }
        return dtoList;
    }

    @Transactional
    public void moveStock(Integer id, int quantity, MovementType type, String reason) {
        Inventory invt = invtRepo.findByProductId(id);

        if (invt == null) {
            throw new RuntimeException("Inventory not found");
        } else {
            if (type == MovementType.OUT) {
                if (invt.getQuantity() < quantity) {
                    throw new RuntimeException("not enough stock");
                } else {
                    invt.setQuantity(invt.getQuantity() - quantity);
                }
            } else {
                invt.setQuantity(invt.getQuantity() + quantity);
            }
            invtRepo.save(invt);
        }

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(invt.getProduct());
        movement.setType(type);
        movement.setMovedQuantity(quantity);
        movement.setReason(reason);
        movement.setCreatedAt(LocalDateTime.now());
        invtMoveRepo.save(movement);

        checkLowStock(invt);
    }


    //CHECK LOW STOCK

    private void checkLowStock(Inventory inventory) {
        if (inventory.getQuantity() <= inventory.getReorderLevel()) {
            sendLowStockAlert(inventory);
        }
    }

    //SEND LOW STOCK ALERT
    private void sendLowStockAlert(Inventory inventory) {
        System.out.println("LOW STOCK ALERT ");
    }


    //GET RECENT MOVEMENTS
    public List<InventoryMovement> recentMovements() {
        return invtMoveRepo.findTop5ByOrderByCreatedAtDesc();
    }


    //GET TOTAL INVENTORY QUANTITY
    public int getTotalInventoryQuantity() {
        List<Inventory> allInventory = invtRepo.findAll();

        int sum = 0;
        for (Inventory invt : allInventory) {
            sum = sum + invt.getQuantity();
        }
        return sum;
    }


    //GET PRODUCTINVENTORYDTO
    public List<ProductInventoryDTO> getAllProductInventory() {
        List<Inventory> inventories = invtRepo.findAll();
        List<ProductInventoryDTO> dtoList = new ArrayList<>();

        for (Inventory inv : inventories) {
            Product p = inv.getProduct();
            dtoList.add(new ProductInventoryDTO(p.getName(), p.getId(), p.getBrand(), p.getPrice(),p.getDescription(), inv.getQuantity(), inv.getReorderLevel()));
        }
        return dtoList;
    }


//    //CREATING A DAILY MOVEMENT GRAPH
//    public List<DailyMovementDTO> getDailyMovementStatus() {
//        List<Object[]> rows = invtMoveRepo.getDailyMovements();
//        List<DailyMovementDTO> result = new ArrayList<>();
//
//        for (Object[] row : rows) {
//            LocalDate date;
//
//            date = (LocalDate) row[0];
//
//            int totalIn = ((Number) row[1]).intValue();
//            int totalOut = ((Number) row[2]).intValue();
//
//            result.add(new DailyMovementDTO(date, totalIn, totalOut));
//        }
//        return result;
//    }



    //OUT OF STOCK REPORT
    public List<ProductInventoryDTO> getOutOfStockProducts() {
        List<Inventory> outStockList = invtRepo.findByQuantity(0);
        List<ProductInventoryDTO> dtoList = new ArrayList<>();

        for (Inventory inv : outStockList) {
            Product p = inv.getProduct();

            dtoList.add(
                    new ProductInventoryDTO(p.getName(), p.getId(), p.getBrand(), p.getPrice(), p.getDescription(),inv.getQuantity(), inv.getReorderLevel())
            );
        }
        return dtoList;
    }

    //DAILY MOVEMENT(LAST 7 DAYS FOR DASHBOARD)
    public List<DailyMovementDTO> getWeekMovement() {
        List<Object[]> rows = invtMoveRepo.getDailyMovements();
        List<DailyMovementDTO> result = new ArrayList<>();
        for(Object[] row : rows){
            LocalDate date = (LocalDate) row[0];
            int totalIn = ((Number)row[1]).intValue();
            int totalOut = ((Number)row[2]).intValue();
            int totalInCount = ((Number)row[3]).intValue();
            int totalOutCount = ((Number)row[4]).intValue();
            result.add(new DailyMovementDTO(date,totalIn,totalOut,totalInCount,totalOutCount));

        }
        return result;
    }

    //FULL MOVEMENT
    public List<DailyMovementDTO> getFullMovementReport() {
        List<Object[]> rows = invtMoveRepo.getFullMovement();
        List<DailyMovementDTO> result = new ArrayList<>();
        for(Object[] row : rows){
            LocalDate date = (LocalDate) row[0];
            int totalIn = ((Number)row[1]).intValue();
            int totalOut = ((Number)row[2]).intValue();
            int totalInCount = ((Number)row[3]).intValue();
            int totalOutCount = ((Number)row[4]).intValue();

            result.add(new DailyMovementDTO(date,totalIn,totalOut,totalInCount,totalOutCount));

        }
        return result;
    }

    //INVENTORY MOVEMENT IN PRODUCT PAGE
    public List<InventoryMovement> getAllMovements(){
        return invtMoveRepo.findAllByOrderByCreatedAtDesc();
    }

    //GET MONTHLY SALES
    public List<Object[]> getMonthlySalesTrendReport(){
        return invtMoveRepo.getMonthlySalesTrend();
    }

    //GET FAST AND SLOW MOVING PRODUCTS
    public List<Object[]> getProductSales(){
        return invtMoveRepo.getProductSales();
    }


}

