package com.example.foodpantryjava;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nambimobile.widgets.efab.ExpandableFab;
import com.nambimobile.widgets.efab.FabOption;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
//
//  Map<Integer, String[]> map = SaveFile.pantry;

  BottomNavigationView navigationBar;
  NavigationRailView navRail;
  NavigationView modalNavDrawer;
  NavigationView navDrawer;

  NavController navController;

  Dialog addNewItemDialog;
  Dialog editItemDialog;
  Button confirmDialogActionButton;
  Button closeDialogButton;
  EditText nameEditField;
  EditText amountEditField;
  EditText sizeEditField;
  EditText expiryDateEditField;
  boolean isEveryFieldChecked = false;

  MenuItem item;

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
    FabOption fabOptionTwo = findViewById(R.id.faboption_2);
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
    //    navRailFab.setOnClickListener(new View.OnClickListener() {
    //      @Override
    //      public void onClick(View view) {
    //        showAddItemDialog();
    //      }
    //    });
    fabOptionTwo.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            PantryFragment.adapter.getFilter().filter("l");
          }
        });
    fabOptionThree.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showDeleteAllItemsDialog();
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
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.i("SEARCH", "onCreateView: ");
    getMenuInflater().inflate(R.menu.top_app_bar_search, menu);

    MenuItem menuItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setQueryHint("Type here to search");
    searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String query) {
                return false;
              }

              @Override
              public boolean onQueryTextChange(String newText) {
                PantryFragment.adapter.getFilter().filter(newText);
                return false;
              }
            });

    return super.onCreateOptionsMenu(menu);
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
      if (navigationBar.getMenu().getItem(0) != null){
        item = navigationBar.getMenu().getItem(0);
        setSelectedMenuItem(item);
      }
    }
  }

  public void showAdd(View view) {
    showAddItemDialog();
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
    Log.i("SAVE", "ON RESUME Called");
    refreshAllItems();
    refreshShoppingList();
  }

  public void showLongToast(String text) {
    if (lastToast != null) {
      lastToast.cancel();
    }
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.show();
    lastToast = toast;
  } // showLongToast

  public void showShortToast(String text) {
    if (lastToast != null) {
      lastToast.cancel();
    }
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
    toast.show();
    lastToast = toast;
  } // showShortToast

  public void addItem(String name, String category, int amount, String weight, String expiryDate) {
    int dataIndex = SaveFile.data.size();
    SaveFile.data.add(dataIndex, new Item(name, category, amount, weight, expiryDate));
    PantryFragment.adapter.notifyItemInserted(dataIndex);
  }

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
    expiryDateEditField = addNewItemDialog.findViewById(R.id.editDateMasked);
    expiryDateEditField.setText("21022022");

    Spinner categorySpinner = addNewItemDialog.findViewById(R.id.spinner);
    ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categorySpinner.setAdapter(categoryAdapter);

    confirmDialogActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isEveryFieldChecked = checkAllInputFields();

        if (isEveryFieldChecked) {
          String nameString = nameEditField.getText().toString();
          String categoryString = categorySpinner.getSelectedItem().toString();
          int amountInteger = Integer.parseInt(amountEditField.getText().toString());
          String weightString = sizeEditField.getText().toString();
          String expDateString = expiryDateEditField.getText().toString();
          addNewItemToPantry(nameString, categoryString, amountInteger, weightString, expDateString);
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

  public void addNewItemToPantry(String name, String category, int amount, String weight, String expiryDate){
    int index = SaveFile.data.size();
    addItem(name, category, amount, weight, expiryDate);
    saveToArray(R.drawable.forkandspoon, name, category, amount, weight, expiryDate, index);
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
    expiryDateEditField = editItemDialog.findViewById(R.id.editDateMasked);

    // Set the text in the fields to match the data on the items
    nameEditField.setText(SaveFile.data.get(index).name);
    for (int i = 0; i < (categorySelector.getCount()); i++) {
      if (categorySelector.getItemAtPosition(i).toString().equalsIgnoreCase(SaveFile.data.get(index).category)) {
        categorySelector.setSelection(i);
      }
    }
    amountEditField.setText(SaveFile.data.get(index).number.toString());
    sizeEditField.setText(SaveFile.data.get(index).size);
    expiryDateEditField.setText(SaveFile.data.get(index).expiryDate);

    confirmDialogActionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isEveryFieldChecked = checkAllInputFields();
        if (isEveryFieldChecked) {
          editItem(index, nameEditField.getText().toString(), categorySelector.getSelectedItem().toString(), Integer.parseInt(amountEditField.getText().toString()), sizeEditField.getText().toString(), expiryDateEditField.getText().toString());
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
  public void editItem(int index, String name, String category, Integer amount, String size, String expDate) {
    SaveFile.data.set(index, new Item(name, category, amount, size, expDate));
    PantryFragment.adapter.notifyItemChanged(index);

    String iconString = Integer.toString(SaveFile.data.get(index).icon);
    String amountString = amount.toString();
    String[] temp = new String[6];
    temp[0] = iconString;
    temp[1] = name;
    temp[2] = category;
    temp[3] = amountString;
    temp[4] = size;
    temp[5] = expDate;

    SaveFile.pantry.replace(index, temp);
    saveHashmapToPreferences();
  } // editItem

  public void showRemoveItemDialog(int index){
    MaterialAlertDialogBuilder deleteDialog = new MaterialAlertDialogBuilder(this);
    deleteDialog.setTitle(getResources().getString(R.string.remove_item_title));
    deleteDialog.setMessage(getResources().getString(R.string.remove_item_message));
    deleteDialog.setCancelable(true);
    deleteDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("DIALOG", "onClick: Negative button clicked");
      }
    });
    deleteDialog.setPositiveButton(getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("DIALOG", "onClick: Positive button clicked");
        removeItemFromPantry(index);
      }
    });
    AlertDialog dialog = deleteDialog.create();
    dialog.show();
  }

  /**
   * Removes an item from the pantry.
   * @param index the index of the current card to be removed
   */
  public void removeItemFromPantry(int index) {
    SaveFile.data.remove(index);
    PantryFragment.adapter.notifyItemRemoved(index);
    SaveFile.pantry.remove(index);

    int id = 0;
    HashMap<Integer, String[]> tempMap = new HashMap<Integer, String[]>();
    Set<Map.Entry<Integer, String[]>> entries = SaveFile.pantry.entrySet();
    Iterator<Map.Entry<Integer, String[]>> iterator =
            entries.iterator();

    while (iterator.hasNext()) {
      Map.Entry<Integer, String[]> entry = iterator.next();
      Integer key = entry.getKey();
      String[] value = entry.getValue();

      tempMap.put(id, value);
      id++;
    }
    SaveFile.pantry = tempMap;
    saveHashmapToPreferences();
  } // removeItemFromPantry

  public void showEditItemAmountDialog(int index) {
    MaterialAlertDialogBuilder editAmountDialog = new MaterialAlertDialogBuilder(this);
    editAmountDialog.setTitle("Change Amount");
    final View customLayout = getLayoutInflater().inflate(R.layout.edit_amount_dialog, null);
    editAmountDialog.setView(customLayout);
    TextView text = customLayout.findViewById(R.id.amountTextInAmountDialog);
    text.setText(SaveFile.data.get(index).number.toString());
    Button increaseButton = customLayout.findViewById(R.id.increaseButton);
    if (increaseButton != null){
      Log.i("ITEM", "Button valid");
      increaseButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.i("ITEM", "Increase button was clicked");
          text.setText(incrementAmount(text));
        }
      });
    } else {
      Log.i("ITEM", "Button not valid");
    }
    Button decreaseButton = customLayout.findViewById(R.id.decreaseButton);
    if (decreaseButton != null) {
      decreaseButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.i("ITEM", "Decrease button was clicked");
          text.setText(decrementAmount(text));
        }
      });
    }
    editAmountDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        editItem(index, SaveFile.data.get(index).name, SaveFile.data.get(index).category, Integer.parseInt(text.getText().toString()), SaveFile.data.get(index).size, SaveFile.data.get(index).expiryDate);
        Log.i("ITEM", "onClick: OK pressed");
      }
    });
    editAmountDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("ITEM", "onClick: Cancel pressed");
      }
    });
    AlertDialog dialog = editAmountDialog.create();
    dialog.show();
  }

  public boolean allowIncrement(int newAmount){
    if (newAmount <= 99999) {
      return true;
    } else {
      return false;
    }
  }

  public boolean allowDecrement(int newAmount){
    if (newAmount >= 0) {
      return true;
    } else {
      return false;
    }
  }

  public String incrementAmount(TextView text) {
    int newAmount = Integer.parseInt(text.getText().toString());
    newAmount++;
    if (allowIncrement(newAmount)) {
      return String.valueOf(newAmount);
    } else {
      showShortToast("Cannot increase amount further");
      return String.valueOf(newAmount - 1);
    }
  }

  public String decrementAmount(TextView text) {
    int newAmount = Integer.parseInt(text.getText().toString());
    newAmount--;
    if (allowDecrement(newAmount)) {
      return String.valueOf(newAmount);
    } else {
      showShortToast("Cannot decrease amount further");
      return String.valueOf(newAmount + 1);
    }
  }

  public void showAddToShoppingListDialog(int index){
    MaterialAlertDialogBuilder addToShoppingListDialog = new MaterialAlertDialogBuilder(this);
    addToShoppingListDialog.setTitle("Add to Shopping List");
    final View customLayout = getLayoutInflater().inflate(R.layout.add_to_shopping_list_dialog, null);
    addToShoppingListDialog.setView(customLayout);
    TextView text = customLayout.findViewById(R.id.itemNameText);
    text.setText(SaveFile.data.get(index).name);
    TextInputEditText number = customLayout.findViewById(R.id.editAmountForShoppingCart);

    addToShoppingListDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        // needed on older versions of android for the override below to work
      }
    });
    addToShoppingListDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("ITEM", "onClick: Cancel pressed");
      }
    });

    AlertDialog dialog = addToShoppingListDialog.create();
    dialog.show();

    //code to prevent the wrong input from closing the dialog
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (number.length() == 0) {
          number.setError("This field is required");
        }
        else if (!allowIncrement(Integer.parseInt(number.getText().toString()))) {
          number.setError("Number too large");
        }
        // allowDecrement will accept 0 but we want it to accept 1 as the lowest value here
        else if (!allowDecrement(Integer.parseInt(number.getText().toString()) - 1)) {
          number.setError("Number too small");
        }
        else{
          addToShoppingList(index, number.getText().toString());
          dialog.dismiss();
        }
        Log.i("ITEM", "onClick: Positive shopping list button was pressed");
      }
    });
  }

  /**
   * Adds an item to the shopping cart.
   * @param index current card index
   */
  public void addToShoppingList(int index, String amount) {
    Log.i("MAIN", "addToShoppingList: ");
    StringBuilder listItemInfo = new StringBuilder();
    listItemInfo.append(SaveFile.data.get(index).name).append(";break;");
    listItemInfo.append(amount).append(";break;");
    listItemInfo.append("Unused").append(";break;");
    listItemInfo.append("Unused").append(";break;");
    listItemInfo.append("F");
    Log.i("MAIN", "addToShoppingList: Before adding list is: " + SaveFile.list.size());
    SaveFile.list.add(listItemInfo.toString());
    Log.i("MAIN", "addToShoppingList: After adding list is: " + SaveFile.list.size());
    ShoppingListFragment.adapter.notifyItemInserted(SaveFile.list.size());
    saveShoppingListToPreferences();
  } // addToShoppingList

  public void showDeleteAllItemsDialog(){
    MaterialAlertDialogBuilder deleteAllDialog = new MaterialAlertDialogBuilder(this);
    deleteAllDialog.setTitle(getResources().getString(R.string.delete_all_items_title));
    deleteAllDialog.setMessage(getResources().getString(R.string.delete_all_items_message));
    deleteAllDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("DIALOG", "onClick: Negative button clicked");
      }
    });
    deleteAllDialog.setPositiveButton(getResources().getString(R.string.delete_all), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("DIALOG", "onClick: Positive button clicked");
        deleteAllPantryItems();
      }
    });
    AlertDialog dialog = deleteAllDialog.create();
    dialog.show();
  }

  public void deleteAllPantryItems(){
    SaveFile.data.clear();
    PantryFragment.adapter.notifyDataSetChanged();
    SaveFile.pantry.clear();
    saveHashmapToPreferences();
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
    saveItemToHashMap(index, temp);
  } // saveToArray

  /**
   * Loads a new item from the array.
   */
  public void loadItemsFromHashmap() {
    for (int i = 0; i < SaveFile.pantry.size(); i++) {
      addItem(SaveFile.pantry.get(i)[1], SaveFile.pantry.get(i)[2], Integer.parseInt(SaveFile.pantry.get(i)[3]), SaveFile.pantry.get(i)[4], SaveFile.pantry.get(i)[5]);
    }
  } // loadFromArray

  /**
   * Saves an item to the hashmap.
   * @param index the index of the current card
   * @param ItemInfo the information in the current card
   */
  public void saveItemToHashMap(int index, String[] ItemInfo) {
    SaveFile.pantry.put(index, ItemInfo);
    saveHashmapToPreferences();
  } // saveToHashMap

  /**
   * Saves the hashmap to shared preferences.
   */
  public void saveHashmapToPreferences() {
    //convert to string using gson
    Gson gson = new Gson();
    String hashMapString = gson.toJson(SaveFile.pantry);

    //save in shared prefs
    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.clear();
    editor.putString("hashString", hashMapString).apply();
  } // saveHashmapToPreferences

  /**
   * Loads information from the hashmap.
   */
  public void loadHashMapFromPreferences() {
    Log.i("SAVE", "Load from hashmap");
    //get shared prefs
    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
    Gson gson = new Gson();
    //get HashMap as string from preferences
    String storedHashMapString = preferences.getString("hashString", "Empty");
    if (storedHashMapString.equals("Empty")) {
      Log.i("SAVE", "Hashmap is empty: ");
    } else {
      Log.i("SAVE", "Hashmap has stuff in it: ");
      java.lang.reflect.Type type = new TypeToken<HashMap<Integer, String[]>>() {
      }.getType();
      HashMap<Integer, String[]> testHashMap2 = gson.fromJson(storedHashMapString, type);
      SaveFile.pantry = testHashMap2;
      loadItemsFromHashmap();
    }
  } // loadHashmapFromPrefs

  public void refreshAllItems() {
    SaveFile.data.clear();
    PantryFragment.adapter.notifyDataSetChanged();
    loadHashMapFromPreferences();
  } // refreshAllItems

  /**
   * Sets the string array preference.
   */
  public void saveShoppingListToPreferences() {
    Log.i("MAIN", "saveShoppingListToPrefs: Before adding list is: " + SaveFile.list);
    String key = "ShoppingList";
    SharedPreferences prefs = getSharedPreferences("LIST", 0);
    SharedPreferences.Editor editor = prefs.edit();
    JSONArray array = new JSONArray();
    for (int i = 0; i < SaveFile.list.size(); i++) {
      array.put(SaveFile.list.get(i));
    }
    if (!SaveFile.list.isEmpty()) {
      Log.i("MAIN", "addToShoppingList: Put in array");
      editor.putString(key, array.toString());
    } else {
      Log.i("MAIN", "addToShoppingList: Not put in array");
      editor.putString(key, null);
    }
    editor.apply();
  } // saveShoppingListToPrefs

  /**
   * Gets the string array preference.
   * @param key the key
   * @return - the urls
   */
  public List<String> getShoppingListFromPreferences(String key) {
    Log.i("MAIN", "getShoppingListFromPreferences: ");
    SharedPreferences prefs = getSharedPreferences("LIST", 0);
    String json = prefs.getString(key, null);
    List<String> stringArray = new ArrayList<>();
    if (json != null) {
      try {
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
          String listItem = array.optString(i);
          stringArray.add(listItem);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return stringArray;
  } // getShoppingListFromPrefs

  /**
   * Stores the shopping list to preferences.
   */
  public void storeShoppingListCALLFUNCTION() {
    saveShoppingListToPreferences();
  } // storeShoppingListToPreference

  /**
   * Gets the shopping list from preferences.
   */
  public void loadShoppingList() {
    Log.i("MAIN", "loadShoppingList: Before adding list is: " + SaveFile.list);
    SaveFile.list = getShoppingListFromPreferences("ShoppingList");
    Log.i("MAIN", "loadShoppingList: After adding list is: " + SaveFile.list);
  } // getShoppingListFromPreferences

  /**
   * Refreshes the shopping list.
   */
  public void refreshShoppingList() {
    SaveFile.list.clear();
    loadShoppingList();
  } // refreshShoppingList

  public void setNavigationRailItemOnClicks(NavigationRailView navRail){
    navRail.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.pantryPage:
            if (R.id.pantryPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.pantryPage);
              setSelectedMenuItem(item);
              return true;
            }
          case R.id.searchPage:
            if (R.id.searchPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.searchPage);
              setSelectedMenuItem(item);
              return true;
            }
          case R.id.shoppingListPage:
            if (R.id.shoppingListPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.shoppingListPage);
              setSelectedMenuItem(item);
              return true;
            }
          case R.id.settingsPage:
            if (R.id.settingsPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.settingsPage);
              setSelectedMenuItem(item);
              return true;
            }
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
            if (R.id.pantryPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.pantryPage);
              setSelectedMenuItem(item);
              return true;
            }
          case R.id.searchPage:
            if (R.id.searchPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.searchPage);
              setSelectedMenuItem(item);
              return true;
            }
          case R.id.shoppingListPage:
            if (R.id.shoppingListPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.shoppingListPage);
              setSelectedMenuItem(item);
              return true;
            }
          case R.id.settingsPage:
            if (R.id.settingsPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              return false;
            } else {
              navController.navigate(R.id.settingsPage);
              setSelectedMenuItem(item);
              return true;
            }
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
            if (R.id.pantryPage != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              navController.navigate(R.id.pantryPage);
              setSelectedMenuItem(item);
            }
            break;
          case R.id.searchPage:
            if (R.id.searchPage != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              navController.navigate(R.id.searchPage);
              setSelectedMenuItem(item);
            }
            break;
          case R.id.shoppingListPage:
            if (R.id.shoppingListPage != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              navController.navigate(R.id.shoppingListPage);
              setSelectedMenuItem(item);
            }
            break;
          case R.id.settingsPage:
            if (R.id.settingsPage != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
              navController.navigate(R.id.settingsPage);
              setSelectedMenuItem(item);
            }
            break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(navView);
        return true;
      }
    });
  }

  public void setNavigationBottomBarItemOnClicks(NavigationBarView navBar){
    navBar.setOnItemSelectedListener(
        new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.pantryPage:
                if (R.id.pantryPage
                    == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                  return false;
                } else {
                  navController.navigate(R.id.pantryPage);
                  setSelectedMenuItem(item);
                  return true;
                }
              case R.id.searchPage:
                if (R.id.searchPage
                        == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                  return false;
                } else {
                  navController.navigate(R.id.searchPage);
                  setSelectedMenuItem(item);
                  return true;
                }
              case R.id.shoppingListPage:
                if (R.id.shoppingListPage
                        == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                  return false;
                } else {
                  navController.navigate(R.id.shoppingListPage);
                  setSelectedMenuItem(item);
                  return true;
                }
              case R.id.settingsPage:
                if (R.id.settingsPage
                        == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                  return false;
                } else {
                  navController.navigate(R.id.settingsPage);
                  setSelectedMenuItem(item);
                  return true;
                }
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
  public boolean checkAllInputFields() {
    // nameEditField checks
    if (nameEditField.length() == 0) {
      nameEditField.setError("This field is required");
      return false;
    }
    if (nameEditField.length() > 28) {
      nameEditField.setError("Maximum 28 characters, currently: " + nameEditField.length());
      return false;
    }
    // amountEditField checks
    if (amountEditField.length() == 0) {
      amountEditField.setError("This field is required");
      return false;
    }
    if (amountEditField.length() > 5) {
      amountEditField.setError("Number too large");
      return false;
    }
    // sizeEditField checks
    if (sizeEditField.length() == 0) {
      sizeEditField.setError("This field is required");
      return false;
    }
    if (sizeEditField.length() > 10) {
      sizeEditField.setError("Too long, give size and unit eg. 20 kg");
      return false;
    }
    // expiryDateEditField checks
    if (expiryDateEditField.length() == 0) {
      expiryDateEditField.setError("This field is required");
      return false;
    }
//    else if (expiryDateEditField.length() != 8) {
//      expiryDateEditField.setError("Format not correct, should be DD/MM/YYYY");
//      return false;
//    }
    else if (!isDateValid(expiryDateEditField.getText().toString())) {
      expiryDateEditField.setError("Date not correct, should be DD/MM/YYYY");
      return false;
    }
    return true;
  } // checkAllFields

  public boolean isDateValid(String date) {
    try {
      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
      dateFormat.setLenient(false);
      dateFormat.parse(date);
      return true;
    } catch (ParseException e) {
      return false;
    }
  }

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