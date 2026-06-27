package com.inventorymanage.InventoryManagement.Controller;

import com.inventorymanage.InventoryManagement.Model.*;
import com.inventorymanage.InventoryManagement.Model.enums.MovementType;
import com.inventorymanage.InventoryManagement.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/Inventory")
public class InventoryController {

    @Autowired
    private InventoryService invtServ;


    @GetMapping("/getAllInventory")
    public List<Inventory> getAllProducts(){
        return invtServ.getAllProducts();
    }


    @GetMapping("/searchByThreshold")
    public List<Inventory> searchByThreshold(){
        return invtServ.findByThreshold();
    }


    @PutMapping("/restock")
    public void restock(@RequestParam int id , @RequestParam int addedQuantity){
        invtServ.restock(id,addedQuantity);
    }

    @GetMapping("/lowstockproducts")
    public List<ProductInventoryDTO> getLowStockProducts(){
        return invtServ.getLowStockProducts();
    }

    @PutMapping("/movement")
    public void stockMovement(@RequestParam Integer id , @RequestParam int quantity , @RequestParam MovementType type , @RequestParam String reason){
        invtServ.moveStock(id,quantity,type,reason);
    }

    @GetMapping("/recentMovements")
    public List<InventoryMovement> recentStockMovements(){
        return invtServ.recentMovements();
    }

    @GetMapping("/inventoryQuantity")
    public int inventoryQuantity(){
        return invtServ.getTotalInventoryQuantity();
    }

    @GetMapping("/productInventoryDTO")
    public List<ProductInventoryDTO> getAllProductInvt(){
        return invtServ.getAllProductInventory();
    }


    @GetMapping("/dashboard/movement")
    public List<DailyMovementDTO> getDashboardMovement(){
        return invtServ.getWeekMovement();
    }

    @GetMapping("/reports/movement")
    public List<DailyMovementDTO> getFullMovement(){
        return invtServ.getFullMovementReport();
    }

    @GetMapping("/outOfStock")
    public List<ProductInventoryDTO> getOutOfStockProducts(){
        return invtServ.getOutOfStockProducts();
    }

   @GetMapping("/pdtmovements")
    public List<InventoryMovement> getAllMovements(){
        return invtServ.getAllMovements();
   }

   @GetMapping("/reports/monthly-sales")
    public List<Object[]> getMonthlySalesTrend(){
        return invtServ.getMonthlySalesTrendReport();
   }

   @GetMapping("/reports/product-sales")
    public List<Object[]> getProductSales(){
        return invtServ.getProductSales();
   }

}
