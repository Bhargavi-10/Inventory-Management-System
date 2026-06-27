package com.inventorymanage.InventoryManagement.Controller;

import com.inventorymanage.InventoryManagement.service.InventoryProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InventoryProdController {
    @Autowired
    private InventoryProdService pdtServ ;

    @RequestMapping("/greet")
    public String greet(){
        return "welcome to inventory management";
    }
}
