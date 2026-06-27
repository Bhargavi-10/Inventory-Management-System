package com.inventorymanage.InventoryManagement.service;

import com.inventorymanage.InventoryManagement.Model.InventoryProduct;
import com.inventorymanage.InventoryManagement.repository.InventoryProdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryProdService {

    @Autowired
    private InventoryProdRepo pdtRepo;

    public List<InventoryProduct> getAllProducts() {
        return pdtRepo.findAll();
    }

    public void addPdt(InventoryProduct pdt) {
        pdtRepo.save(pdt);
    }

    public List<InventoryProduct> searchByName(String name) {
        List<InventoryProduct> list = pdtRepo.findByNameContainingIgnoreCase(name);
        List<InventoryProduct> filteredList = new ArrayList<>();
        for (InventoryProduct p : list) {
            if (p.getQuantity() != 0) {
                filteredList.add(p);
            }
        }

        list = filteredList;

        if (list.isEmpty()) {
            throw new RuntimeException("product not found matching  " + name);
        }
        return list;
    }


    public InventoryProduct updateProduct(Integer id, int quantity, double price, int reorderLevel) {
        Optional<InventoryProduct> Optionalprod = pdtRepo.findById(id);

        InventoryProduct prod;

        if (Optionalprod.isPresent()) {
            prod = Optionalprod.get();
            prod.setQuantity(quantity);
            prod.setPrice(price);
            prod.setReorderLevel(reorderLevel);
        }else{
            throw new RuntimeException("Product not found with id "+id);
        }
        return pdtRepo.save(prod);
    }

    public void deleteProd(Integer id){
        Optional<InventoryProduct> optpdt = pdtRepo.findById(id);

        if(optpdt.isPresent()){
            pdtRepo.deleteById(id);
        }else
    }
}
