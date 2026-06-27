package com.inventorymanage.InventoryManagement.service;

import com.inventorymanage.InventoryManagement.Model.Inventory;
import com.inventorymanage.InventoryManagement.Model.InventoryMovement;
import com.inventorymanage.InventoryManagement.Model.Product;
import com.inventorymanage.InventoryManagement.Model.ProductInventoryDTO;
import com.inventorymanage.InventoryManagement.Model.enums.MovementType;
import com.inventorymanage.InventoryManagement.repository.InventoryMovementRepo;
import com.inventorymanage.InventoryManagement.repository.InventoryRepo;
import com.inventorymanage.InventoryManagement.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo pdtRepo;

    @Autowired
    private InventoryRepo invtRepo;
    @Autowired
    private InventoryService invtserv;

    @Autowired
    private InventoryMovementRepo movementRepo;

    //RETRIEVE PRODUCTS
    public List<Product> getAllProducts() {
        return pdtRepo.findAll();
    }

    //ADD NEW PRODUCT
    public Product addPdt(Product pdt , int quantityToAdd  ,int reorderLevel) {
        Optional<Product>existingProductOpt = Optional.ofNullable(pdtRepo.findByNameIgnoreCaseAndBrandIgnoreCase(pdt.getName(), pdt.getBrand()));

        if(existingProductOpt.isPresent()){
            Product existingProduct = existingProductOpt.get();

            Inventory invt = invtRepo.findByProductId(existingProduct.getId());
            if(invt==null){
                //CREATE INVENTORY IF MISSING
                invt = new Inventory();
                invt.setProduct(existingProduct);
                invt.setQuantity(quantityToAdd);
                invt.setReorderLevel(reorderLevel);
            }else{
                invt.setQuantity(invt.getQuantity() + quantityToAdd);
                invt.setReorderLevel(reorderLevel);
            }
            existingProduct.setPrice(pdt.getPrice());
            existingProduct.setDescription(pdt.getDescription());
            pdtRepo.save(existingProduct);
            invtRepo.save(invt);

            invtserv.moveStock(existingProduct.getId(), quantityToAdd, MovementType.IN , "Existed product is restocked ");

            return existingProduct;
        }
    //NEW PRODUCT
        Product savedProduct = pdtRepo.save(pdt);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(0);
        inventory.setReorderLevel(reorderLevel);

        invtRepo.save(inventory);


        //save movement
        invtserv.moveStock(savedProduct.getId(), quantityToAdd ,MovementType.IN , "New Product Added"); //movestock is adding quantity to the exisiting inventory . hence we pass invt.setQuantity(0)
        System.out.println("Inventory saved for product: " + savedProduct.getId());


        return savedProduct;
    }


    //SEARCH PRODUCT BY NAME
    public List<Product> searchByName(String name) {
        List<Product> list = pdtRepo.findByNameContainingIgnoreCase(name);
        List<Product> filteredList = new ArrayList<>();
        for (Product p : list) {
            Optional<Inventory>inv = Optional.ofNullable(invtRepo.findByProductId(p.getId()));
            //if (inv.isPresent() && inv.get().getQuantity() != 0) {
                filteredList.add(p);
            //}
        }

        list = filteredList;

//        if (list.isEmpty()) {
//            throw new RuntimeException("product not found matching  " + name);
//        } this is creating 500 internal server error
        return list;
    }

    //UPDATE
    public Product updateProduct(Integer id , double price, int reorderLevel, String description) {
        Optional<Product> Optionalprod = pdtRepo.findById(id);

        Product prod;

        if (Optionalprod.isPresent()) {
            prod = Optionalprod.get();
            prod.setPrice(price);
            prod.setDescription(description);
        }else{
            throw new RuntimeException("Product not found with id "+id);
        }

        Optional<Inventory> inventory = Optional.ofNullable(invtRepo.findByProductId(id));
        if(inventory.isPresent()){
            Inventory invt = inventory.get();
            invt.setReorderLevel(reorderLevel);
            invtRepo.save(invt);
        }else{
            throw new RuntimeException("inventory for that product does not exist");
        }
        return prod;
    }

    //DELETE
    public void deleteProd(Integer id){
        Optional<Product> optpdt = pdtRepo.findById(id);
        if(optpdt.isPresent()){
            Product pdt = optpdt.get();
            Inventory inventory = invtRepo.findByProductId(pdt.getId());
            if(inventory != null){
                invtserv.moveStock(pdt.getId() , inventory.getQuantity() , MovementType.OUT , "Product moved out of inventory completely");
                invtRepo.delete(inventory);
            }

            pdtRepo.deleteById(id);
        }else{
            throw new RuntimeException("Product not found with id "+id);
        }
    }


    //SEARCH BY BRAND
    public List<Product> searchByBrand(String brand){
        List<Product> list = pdtRepo.findByBrandContainingIgnoreCase(brand);
        List<Product> filteredList = new ArrayList<>();
        for (Product p : list) {
            Optional<Inventory>inv = Optional.ofNullable(invtRepo.findByProductId(p.getId()));
            if (inv.isPresent() && inv.get().getQuantity() > 0) {
                filteredList.add(p);
            }
        }

        list = filteredList;

        if (list.isEmpty()) {
            throw new RuntimeException("product not found for the brand  " + brand);
        }
        return list;
    }


    //SEARCH BY PRICE RANGE
    public List<Product> searchByPriceRange(double minPrice , double maxPrice){

        if(minPrice<0 || maxPrice<0){
            throw new RuntimeException("entered price is not valid");
        }
        List<Product> list = pdtRepo.findByPriceBetween( minPrice ,  maxPrice);
        List<Product> filteredList = new ArrayList<>();
        for (Product p : list) {
            Optional<Inventory>inv = Optional.ofNullable(invtRepo.findByProductId(p.getId()));
            if (inv.isPresent() && inv.get().getQuantity() != 0) {
                filteredList.add(p);
            }
        }

        list = filteredList;

        if (list.isEmpty()) {
            throw new RuntimeException("product not found for the price in range  " + minPrice +"-" + maxPrice);
        }
        return list;
    }

   //GET NUMBER OF PRODUCTS IN INVENTORY
    public long getPdtCount() {

        return pdtRepo.count();
    }


    //GET DETAILS OF THE PRODUCT WITH THE GIVEN ID FOR UPDATION
    public ProductInventoryDTO getProductInventory(Integer id){
        Inventory inv = invtRepo.findByProductId(id);
        if(inv==null){
            return null;
        }
        Product  p = inv.getProduct();
        ProductInventoryDTO dto = new ProductInventoryDTO(
                p.getName(),
                p.getId(),
                p.getBrand(),
                p.getPrice(),
                p.getDescription(),
                inv.getQuantity(),
                inv.getReorderLevel()
        );

        return dto;
    }


    //SELL PRODUCT OR REDUCE STOCK
    public void reduceStock(Integer productId , int quantitySold){
        Inventory invt = invtRepo.findByProductId(productId);
        if(invt==null){
           throw new RuntimeException("Inventory not found");
        }
        if(invt.getQuantity() < quantitySold){
            throw new RuntimeException("Not enough stock available");
        }

        invtserv.moveStock(productId,quantitySold,MovementType.OUT,"Product Sold");

    }
}
