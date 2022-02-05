package com.example.foodpantryjava;

public class Item {

  public Item (String name, String category, Integer number, String size, String expiryDate){
    this.name = name;
    this.category = category;
    this.number = number;
    this.size  = size;
    this.expiryDate = expiryDate;
  }

  String name;
  String category;
  Integer number;
  String size;
  String expiryDate;
}
