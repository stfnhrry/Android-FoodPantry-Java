package com.example.foodpantryjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

  public SearchFragment() {
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
    return inflater.inflate(R.layout.fragment_search, container, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu search, MenuInflater inflater){
    inflater.inflate(R.menu.search, search);
  }
}