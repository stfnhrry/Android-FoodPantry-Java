package com.example.foodpantryjava;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nambimobile.widgets.efab.ExpandableFab;
import com.nambimobile.widgets.efab.FabOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

  SaveFile hashMapFile = new SaveFile();
  Map<Integer, String[]> map = hashMapFile.pantry;
  ArrayList<Item> data = hashMapFile.data;

  BottomNavigationView navigationBar;
  NavigationRailView navRail;
  NavigationView modalNavDrawer;
  NavigationView navDrawer;

  NavController navController;
  Integer dataSize = 0;

  Dialog addDialog;
  Dialog editDialog;
  boolean isEveryFieldChecked = false;
  Button addButton;
  Button closeButton;
  EditText name;
  EditText amount;
  EditText weight;
  EditText expDate;

//  ArrayList<Item> data = new ArrayList<>();

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

    //onCreateDemoView stuff
    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    View bottomNav = findViewById(R.id.coordinatorLayout);
    ExpandableFab fab = findViewById(R.id.fab);
    FabOption addItem = findViewById(R.id.faboption_1);
    FabOption removeItem = findViewById(R.id.faboption_3);
    ExtendedFloatingActionButton navFab = findViewById(R.id.nav_fab);

    Configuration configuration = getResources().getConfiguration();
    FragmentManager fragmentManager = getSupportFragmentManager();


    addItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showAddItemDialog();
      }
    });
    navFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showAddItemDialog();
      }
    });
    removeItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        data.clear();
        itemViews.adapter.notifyDataSetChanged();
        map.clear();
        saveHashmapToPreferences();
        loadFromHashmap();
      }
    });

    // Update navigation views according to screen width size.
    int screenWidth = configuration.screenWidthDp;
    AdaptiveUtils.updateNavigationViewLayout(
            screenWidth, drawerLayout, modalNavDrawer, fab, bottomNav, navRail, navDrawer, navFab);

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
    addDialog = new Dialog(this);
    addDialog.setContentView(R.layout.add_item_dialog);

    addButton = addDialog.findViewById(R.id.confirmButton);
    closeButton = addDialog.findViewById(R.id.cancelButton);
    name = addDialog.findViewById(R.id.editName);
    amount = addDialog.findViewById(R.id.editAmount);
    weight = addDialog.findViewById(R.id.editSize);
    weight.setText("10kg");
    expDate = addDialog.findViewById(R.id.editDate);
    expDate.setText("21/02/2022");

    Spinner categorySpinner = addDialog.findViewById(R.id.spinner);
    ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categorySpinner.setAdapter(categoryAdapter);

    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isEveryFieldChecked = checkAllFields();

        if (isEveryFieldChecked) {
          int image = setIconFromCategory(categorySpinner);
          String nameString = name.getText().toString();
          String categoryString = categorySpinner.getSelectedItem().toString();
          int amountInteger = Integer.parseInt(amount.getText().toString());
          String weightString = weight.getText().toString();
          String expDateString = expDate.getText().toString();
          addNewItem(nameString, categoryString, amountInteger, weightString, expDateString);
        }
      }
    });

    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        hideKeyboard(name);
        addDialog.dismiss();
      }
    });

    addDialog.show();
  } // showAddItemDialog

  public void addNewItem(String name, String category, Integer number, String size, String expiryDate){

    Log.i("SAVE", "Recycler view items (Before adding) are now: " + itemViews.pantryRecyclerView.getChildCount());
    dataNum = data.size();
    Log.i("SAVE", "Adding item to this index: " + dataNum);
    data.add(dataNum, new Item(name, category, number, size, expiryDate));
    itemViews.adapter.notifyItemInserted(dataNum);
    Log.i("SAVE", "Recycler view items (Just after adding) are now: " + itemViews.pantryRecyclerView.getChildCount());
    //    saveToArray(R.drawable.forkandspoon, name, category, number, size, expiryDate, dataSize);
    if (itemViews.pantryRecyclerView == null) {
      Log.i("SAVE", "recycler view is null");
    }
    else {
      Log.i("SAVE", "recycler view is not null");
    }
    Log.i("SAVE", "recycler view (just before looking for how many) has this many items: " + itemViews.pantryRecyclerView.getChildCount());
    if (itemViews.pantryRecyclerView.getChildAt(dataNum) == null) {
      Log.i("SAVE", "recycler view child at " + dataNum + " is null");
    }
    else {
      Log.i("SAVE", "recycler view child at " + dataNum + " is valid");
    }


//        itemViews.data.clear();
//        map.clear();
//    dataSize = itemViews.data.size();
//    itemViews.data.add(dataSize, new Item(name, category, number, size, expiryDate));
//    itemViews.adapter.notifyItemInserted(dataSize);
//    itemViews.add(name, category, number, size, expiryDate);
    Log.i("SAVE", "Recycler view items(Added to data, before adding to array) are now: " + itemViews.pantryRecyclerView.getChildCount());
    saveToArray(R.drawable.forkandspoon, name, category, number, size, expiryDate, dataNum);
//    itemViews.pantryRecyclerView.getChildAt(dataSize).findViewById(R.id.removeButton).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        //code here
//      }
//    });
  }

  /**
   * Removes an item from the pantry.
   * @param index the index of the current card to be removed
   */
  public void removeItemFromPantry(int index) {
    Log.i("SAVE", "Remove item from hashmap at index " + index);
    Log.i("SAVE", "Hashmap is currently " + map);
//    cardLayout.removeViewAt(index);
//    itemViews.data.remove(index);
//    itemViews.adapter.notifyItemRemoved(index);
    map.remove(index);
    Log.i("SAVE", "Hashmap is now " + map);

    int id = 0;
    HashMap<Integer, String[]> tempMap = new HashMap<Integer, String[]>();

    Set<Map.Entry<Integer, String[]>> entries = map.entrySet();

    Iterator<Map.Entry<Integer, String[]>> iterator =
            entries.iterator();

    while (iterator.hasNext()) {
      Map.Entry<Integer, String[]> entry = iterator.next();
      Integer key = entry.getKey();
      String[] value = entry.getValue();

      Log.i("SAVE", "Hashmap index " + key + " is " + value);

      tempMap.put(id, value);
      id++;
    }
    map = tempMap;
    saveHashmapToPreferences();
//    refreshAllItems();
//    if (inRemovingMode == true) {
//      setRemoveModeActive();
//    } else {
//      setRemoveModeInactive();
//    }
  } // removeItemFromPantry

  public void loadNewItem(int icon, String name, String category, int amount, String weight, String expDate) {
//    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//    transaction.add(cardLayout.getId(), ItemFragment.newInstance(icon, name, category, amount, weight, expDate));
//    transaction.commitNow();
    itemViews.add(name, category, amount, weight, expDate);
//    saveToArray(R.drawable.forkandspoon, name, category, amount, weight, expDate, dataSize);

////    numItems = cardLayout.getChildCount();
//    numItems = pantryRecyclerView.getChildCount();
//
//    View card = pantryRecyclerView.getChildAt(numItems - 1);
//    ImageButton removeItemButton = pantryRecyclerView.getChildAt(numItems - 1).findViewById(R.id.removeButton);
//    TextView cardText = pantryRecyclerView.getChildAt(numItems - 1).findViewById(R.id.titleText);
//    ImageButton editButton = card.findViewById(R.id.editButton);
//    ImageButton addToShopButton = pantryRecyclerView.getChildAt(numItems - 1).findViewById(R.id.toShoppingListButton);
//    removeItemButton.setVisibility(View.GONE);
//    int id = numItems - 1;
//
//    editButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
////        showEditItemDialog(card);
//      }
//    });
//    addToShopButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
////        addToCart(card);
////        showToast("Item has been added to shopping list");
//
//        addToShopButton.setEnabled(false);
//        addToShopButton.postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            addToShopButton.setEnabled(true);
//            Log.d(TAG, "disabled button");
//          }
//        }, 500);
//      }
//
//    });
//    removeItemButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
////        removeItemFromPantry(id);
//      }
//    });
  }

  public void saveToArray(int icon, String name, String category, int amount, String weight, String expDate, int index) {
    Log.i("SAVE", "saveToArray");
    String iconString = icon + "";
    String amountString = amount + "";
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
   * Loads information from the hasmap.
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
    RecyclerView pantryRecyclerView = findViewById(R.id.recyclerView);
//    pantryRecyclerView.removeAllViewsInLayout();
//    itemViews.data.clear();
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
    if (name.length() == 0) {
      name.setError("This field is required");
      return false;
    }
    if (amount.length() == 0) {
      amount.setError("This field is required");
      return false;
    }
    if (weight.length() == 0) {
      weight.setError("This field is required");
      return false;
    }
    if (expDate.length() == 0) {
      expDate.setError("This field is required");
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

}