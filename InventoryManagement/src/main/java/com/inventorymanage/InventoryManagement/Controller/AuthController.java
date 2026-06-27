package com.inventorymanage.InventoryManagement.Controller;

import com.inventorymanage.InventoryManagement.Model.User;
import com.inventorymanage.InventoryManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

   @Autowired
   UserRepository userRepo;
   @PostMapping("/login")
   public User login(@RequestBody User loginUser){
      User user  = userRepo.findByUsername(loginUser.getUsername());

      if(user!=null && user.getPassword().equals(loginUser.getPassword())){
         return user;
      }
      throw new RuntimeException("invalid credentials");
   }
}
