package com.example.foodpantryjava;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class PantryFragment extends Fragment {

  public static RecyclerView pantryRecyclerView;
  ActionMenuItemView searchView;

  int minCardWidth = 293;
  Integer columns = 1;

  public static AdapterClass adapter;

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
    pantryRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));
    Decoration decoration = new Decoration(8);
    pantryRecyclerView.addItemDecoration(decoration);
    adapter = new AdapterClass(
            new AdapterClass.itemCardListener() {
              @Override
              public void onDelete(int position) {
                if (getActivity() != null) {
                  Log.i("PANTRY FRAGMENT", "Item was deleted");
                  ((MainActivity)getActivity()).showRemoveItemDialog(position);
                }
              }

              @Override
              public void onEdit(int position) {
                if (getActivity() != null) {
                  Log.i("PANTRY FRAGMENT", "Item was edited");
                  ((MainActivity)getActivity()).showEditItemDialog(position);
                }
              }

              @Override
              public void onAddToList(int position) {
                if (getActivity() != null) {
                  Log.i("PANTRY FRAGMENT", "Item was added to shopping list");
                  ((MainActivity)getActivity()).showAddToShoppingListDialog(position);
                }
              }

              @Override
              public void onLongPress(int position) {
                if (getActivity() != null) {
                  Log.i("PANTRY FRAGMENT", "Item was long pressed");
                  ((MainActivity)getActivity()).showEditItemAmountDialog(position);
                }
              }
            }, getResources());
    pantryRecyclerView.setAdapter(adapter);

    TabLayout tabLayout = view.findViewById(R.id.tab_layout);
    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
              case 0:
                adapter.showAllItems();
                break;
              case 1:
                Log.i("SEARCH", "onTabSelected: Low in stock");
                adapter.showLowInStockItems();
                break;
              case 2:
                adapter.showOutOfStockItems();
                break;
              case 3:
                adapter.showExpiringSoonItems();
                break;
              case 4:
                adapter.showExpiredItems();
                break;
            }
            if (tab.getPosition() != 100) {
              Log.i("SEARCH", "onTabSelected: " + tab.getPosition());
            }
          }
          @Override
          public void onTabUnselected(TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(TabLayout.Tab tab) {}
        });

    return view;
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
  }

  public static class Decoration extends RecyclerView.ItemDecoration {
    private final int space;

    public Decoration(int space) {
      this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
      outRect.left = space;
      outRect.right = space;
      outRect.top = space;
      outRect.bottom = space;
    }
  }
}