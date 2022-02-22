package com.example.foodpantryjava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment {

  public RecipeFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_recipes, container, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu search, MenuInflater inflater){
    inflater.inflate(R.menu.search, search);
  }

  @Override
  public void onResume() {
    super.onResume();
    ((MainActivity)requireActivity()).setActionBarTitle("Recipes");
  }
}