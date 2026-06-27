package com.inventorymanage.InventoryManagement.Model;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class DailyMovementDTO {
   private LocalDate date;
   private int totalIn;
   private int totalOut;

   private int totalInCount;
   private int totalOutCount;

    public DailyMovementDTO(LocalDate date, int totalIn, int totalOut, int totalInCount , int totalOutCount) {
        this.date = date;
        this.totalIn = totalIn;
        this.totalOut = totalOut;
        this.totalInCount = totalInCount;
        this.totalOutCount  = totalOutCount;
    }

    public LocalDate getDate(){
        return date;
    }

    public int getTotalIn(){
        return totalIn;
    }
    public int getTotalOut(){
        return totalOut;
    }
    public int getTotalInCount(){return totalInCount;}
    public int getTotalOutCount(){return totalOutCount;}
}
