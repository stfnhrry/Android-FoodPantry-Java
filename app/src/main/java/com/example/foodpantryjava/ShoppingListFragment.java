package com.example.foodpantryjava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

/** A simple {@link Fragment} subclass. */
public class ShoppingListFragment extends Fragment {

  public static RecyclerView shoppingListRecyclerView;
  public static ShoppingListAdapter adapter;
  ItemTouchHelper.SimpleCallback simpleCallback =
      new ItemTouchHelper.SimpleCallback(
          ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
          0) {
        @Override
        public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target) {
          int fromPosition = viewHolder.getAdapterPosition();
          int toPosition = target.getAdapterPosition();

          Collections.swap(SaveFile.list, fromPosition, toPosition);
          ((MainActivity) requireActivity()).saveShoppingListToPreferences();
          adapter.notifyItemMoved(fromPosition, toPosition);
          return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
          // code for when items are swiped would go here
        }
      };

  public ShoppingListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.i("SHOPPING LIST FRAGMENT", "OnCreateView");
    // Inflate the layout for this fragment
    View myView = inflater.inflate(R.layout.fragment_shopping_list, container, false);

    shoppingListRecyclerView = myView.findViewById(R.id.recyclerView_ShoppingList);
    shoppingListRecyclerView.setHasFixedSize(true);
    shoppingListRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    adapter =
        new ShoppingListAdapter(
            () -> ((MainActivity) requireActivity()).saveShoppingListToPreferences());
    shoppingListRecyclerView.setAdapter(adapter);

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
    itemTouchHelper.attachToRecyclerView(shoppingListRecyclerView);

    return myView;
  }

  @Override
  public void onStart() {
    super.onStart();
    //    adapter.notifyDataSetChanged();
  }
}
