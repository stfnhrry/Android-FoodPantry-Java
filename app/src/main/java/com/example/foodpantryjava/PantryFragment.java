package com.example.foodpantryjava;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PantryFragment extends Fragment {

  RecyclerView pantryRecyclerView;


  public static ArrayList<Item> data = new ArrayList<>();
  Integer minCardWidth = 293;
  Integer columns = 1;

  int numItems;
  SaveFile hashMapFile = new SaveFile();
  Map<Integer, String[]> map = hashMapFile.pantry;
//  static ShoppingListFile staticShoppingList = new ShoppingListFile();
  //static ArrayList<String> itemNames = staticShoppingList.itemNames;
//  static ArrayList<String> itemNames = ShoppingListFile.itemNames;
  ArrayList<String> sizes = new ArrayList<>();
  Boolean inRemovingMode = false;

  public static AdapterClass adapter;

  Integer dataNum;

  public PantryFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_pantry, container, false);

    pantryRecyclerView = view.findViewById(R.id.recyclerView);
    pantryRecyclerView.setHasFixedSize(true);
    setNumberOfColumnsBasedOnScreenWidth();
    pantryRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), columns, LinearLayoutManager.VERTICAL, false));
    adapter = new AdapterClass(data);
    pantryRecyclerView.setAdapter(adapter);
    Decoration decoration = new Decoration(8);
    pantryRecyclerView.addItemDecoration(decoration);

    return view;
  }

  @Override
  public void onDestroy(){
    super.onDestroy();
  }

  private void setNumberOfColumnsBasedOnScreenWidth(){
    int currentScreenWidth = getResources().getConfiguration().screenWidthDp;
    if (currentScreenWidth < 400){
      columns = 1;
    }
    else if (currentScreenWidth < 600){
      columns = (currentScreenWidth)/minCardWidth;
    }
    else if (currentScreenWidth > 1239){
      columns = (currentScreenWidth - 270)/minCardWidth;
    }
    else{
      columns = (currentScreenWidth - 64)/minCardWidth;
    }

//    Toast.makeText(getContext(), "The actual screen width is: " + currentScreenWidth + " , my double is: " + BigDecimal.valueOf(currentScreenWidth/minCardWidth) + " , and the columns are: " + columns, Toast.LENGTH_LONG).show();
  }

  public static class Decoration extends RecyclerView.ItemDecoration {
    private final int space;

    public Decoration(int space) {
      this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
      outRect.left = space;
      outRect.right = space;
      outRect.top = space;
      outRect.bottom = space;
    }
  }

//  @Override
//  public void onResume() {
////    super.onResume();
//    Log.i("SAVE", "onResume: Fragment was resumed");
//  }

  public void add(String name, String category, Integer number, String size, String expiryDate){
    pantryRecyclerView = getView().findViewById(R.id.recyclerView);
    Log.i("SAVE", "Recycler view items (Before adding) are now: " + pantryRecyclerView.getChildCount());
    dataNum = data.size();
    Log.i("SAVE", "Adding item to this index: " + dataNum);
    Log.i("SAVE", "Data before adding: " + data);
    data.add(dataNum, new Item(name, category, number, size, expiryDate));
    Log.i("SAVE", "Data after adding: " + data);
    adapter.notifyItemInserted(dataNum);
//    Log.i("SAVE", "Recycler view items (Just after adding) are now: " + pantryRecyclerView.getChildCount());
//    //    saveToArray(R.drawable.forkandspoon, name, category, number, size, expiryDate, dataSize);
//    if (pantryRecyclerView == null) {
//      Log.i("SAVE", "recycler view is null");
//    }
//    else {
//      Log.i("SAVE", "recycler view is not null");
//    }
//    Log.i("SAVE", "recycler view (just before looking for how many) has this many items: " + pantryRecyclerView.getChildCount());
//    if (pantryRecyclerView.getChildAt(dataNum) == null) {
//      Log.i("SAVE", "recycler view child at " + dataNum + " is null");
//    }
//    else {
//      Log.i("SAVE", "recycler view child at " + dataNum + " is valid");
//    }
  }
}