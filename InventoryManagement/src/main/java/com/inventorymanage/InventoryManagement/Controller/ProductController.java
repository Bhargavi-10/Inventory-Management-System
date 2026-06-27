package com.inventorymanage.InventoryManagement.Controller;

import com.inventorymanage.InventoryManagement.Model.Inventory;
import com.inventorymanage.InventoryManagement.Model.Product;
import com.inventorymanage.InventoryManagement.Model.ProductInventoryDTO;
import com.inventorymanage.InventoryManagement.service.InventoryService;
import com.inventorymanage.InventoryManagement.service.ProductService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(
        origins = "http://127.0.0.1:5500",
        allowedHeaders = "*",
        methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
@RestController
@RequestMapping("/Product")
public class ProductController {
    @Autowired
    private ProductService pdtServ ;
    @Autowired
    private InventoryService invtServ;

    @RequestMapping("/greet")
    public String greet(){
        return "welcome to inventory management";
    }

    @GetMapping("/getProducts")
    public List<Product> getProducts(){
        return   pdtServ.getAllProducts();
    }

    @PostMapping("/addPdt")
    public ResponseEntity<Product> add(@RequestBody Product pdt , @RequestParam int quantitytoAdd , @RequestParam int reorderLevel){
        try{
            Product savedPdt = pdtServ.addPdt(pdt , quantitytoAdd , reorderLevel);
            return  ResponseEntity.status(HttpStatus.CREATED).body(savedPdt);
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/searchbyName")
    public List<Product> searchByName(@RequestParam String name){
        return pdtServ.searchByName(name);
    }

    @PutMapping("/update/{id}")
    public Product updatePdt(@PathVariable Integer id, @RequestParam double price, @RequestParam int reorderLevel ,@RequestParam String description){
        return pdtServ.updateProduct(id, price ,reorderLevel ,  description);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable Integer id){
        pdtServ.deleteProd(id);
    }

    @GetMapping("/searchbyBrand")
    public List<Product> searchByBrand(@RequestParam String brand ){
        return pdtServ.searchByBrand(brand);
    }

    @GetMapping("/searchByPrice")
    public List<Product> searchByPriceRange(@RequestParam double minPrice , @RequestParam double maxPrice){
        return pdtServ.searchByPriceRange(minPrice , maxPrice);
    }
    @GetMapping("/productCount")
    public long NumberofProducts(){
        return pdtServ.getPdtCount();
    }

    @GetMapping("/getProductInventory/{id}")
    public ResponseEntity<ProductInventoryDTO> getProductInventory(@PathVariable Integer id){
       ProductInventoryDTO dto= pdtServ.getProductInventory(id);
       if(dto==null){
           return ResponseEntity.notFound().build(); //returns 404
       }
       return ResponseEntity.ok(dto);
    }

    @PostMapping("/sell")
    public ResponseEntity<?>sellProduct(@RequestParam Integer id , @RequestParam int quantitySold){
        try{
          pdtServ.reduceStock(id,quantitySold);
          return ResponseEntity.ok("Product sold successfully");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
