package com.example.foodpantryjava;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditPantryItemDialog extends DialogFragment {
  public static final String TAG = "edit_item_dialog";
  static int itemIndex;
  EditText nameEditField;
  EditText amountEditField;
  EditText sizeEditField;
  EditText expiryDateEditField;
  Spinner categorySelector;
  boolean isEveryFieldChecked = false;
  private Toolbar toolbar;

  public static EditPantryItemDialog display(FragmentManager fragmentManager, int index) {
    EditPantryItemDialog editDialog = new EditPantryItemDialog();
    editDialog.show(fragmentManager, TAG);
    itemIndex = index;
    return editDialog;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FullScreenDialog);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.add_edit_item_dialog, container, false);

    toolbar = view.findViewById(R.id.toolbar);
    nameEditField = view.findViewById(R.id.editName);
    amountEditField = view.findViewById(R.id.editAmount);
    sizeEditField = view.findViewById(R.id.editSize);
    expiryDateEditField = view.findViewById(R.id.editDateMasked);
    categorySelector = view.findViewById(R.id.spinner);
    ArrayAdapter<CharSequence> categoryAdapter =
        ArrayAdapter.createFromResource(
            getContext(), R.array.categories, android.R.layout.simple_spinner_item);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categorySelector.setAdapter(categoryAdapter);

    // Set the text in the fields to match the data on the items
    nameEditField.setText(SaveFile.data.get(itemIndex).name);
    amountEditField.setText(SaveFile.data.get(itemIndex).number.toString());
    sizeEditField.setText(SaveFile.data.get(itemIndex).size);
    expiryDateEditField.setText(SaveFile.data.get(itemIndex).expiryDate);
    for (int i = 0; i < (categorySelector.getCount()); i++) {
      if (categorySelector
          .getItemAtPosition(i)
          .toString()
          .equalsIgnoreCase(SaveFile.data.get(itemIndex).category)) {
        categorySelector.setSelection(i);
      }
    }

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar.setNavigationOnClickListener(view1 -> dismiss());
    toolbar.setTitle(R.string.edit_item);
    toolbar.inflateMenu(R.menu.add_edit_item_menu);
    toolbar.setOnMenuItemClickListener(
        item -> {
          isEveryFieldChecked = checkAllInputFields();
          if (isEveryFieldChecked) {
            ((MainActivity) requireActivity())
                .editItem(
                    itemIndex,
                    nameEditField.getText().toString(),
                    categorySelector.getSelectedItem().toString(),
                    Integer.parseInt(amountEditField.getText().toString()),
                    sizeEditField.getText().toString(),
                    expiryDateEditField.getText().toString());
            dismiss();
            return true;
          }
          return false;
        });
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
      dialog.getWindow().setWindowAnimations(R.style.Theme_Slide);
    }
  }

  /**
   * Checks all fields.
   *
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
    } else if (!isDateValid(expiryDateEditField.getText().toString())) {
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
}
