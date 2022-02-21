package com.example.foodpantryjava;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AddPantryItemDialog extends DialogFragment {
  public static final String TAG = "add_item_dialog";

  private Toolbar toolbar;

  public static AddPantryItemDialog display(FragmentManager fragmentManager){
    AddPantryItemDialog addDialog = new AddPantryItemDialog();
    addDialog.show(fragmentManager, TAG);
    return addDialog;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FullScreenDialog);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.add_item_dialog, container, false);

    toolbar = view.findViewById(R.id.toolbar);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });
    toolbar.setTitle(R.string.add_new_item);
    toolbar.inflateMenu(R.menu.add_edit_item_menu);
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        dismiss();
        //do stuff when the save button is pressed here
        return true;
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if(dialog != null){
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }
}
