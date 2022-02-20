package com.example.foodpantryjava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

  public static RecyclerView shoppingListRecyclerView;
  public static ShoppingListAdapter adapter;

  LinearLayout listLayout;
  ViewGroup viewGroup;

  public ShoppingListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.i("SHOPPING LIST FRAGMENT", "OnCreateView");
    // Inflate the layout for this fragment
    View myView = inflater.inflate(R.layout.fragment_shopping_list, container, false);

    shoppingListRecyclerView = myView.findViewById(R.id.recyclerView_ShoppingList);
    shoppingListRecyclerView.setHasFixedSize(true);
    shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    adapter = new ShoppingListAdapter();
    shoppingListRecyclerView.setAdapter(adapter);

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
    itemTouchHelper.attachToRecyclerView(shoppingListRecyclerView);

    return myView;
  }

  @Override
  public void onStart() {
    super.onStart();
    adapter.notifyDataSetChanged();
  }

  ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
      int fromPosition = viewHolder.getAdapterPosition();
      int toPosition = target.getAdapterPosition();

      Collections.swap(SaveFile.list, fromPosition, toPosition);
      ((MainActivity)getActivity()).saveShoppingListToPreferences();
      adapter.notifyItemMoved(fromPosition, toPosition);
      return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
      // code for when items are swiped would go here
    }
  };











  //    listLayout = myView.findViewById(R.id.shoppingListView);
  //Shopping list button was moved around
//    ImageButton clearList = myView.findViewById(R.id.ClearAll);
//    clearList.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        SaveFile.list.clear();
//        listLayout.removeAllViews();
//        if (getActivity() != null) {
//          Log.i("SHOPPING LIST FRAGMENT", "List was cleared");
//          ((MainActivity)getActivity()).saveShoppingListToPreferences();
//        }
//      }
//    });


  public void populateList(){
    Log.i("SHOPPING LIST FRAGMENT", "Populate list");
    Log.i("SHOPPING LIST FRAGMENT", "Before populating, list was: " + SaveFile.list);
//    viewGroup = (ViewGroup) myView.findViewById(R.id.shoppingListView); // returns base view of the fragment
    if (viewGroup == null) {
      Log.i("SHOPPING LIST FRAGMENT", "View is null");
      return;
    }
    if (!(viewGroup instanceof ViewGroup)) {
      Log.i("SHOPPING LIST FRAGMENT", "View is not an instance of viewgroup");
      return;
    }
    for (int i = 0; i < SaveFile.list.size(); i++) {
      View popup = View.inflate(viewGroup.getContext(), R.layout.shopping_list_item, null);
      TextView title = popup.findViewById(R.id.itemName);
      title.setText(SaveFile.list.get(i));
      MaterialCardView card = popup.findViewById(R.id.background);
      card.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
          card.setChecked(!card.isChecked());
          return true;
        }
      });
      listLayout.addView(popup);
    }
    Log.i("SHOPPING LIST FRAGMENT", "After populating, list is: " + SaveFile.list);
  }
}