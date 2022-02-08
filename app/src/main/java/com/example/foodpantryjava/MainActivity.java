package com.example.foodpantryjava;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nambimobile.widgets.efab.ExpandableFab;
import com.nambimobile.widgets.efab.FabOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity  {

  @Nullable
  private final Handler handler = new Handler();
  private Boolean backPressedOnce = false;
  final Runnable runnable = this::setBackPressedToFalse;
  public static PantryFragment itemViews;
  Toast lastToast;

  Map<Integer, String[]> map = SaveFile.pantry;
  ArrayList<Item> data = SaveFile.data;

  BottomNavigationView navigationBar;
  NavigationRailView navRail;
  NavigationView modalNavDrawer;
  NavigationView navDrawer;

  NavController navController;

  Dialog addNewItemDialog;
  Dialog editItemDialog;
  boolean isEveryFieldChecked = false;
  Button confirmDialogActionButton;
  Button closeDialogButton;
  EditText nameEditField;
  EditText amountEditField;
  EditText sizeEditField;
  EditText expiryDateEditField;

  Integer dataNum;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.i("SAVE", "onCreate: ");
    itemViews = new PantryFragment();
    setupViews();
  }

  public void setupViews(){
    navigationBar = findViewById(R.id.bottom_navigation);
    navRail = findViewById(R.id.nav_rail);
    modalNavDrawer = findViewById(R.id.modal_nav_drawer);
    navDrawer = findViewById(R.id.nav_drawer);
    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.pantryPage, R.id.searchPage, R.id.shoppingListPage, R.id.settingsPage
    ).build();

    // Initialising the Navigation Controller
    navController = Navigation.findNavController(this, R.id.fragNavHost);

    // Setting up navigation click listeners
    setNavigationBottomBarItemOnClicks(navigationBar);
    setNavigationRailItemOnClicks(navRail);
    setModalNavigationDrawerItemOnClicks(modalNavDrawer);
    setNavigationDrawerItemOnClicks(navDrawer);

    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    View bottomNav = findViewById(R.id.coordinatorLayout);
    ExpandableFab bottomNavFab = findViewById(R.id.fab);
    FabOption fabOptionOne = findViewById(R.id.faboption_1);
    FabOption fabOptionThree = findViewById(R.id.faboption_3);
    ExtendedFloatingActionButton navRailFab = findViewById(R.id.nav_fab);

    Configuration configuration = getResources().getConfiguration();
    FragmentManager fragmentManager = getSupportFragmentManager();

    fabOptionOne.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showAddItemDialog();
      }
    });
    navRailFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showAddItemDialog();
      }
    });
    fabOptionThree.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        data.clear();
        PantryFragment.adapter.notifyDataSetChanged();
        map.clear();
        saveHashmapToPreferences();
      }
    });

    // Update navigation views according to screen width size.
    int screenWidth = configuration.screenWidthDp;
    AdaptiveUtils.updateNavigationViewLayout(
            screenWidth, drawerLayout, modalNavDrawer, bottomNavFab, bottomNav, navRail, navDrawer, navRailFab);

    // Clear backstack to prevent unexpected behaviors when pressing back button.
    int backStackEntryCount = fragmentManager.getBackStackEntryCount();
    for (int entry = 0; entry < backStackEntryCount; entry++) {
      fragmentManager.popBackStack();
    }
  }

  @Override
  public void onBackPressed(){
    NavController navController = Navigation.findNavController(this, R.id.fragNavHost);

    // Check if current destination is the start point
    if (navController.getGraph().getStartDestination() == Objects.requireNonNull(navController.getCurrentDestination()).getId()){
      //Was the back button pressed once already? If so then exit app
      if (backPressedOnce){
        super.onBackPressed();
        return;
      }
      backPressedOnce = true;
      Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

      if (handler != null) {
        handler.postDelayed(runnable, 2000);
      }
    }
    else {
      super.onBackPressed();
    }
  }

  public void setBackPressedToFalse(){
    backPressedOnce = false;
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.i("SAVE", "ON START Called");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.i("SAVE", "ON STOP Called");
  }

  @Override
  protected void onResume() {
    super.onResume();
    refreshAllItems();
    Log.i("SAVE", "ON RESUME Called");
  }

  public void showToast(String text) {
    if (lastToast != null) {
      lastToast.cancel();
    }
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.show();
    lastToast = toast;
  } // showToast

  /**
   * Shows the add item dialog.
   */
  public void showAddItemDialog() {
    Log.i("SAVE", "Show add item dialog");
    addNewItemDialog = new Dialog(this);
    addNewItemDialog.setContentView(R.layout.add_item_dialog);

    confirmDialogActionButton = addNewItemDialog.findViewById(R.id.confirmButton);
    closeDialogButton = addNewItemDialog.findViewById(R.id.cancelButton);
    nameEditField = addNewItemDialog.findViewById(R.id.editName);
    amountEditField = addNewItemDialog.findViewById(R.id.editAmount);
    amountEditField.setText("2");
    sizeEditField = addNewItemDialog.findViewById(R.id.editSize);
    sizeEditField.setText("10kg");
    expiryDateEditField = addNewItemDialog.findViewById(R.id.editDate);
    expiryDateEditField.setText("21/02/2022");

    Spinner categorySpinner = addNewItemDialog.findViewById(R.id.spinner);
    ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categorySpinner.setAdapter(categoryAdapter);

    confirmDialogActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isEveryFieldChecked = checkAllFields();

        if (isEveryFieldChecked) {
          String nameString = nameEditField.getText().toString();
          String categoryString = categorySpinner.getSelectedItem().toString();
          int amountInteger = Integer.parseInt(amountEditField.getText().toString());
          String weightString = sizeEditField.getText().toString();
          String expDateString = expiryDateEditField.getText().toString();
          addNewItem(nameString, categoryString, amountInteger, weightString, expDateString);
        }
      }
    });

    closeDialogButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        hideKeyboard(nameEditField);
        addNewItemDialog.dismiss();
      }
    });

    addNewItemDialog.show();
  } // showAddItemDialog

  public void addNewItem(String name, String category, int amount, String weight, String expiryDate){
    dataNum = data.size();
    data.add(dataNum, new Item(name, category, amount, weight, expiryDate));
    itemViews.adapter.notifyItemInserted(dataNum);
    saveToArray(R.drawable.forkandspoon, name, category, amount, weight, expiryDate, dataNum);
  }

  /**
   * Shows the edit item dialog.
   * @param index current card index
   */
  public void showEditItemDialog(int index) {
    editItemDialog = new Dialog(this);
    editItemDialog.setContentView(R.layout.edit_item_dialog);

    confirmDialogActionButton = editItemDialog.findViewById(R.id.confirmButton);
    closeDialogButton = editItemDialog.findViewById(R.id.cancelButton);

    nameEditField = editItemDialog.findViewById(R.id.editName);
    Spinner categorySelector = editItemDialog.findViewById(R.id.spinner);
    ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categorySelector.setAdapter(categoryAdapter);
    amountEditField = editItemDialog.findViewById(R.id.editAmount);
    sizeEditField = editItemDialog.findViewById(R.id.editSize);
    expiryDateEditField = editItemDialog.findViewById(R.id.editDate);

    // Set the text in the fields to match the data on the items
    nameEditField.setText(data.get(index).name);
    for (int i = 0; i < (categorySelector.getCount()); i++) {
      if (categorySelector.getItemAtPosition(i).toString().equalsIgnoreCase(data.get(index).category)) {
        categorySelector.setSelection(i);
      }
    }
    amountEditField.setText(data.get(index).number.toString());
    sizeEditField.setText(data.get(index).size);
    expiryDateEditField.setText(data.get(index).expiryDate);

    confirmDialogActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isEveryFieldChecked = checkAllFields();
        if (isEveryFieldChecked) {
          editItem(index, nameEditField.getText().toString(), categorySelector, Integer.parseInt(amountEditField.getText().toString()), sizeEditField.getText().toString(), expiryDateEditField.getText().toString());
          hideKeyboard(nameEditField);
          editItemDialog.dismiss();
        }
      }
    });

    closeDialogButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        hideKeyboard(nameEditField);
        editItemDialog.dismiss();
      }
    });

    editItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override
      public void onDismiss(DialogInterface dialogInterface) {
        hideKeyboard(nameEditField);
      }
    });
    editItemDialog.show();
  } // showEditItemDialog

  /**
   * Edits an item already in the pantry.
   *
   * @param index    - the index of the item
   * @param name     - name of the item
   * @param category - category of the item (can, jar, cookies,...)
   * @param amount   - the amount in stock of the item
   * @param size     - size of the item
   * @param expDate  - expiry date of the item
   */
  public void editItem(int index, String name, Spinner category, Integer amount, String size, String expDate) {
    data.set(index, new Item(name, category.getSelectedItem().toString(), amount, size, expDate));
    itemViews.adapter.notifyItemChanged(index);

    String iconString = Integer.toString(data.get(index).icon);
    String amountString = amount.toString();
    String[] temp = new String[6];
    temp[0] = iconString;
    temp[1] = name;
    temp[2] = category.getSelectedItem().toString();
    temp[3] = amountString;
    temp[4] = size;
    temp[5] = expDate;

    map.replace(index, temp);
    saveHashmapToPreferences();
  } // editItem

  /**
   * Removes an item from the pantry.
   * @param index the index of the current card to be removed
   */
  public void removeItemFromPantry(int index) {
    data.remove(index);
    itemViews.adapter.notifyItemRemoved(index);
    map.remove(index);

    int id = 0;
    HashMap<Integer, String[]> tempMap = new HashMap<Integer, String[]>();
    Set<Map.Entry<Integer, String[]>> entries = map.entrySet();
    Iterator<Map.Entry<Integer, String[]>> iterator =
            entries.iterator();

    while (iterator.hasNext()) {
      Map.Entry<Integer, String[]> entry = iterator.next();
      Integer key = entry.getKey();
      String[] value = entry.getValue();

      tempMap.put(id, value);
      id++;
    }
    map = tempMap;
    saveHashmapToPreferences();
//    if (inRemovingMode == true) {
//      setRemoveModeActive();
//    } else {
//      setRemoveModeInactive();
//    }
  } // removeItemFromPantry

  public void loadNewItem(int icon, String name, String category, int amount, String weight, String expiryDate) {
    dataNum = data.size();
    data.add(dataNum, new Item(name, category, amount, weight, expiryDate));
//    itemViews.adapter.notifyItemInserted(dataNum);
//    saveToArray(R.drawable.forkandspoon, name, category, amount, weight, expiryDate, dataNum);
  }

  public void saveToArray(Integer icon, String name, String category, Integer amount, String weight, String expDate, int index) {
    Log.i("SAVE", "saveToArray");
    String iconString = icon.toString();
    String amountString = amount.toString();
    String[] temp = new String[6];
    temp[0] = iconString;
    temp[1] = name;
    temp[2] = category;
    temp[3] = amountString;
    temp[4] = weight;
    temp[5] = expDate;
    saveToHashMap(index, temp);
  } // saveToArray

  /**
   * Loads a new item from the array.
   */
  public void loadFromArray() {
    for (int i = 0; i < map.size(); i++) {
      loadNewItem(Integer.parseInt(map.get(i)[0]), map.get(i)[1], map.get(i)[2], Integer.parseInt(map.get(i)[3]), map.get(i)[4], map.get(i)[5]);
    }
  } // loadFromArray

  /**
   * Saves an item to the hashmap.
   * @param index the index of the current card
   * @param ItemInfo the information in the current card
   */
  public void saveToHashMap(int index, String[] ItemInfo) {
    map.put(index, ItemInfo);
    saveHashmapToPreferences();
  } // saveToHashMap

  /**
   * Saves the hashmap to shared preferences.
   */
  public void saveHashmapToPreferences() {
    //convert to string using gson
    Gson gson = new Gson();
    String hashMapString = gson.toJson(map);

    //save in shared prefs
    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.clear();
    editor.putString("hashString", hashMapString).apply();
  } // saveHashmapToPreferences

  /**
   * Loads information from the hashmap.
   */
  public void loadFromHashmap() {
    Log.i("SAVE", "Load from hashmap");
    //get shared prefs
    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
    Gson gson = new Gson();
    //get HashMap as string from preferences
    String storedHashMapString = preferences.getString("hashString", "Empty");
    if (storedHashMapString.equals("Empty")) {
      Log.i("SAVE", "Hashmap is empty: " + preferences.getString("hashString", "Empty"));
      return;
    } else {
      Log.i("SAVE", "Hashmap has stuff in it: " + preferences.getString("hashString", "Empty"));
      java.lang.reflect.Type type = new TypeToken<HashMap<Integer, String[]>>() {
      }.getType();
      HashMap<Integer, String[]> testHashMap2 = gson.fromJson(storedHashMapString, type);
      map = testHashMap2;
      loadFromArray();
    }
  } // loadFromHashmap

  public void refreshAllItems() {
    data.clear();
    PantryFragment.adapter.notifyDataSetChanged();
    loadFromHashmap();
  } // refreshAllItems

  public void setNavigationRailItemOnClicks(NavigationRailView navRail){
    navRail.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.pantryPage:
            navController.navigate(R.id.pantryPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.searchPage:
            navController.navigate(R.id.searchPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.shoppingListPage:
            navController.navigate(R.id.shoppingListPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.settingsPage:
            navController.navigate(R.id.settingsPage);
            setSelectedMenuItem(item);
            return true;
        }
        return false;
      }
    });
  }

  public void setNavigationDrawerItemOnClicks(NavigationView navView){
    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.pantryPage:
            navController.navigate(R.id.pantryPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.searchPage:
            navController.navigate(R.id.searchPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.shoppingListPage:
            navController.navigate(R.id.shoppingListPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.settingsPage:
            navController.navigate(R.id.settingsPage);
            setSelectedMenuItem(item);
            return true;
        }
        return false;
      }
    });
  }

  public void setModalNavigationDrawerItemOnClicks(NavigationView navView){
    navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.pantryPage:
            navController.navigate(R.id.pantryPage);
            setSelectedMenuItem(item);
            break;
          case R.id.searchPage:
            navController.navigate(R.id.searchPage);
            setSelectedMenuItem(item);
            break;
          case R.id.shoppingListPage:
            navController.navigate(R.id.shoppingListPage);
            setSelectedMenuItem(item);
            break;
          case R.id.settingsPage:
            navController.navigate(R.id.settingsPage);
            setSelectedMenuItem(item);
            break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(navView);
        return true;
      }
    });
  }

  public void setNavigationBottomBarItemOnClicks(NavigationBarView navBar){
    navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.pantryPage:
            navController.navigate(R.id.pantryPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.searchPage:
            navController.navigate(R.id.searchPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.shoppingListPage:
            navController.navigate(R.id.shoppingListPage);
            setSelectedMenuItem(item);
            return true;
          case R.id.settingsPage:
            navController.navigate(R.id.settingsPage);
            setSelectedMenuItem(item);
            return true;
        }
        return false;
      }
    });
  }

  public void setSelectedMenuItem(MenuItem item){
    navigationBar.getMenu().findItem(item.getItemId()).setChecked(true);
    navRail.getMenu().findItem(item.getItemId()).setChecked(true);
    modalNavDrawer.setCheckedItem(item);
    navDrawer.setCheckedItem(item);
  }

  /**
   * Checks all fields.
   * @return - true if all fields are not 0, false otherwise
   */
  public boolean checkAllFields() {
    if (nameEditField.length() == 0) {
      nameEditField.setError("This field is required");
      return false;
    }
    if (amountEditField.length() == 0) {
      amountEditField.setError("This field is required");
      return false;
    }
    if (sizeEditField.length() == 0) {
      sizeEditField.setError("This field is required");
      return false;
    }
    if (expiryDateEditField.length() == 0) {
      expiryDateEditField.setError("This field is required");
      return false;
    }
    return true;
  } // checkAllFields

  /**
   * Sets the correct icon depending on the category that the user has chosen.
   * @param category the category in question
   * @return the corresponding icon
   */
  public int setIconFromCategory(Spinner category) {
    int index = category.getSelectedItemPosition();
    int itemIcon = R.drawable.cookies;
    switch (index) {
      case 0:
        itemIcon = R.drawable.can_icon;
        break;
      case 1:
        itemIcon = R.drawable.jar_icon;
        break;
      case 2:
        itemIcon = R.drawable.juice_box_icon;
        break;
      case 3:
        itemIcon = R.drawable.granola_bar_icon;
        break;
      case 4:
        itemIcon = R.drawable.wheat_icon;
        break;
      case 5:
        itemIcon = R.drawable.cookies;
        break;
      case 6:
        itemIcon = R.drawable.picture2;
    }
    return itemIcon;
  } // setIconFromCategory

  /**
   * Hides the keyboard from the user when opening an edit text.
   * @param input the current input
   */
  public void hideKeyboard(EditText input) {
    Log.i("SAVE", "Hide keyboard run");
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
  } // hideKeyboard

  /**
   * Calculates the amount of days.
   * @param expiryDate the expiry date
   * @return - the amount of days left as a string
   */
  public String getDateDifferenceAsString(String expiryDate) {
    Date calendar = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    try {
      Date date2;
      date2 = dateFormat.parse(expiryDate);
      long difference = (date2.getTime() - calendar.getTime());
      long differenceDates = difference / (24 * 60 * 60 * 1000);

      return Long.toString(differenceDates);

    } catch (Exception exception) {
      Log.i("DATE", "Cannot find day difference as string");
      return "null";
    }
  } // getDateDifferenceAsString

  /**
   * Calculates the date difference as a long.
   * @param expiryDate the expiry date
   * @return - amount of days left as a long value
   */
  public long getDateDifferenceAsLong(String expiryDate) {
    Date calendar = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    try {
      Date date2;
      date2 = dateFormat.parse(expiryDate);
      long difference = (date2.getTime() - calendar.getTime());

      return difference / (24 * 60 * 60 * 1000);

    } catch (Exception exception) {
      Log.i("DATE", "Cannot find day difference as long");
      return 99999;
    }
  } // getDateDifferenceAsLong

}