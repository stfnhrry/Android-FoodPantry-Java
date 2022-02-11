package com.example.foodpantryjava;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

  LinearLayout listLayout;
  View myView;
  ViewGroup viewGroup;

  public ShoppingListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.i("SHOPPING LIST FRAGMENT", "OnCreateView");
    // Inflate the layout for this fragment
    myView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
    viewGroup = (ViewGroup) myView.findViewById(R.id.shoppingListView); // returns base view of the fragment
    listLayout = myView.findViewById(R.id.shoppingListView);
    ImageButton clearList = myView.findViewById(R.id.ClearAll);
    clearList.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SaveFile.list.clear();
        listLayout.removeAllViews();
        if (getActivity() != null) {
          Log.i("SHOPPING LIST FRAGMENT", "List was cleared");
          ((MainActivity)getActivity()).saveShoppingListToPreferences();
        }
      }
    });
    populateList();
    return myView;
  }

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
      MaterialCardView card = popup.findViewById(R.id.background1);
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