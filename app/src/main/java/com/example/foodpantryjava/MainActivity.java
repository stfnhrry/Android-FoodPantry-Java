package com.example.foodpantryjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

  public static PantryFragment itemViews;
  @Nullable private final Handler handler = new Handler();
  Toast lastToast;
  BottomNavigationView navigationBar;
  NavigationRailView navRail;
  NavigationView modalNavDrawer;
  NavigationView navDrawer;
  NavController navController;
  MenuItem item;
  private Boolean backPressedOnce = false;
  final Runnable runnable = this::setBackPressedToFalse;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.i("SAVE", "onCreate: ");
    itemViews = new PantryFragment();
    setupViews();
  }

  public void setupViews() {
    navigationBar = findViewById(R.id.bottom_navigation);
    navRail = findViewById(R.id.nav_rail);
    modalNavDrawer = findViewById(R.id.modal_nav_drawer);
    navDrawer = findViewById(R.id.nav_drawer);

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

    fabOptionOne.setOnClickListener(view -> showAddItemDialog());
    navRailFab.setOnClickListener(view -> showAddItemDialog());
    fabOptionTwo.setOnClickListener(view -> PantryFragment.adapter.getFilter().filter("l"));
    fabOptionThree.setOnClickListener(view -> showDeleteAllItemsDialog());

    // Update navigation views according to screen width size.
    int screenWidth = configuration.screenWidthDp;
    AdaptiveUtils.updateNavigationViewLayout(
        screenWidth,
        drawerLayout,
        modalNavDrawer,
        bottomNavFab,
        bottomNav,
        navRail,
        navDrawer,
        navRailFab);

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
  public void onBackPressed() {
    NavController navController = Navigation.findNavController(this, R.id.fragNavHost);

    // Check if current destination is the start point
    if (navController.getGraph().getStartDestination()
        == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
      // Was the back button pressed once already? If so then exit app
      if (backPressedOnce) {
        super.onBackPressed();
        return;
      }
      backPressedOnce = true;
      Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

      if (handler != null) {
        handler.postDelayed(runnable, 2000);
      }
    } else {
      super.onBackPressed();
      if (navigationBar.getMenu().getItem(0) != null) {
        item = navigationBar.getMenu().getItem(0);
        setSelectedMenuItem(item);
      }
    }
  }

  public void showAdd(View view) {
    showAddItemDialog();
  }

  public void setBackPressedToFalse() {
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

  /** Shows the add item dialog. */
  public void showAddItemDialog() {
    AddPantryItemDialog.display(getSupportFragmentManager());
  } // showAddItemDialog

  public void addNewItemToPantry(
      String name, String category, int amount, String weight, String expiryDate) {
    int index = SaveFile.data.size();
    addItem(name, category, amount, weight, expiryDate);
    saveToArray(R.drawable.forkandspoon, name, category, amount, weight, expiryDate, index);
  }

  /**
   * Shows the edit item dialog.
   *
   * @param index current card index
   */
  public void showEditItemDialog(int index) {
    EditPantryItemDialog.display(getSupportFragmentManager(), index);
  } // showEditItemDialog

  /**
   * Edits an item already in the pantry.
   *
   * @param index - the index of the item
   * @param name - name of the item
   * @param category - category of the item (can, jar, cookies,...)
   * @param amount - the amount in stock of the item
   * @param size - size of the item
   * @param expDate - expiry date of the item
   */
  public void editItem(
      int index, String name, String category, Integer amount, String size, String expDate) {
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

  public void showRemoveItemDialog(int index) {
    MaterialAlertDialogBuilder deleteDialog = new MaterialAlertDialogBuilder(this);
    deleteDialog.setTitle(getResources().getString(R.string.remove_item_title));
    deleteDialog.setMessage(getResources().getString(R.string.remove_item_message));
    deleteDialog.setCancelable(true);
    deleteDialog.setNegativeButton(
        getResources().getString(R.string.cancel),
        (dialogInterface, i) -> Log.i("DIALOG", "onClick: Negative button clicked"));
    deleteDialog.setPositiveButton(
        getResources().getString(R.string.remove),
        (dialogInterface, i) -> {
          Log.i("DIALOG", "onClick: Positive button clicked");
          removeItemFromPantry(index);
        });
    AlertDialog dialog = deleteDialog.create();
    dialog.show();
  }

  /**
   * Removes an item from the pantry.
   *
   * @param index the index of the current card to be removed
   */
  public void removeItemFromPantry(int index) {
    SaveFile.data.remove(index);
    PantryFragment.adapter.notifyItemRemoved(index);
    SaveFile.pantry.remove(index);

    int id = 0;
    HashMap<Integer, String[]> tempMap = new HashMap<>();
    Set<Map.Entry<Integer, String[]>> entries = SaveFile.pantry.entrySet();
    Iterator<Map.Entry<Integer, String[]>> iterator = entries.iterator();

    while (iterator.hasNext()) {
      Map.Entry<Integer, String[]> entry = iterator.next();
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
    if (increaseButton != null) {
      Log.i("ITEM", "Button valid");
      increaseButton.setOnClickListener(
          view -> {
            Log.i("ITEM", "Increase button was clicked");
            text.setText(incrementAmount(text));
          });
    } else {
      Log.i("ITEM", "Button not valid");
    }
    Button decreaseButton = customLayout.findViewById(R.id.decreaseButton);
    if (decreaseButton != null) {
      decreaseButton.setOnClickListener(
          view -> {
            Log.i("ITEM", "Decrease button was clicked");
            text.setText(decrementAmount(text));
          });
    }
    editAmountDialog.setPositiveButton(
        "Save",
        (dialogInterface, i) -> {
          editItem(
              index,
              SaveFile.data.get(index).name,
              SaveFile.data.get(index).category,
              Integer.parseInt(text.getText().toString()),
              SaveFile.data.get(index).size,
              SaveFile.data.get(index).expiryDate);
          Log.i("ITEM", "onClick: OK pressed");
        });
    editAmountDialog.setNegativeButton(
        "Cancel", (dialogInterface, i) -> Log.i("ITEM", "onClick: Cancel pressed"));
    AlertDialog dialog = editAmountDialog.create();
    dialog.show();
  }

  public boolean allowIncrement(int newAmount) {
    return newAmount <= 99999;
  }

  public boolean allowDecrement(int newAmount) {
    return newAmount >= 0;
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

  public void showAddToShoppingListDialog(int index) {
    MaterialAlertDialogBuilder addToShoppingListDialog = new MaterialAlertDialogBuilder(this);
    addToShoppingListDialog.setTitle("Add to Shopping List");
    final View customLayout =
        getLayoutInflater().inflate(R.layout.add_to_shopping_list_dialog, null);
    addToShoppingListDialog.setView(customLayout);
    TextView text = customLayout.findViewById(R.id.itemNameText);
    text.setText(SaveFile.data.get(index).name);
    TextInputEditText number = customLayout.findViewById(R.id.editAmountForShoppingCart);

    addToShoppingListDialog.setPositiveButton(
        "Add",
        (dialogInterface, i) -> {
          // needed on older versions of android for the override below to work
        });
    addToShoppingListDialog.setNegativeButton(
        "Cancel", (dialogInterface, i) -> Log.i("ITEM", "onClick: Cancel pressed"));

    AlertDialog dialog = addToShoppingListDialog.create();
    dialog.show();

    // code to prevent the wrong input from closing the dialog
    dialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            view -> {
              if (number.length() == 0) {
                number.setError("This field is required");
              } else if (!allowIncrement(
                  Integer.parseInt(Objects.requireNonNull(number.getText()).toString()))) {
                number.setError("Number too large");
              }
              // allowDecrement will accept 0 but we want it to accept 1 as the lowest value here
              else if (!allowDecrement(Integer.parseInt(number.getText().toString()) - 1)) {
                number.setError("Number too small");
              } else {
                addToShoppingList(index, number.getText().toString());
                dialog.dismiss();
              }
              Log.i("ITEM", "onClick: Positive shopping list button was pressed");
            });
  }

  /**
   * Adds an item to the shopping cart.
   *
   * @param index current card index
   */
  public void addToShoppingList(int index, String amount) {
    Log.i("MAIN", "addToShoppingList: ");
    Log.i("MAIN", "addToShoppingList: Before adding list is: " + SaveFile.list.size());
    String listItemInfo =
        SaveFile.data.get(index).name
            + ";break;"
            + amount
            + ";break;"
            + "Unused"
            + ";break;"
            + "Unused"
            + ";break;"
            + "F";
    SaveFile.list.add(listItemInfo);
    Log.i("MAIN", "addToShoppingList: After adding list is: " + SaveFile.list.size());
    ShoppingListFragment.adapter.notifyItemInserted(SaveFile.list.size());
    saveShoppingListToPreferences();
  } // addToShoppingList

  public void showDeleteAllItemsDialog() {
    MaterialAlertDialogBuilder deleteAllDialog = new MaterialAlertDialogBuilder(this);
    deleteAllDialog.setTitle(getResources().getString(R.string.delete_all_items_title));
    deleteAllDialog.setMessage(getResources().getString(R.string.delete_all_items_message));
    deleteAllDialog.setNegativeButton(
        getResources().getString(R.string.cancel),
        (dialogInterface, i) -> Log.i("DIALOG", "onClick: Negative button clicked"));
    deleteAllDialog.setPositiveButton(
        getResources().getString(R.string.delete_all),
        (dialogInterface, i) -> {
          Log.i("DIALOG", "onClick: Positive button clicked");
          deleteAllPantryItems();
        });
    AlertDialog dialog = deleteAllDialog.create();
    dialog.show();
  }

  public void deleteAllPantryItems() {
    SaveFile.data.clear();
    PantryFragment.adapter.notifyDataSetChanged();
    SaveFile.pantry.clear();
    saveHashmapToPreferences();
  }

  public void saveToArray(
      Integer icon,
      String name,
      String category,
      Integer amount,
      String weight,
      String expDate,
      int index) {
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

  /** Loads a new item from the array. */
  public void loadItemsFromHashmap() {
    for (int i = 0; i < SaveFile.pantry.size(); i++) {
      addItem(
          Objects.requireNonNull(SaveFile.pantry.get(i))[1],
          Objects.requireNonNull(SaveFile.pantry.get(i))[2],
          Integer.parseInt(Objects.requireNonNull(SaveFile.pantry.get(i))[3]),
          Objects.requireNonNull(SaveFile.pantry.get(i))[4],
          Objects.requireNonNull(SaveFile.pantry.get(i))[5]);
    }
  } // loadFromArray

  /**
   * Saves an item to the hashmap.
   *
   * @param index the index of the current card
   * @param ItemInfo the information in the current card
   */
  public void saveItemToHashMap(int index, String[] ItemInfo) {
    SaveFile.pantry.put(index, ItemInfo);
    saveHashmapToPreferences();
  } // saveToHashMap

  /** Saves the hashmap to shared preferences. */
  public void saveHashmapToPreferences() {
    // convert to string using gson
    Gson gson = new Gson();
    String hashMapString = gson.toJson(SaveFile.pantry);

    // save in shared prefs
    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.clear();
    editor.putString("hashString", hashMapString).apply();
  } // saveHashmapToPreferences

  /** Loads information from the hashmap. */
  public void loadHashMapFromPreferences() {
    Log.i("SAVE", "Load from hashmap");
    // get shared prefs
    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
    Gson gson = new Gson();
    // get HashMap as string from preferences
    String storedHashMapString = preferences.getString("hashString", "Empty");
    if (storedHashMapString.equals("Empty")) {
      Log.i("SAVE", "Hashmap is empty: ");
    } else {
      Log.i("SAVE", "Hashmap has stuff in it: ");
      java.lang.reflect.Type type = new TypeToken<HashMap<Integer, String[]>>() {}.getType();
      SaveFile.pantry = gson.<HashMap<Integer, String[]>>fromJson(storedHashMapString, type);
      loadItemsFromHashmap();
    }
  } // loadHashmapFromPrefs

  public void refreshAllItems() {
    SaveFile.data.clear();
    PantryFragment.adapter.notifyDataSetChanged();
    loadHashMapFromPreferences();
  } // refreshAllItems

  /** Sets the string array preference. */
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
   *
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

  /** Gets the shopping list from preferences. */
  public void loadShoppingList() {
    Log.i("MAIN", "loadShoppingList: Before adding list is: " + SaveFile.list);
    SaveFile.list = getShoppingListFromPreferences("ShoppingList");
    Log.i("MAIN", "loadShoppingList: After adding list is: " + SaveFile.list);
  } // getShoppingListFromPreferences

  /** Refreshes the shopping list. */
  public void refreshShoppingList() {
    SaveFile.list.clear();
    loadShoppingList();
  } // refreshShoppingList

  public void setNavigationRailItemOnClicks(NavigationRailView navRail) {
    navRail.setOnItemSelectedListener(
        item -> {
          switch (item.getTitle().toString()) {
            case "Pantry":
              if (R.id.pantryPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.pantryPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "Search":
              if (R.id.searchPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.searchPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "List":
              if (R.id.shoppingListPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.shoppingListPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "Settings":
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
        });
  }

  public void setNavigationDrawerItemOnClicks(NavigationView navView) {
    navView.setNavigationItemSelectedListener(
        item -> {
          switch (item.getTitle().toString()) {
            case "Pantry":
              if (R.id.pantryPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.pantryPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "Search":
              if (R.id.searchPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.searchPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "List":
              if (R.id.shoppingListPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.shoppingListPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "Settings":
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
        });
  }

  public void setModalNavigationDrawerItemOnClicks(NavigationView navView) {
    navView.setNavigationItemSelectedListener(
        item -> {
          switch (item.getTitle().toString()) {
            case "Pantry":
              if (R.id.pantryPage
                  != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                navController.navigate(R.id.pantryPage);
                setSelectedMenuItem(item);
              }
              break;
            case "Search":
              if (R.id.searchPage
                  != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                navController.navigate(R.id.searchPage);
                setSelectedMenuItem(item);
              }
              break;
            case "List":
              if (R.id.shoppingListPage
                  != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                navController.navigate(R.id.shoppingListPage);
                setSelectedMenuItem(item);
              }
              break;
            case "Settings":
              if (R.id.settingsPage
                  != Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                navController.navigate(R.id.settingsPage);
                setSelectedMenuItem(item);
              }
              break;
          }
          DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
          drawerLayout.closeDrawer(navView);
          return true;
        });
  }

  public void setNavigationBottomBarItemOnClicks(NavigationBarView navBar) {
    navBar.setOnItemSelectedListener(
        item -> {
          switch (item.getTitle().toString()) {
            case "Pantry":
              if (R.id.pantryPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.pantryPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "Search":
              if (R.id.searchPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.searchPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "List":
              if (R.id.shoppingListPage
                  == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
                return false;
              } else {
                navController.navigate(R.id.shoppingListPage);
                setSelectedMenuItem(item);
                return true;
              }
            case "Settings":
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
        });
  }

  public void setSelectedMenuItem(MenuItem item) {
    navigationBar.getMenu().findItem(item.getItemId()).setChecked(true);
    navRail.getMenu().findItem(item.getItemId()).setChecked(true);
    modalNavDrawer.setCheckedItem(item);
    navDrawer.setCheckedItem(item);
  }
}
