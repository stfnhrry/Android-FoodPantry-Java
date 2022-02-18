package com.example.foodpantryjava;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Item {

  public Item (String name, String category, Integer number, String size, String expiryDate){
    this.name = name;
    this.category = category;
    this.number = number;
    this.size  = size;
    this.expiryDate = expiryDate;
    icon = getIcon();
    expiryText = getDateDifferenceAsString(expiryDate);
  }

  String name;
  String category;
  Integer number;
  String size;
  String expiryDate;
  int icon;
  String expiryText;

  public int getIcon(){
    int itemIcon = R.drawable.cookies;
    switch (category) {
      case "CANS":
        itemIcon = R.drawable.can_icon;
        break;
      case "JARS":
        itemIcon = R.drawable.jar_icon;
        break;
      case "DRINKS":
        itemIcon = R.drawable.juice_box_icon;
        break;
      case "BARS":
        itemIcon = R.drawable.granola_bar_icon;
        break;
      case "GRAINS":
        itemIcon = R.drawable.wheat_icon;
        break;
      case "COOKIES":
        itemIcon = R.drawable.cookies;
        break;
      case "OTHER":
        itemIcon = R.drawable.picture2;
    }
    return itemIcon;
  }

  /**
   * Calculates the amount of days.
   * @param expiryDate the expiry date
   * @return - the amount of days left as a string
   */
  public String getDateDifferenceAsString(String expiryDate) {
    Date calendar = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());

    try {
      Date date2;
      date2 = dateFormat.parse(expiryDate);
      long difference = 99999;
      if (date2 != null) {
        difference = (date2.getTime() - calendar.getTime());
      }
      long differenceDates = difference / (24 * 60 * 60 * 1000);

      return Long.toString(differenceDates);

    } catch (Exception exception) {
      Log.i("DATE", "Cannot find day difference as string");
      return "null";
    }
  } // getDateDifferenceAsString
}
