package com.inventorymanage.InventoryManagement.repository;

import com.inventorymanage.InventoryManagement.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    List<Product> findByNameIgnoreCase(String name);

    List<Product> findByNameContainingIgnoreCase(String name);

    Boolean existsByNameIgnoreCaseAndBrandIgnoreCase(String name , String brand);

    List<Product> findByBrandContainingIgnoreCase(String brand);

    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    Product findByNameIgnoreCaseAndBrandIgnoreCase(String name, String brand);


    /*here we are giving only the declaration of the method , we are not defining or implementing it
    Here spring  auto implements this methods .
    spring data jpa uses parsing method during runtime and generates the corresponding query to perform
    BUT HOW SPRING KNOWS WHAT TO DO ??
         it specifies some keywords(findBy , exitsBy , deleteBy ..) that has to be used in the method ,
         so that while parsing , it gets to know and generates the    query and executes it.
     */
}
