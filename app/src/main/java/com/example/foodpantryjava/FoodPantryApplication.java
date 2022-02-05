package com.example.foodpantryjava;

import android.app.Application;
import android.os.Bundle;

import com.google.android.material.color.DynamicColors;

public class FoodPantryApplication extends Application {
  @Override
  public void onCreate(){
    super.onCreate();
    DynamicColors.applyToActivitiesIfAvailable(this);
  }
}
