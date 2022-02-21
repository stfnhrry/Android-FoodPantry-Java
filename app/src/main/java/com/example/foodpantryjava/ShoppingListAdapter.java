package com.example.foodpantryjava;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
  // Listener for implementing functions when pressing cards
  public interface listCardListener {
    void onSave();
  }

  private final listCardListener mListener;

  public ShoppingListAdapter(listCardListener listener) {
    mListener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View myView =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.shopping_list_item, parent, false);
    Log.i("LIST ADAPTER", "onCreateViewHolder: ");
    return new ViewHolder(myView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind();
  }

  @Override
  public int getItemCount() {
    return SaveFile.list.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView title, info;
    MaterialCardView card;
    CheckBox checkBox;
    MaterialButton deleteButton;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.listItemName);
      info = itemView.findViewById(R.id.listItemExtra);
      card = itemView.findViewById(R.id.background);
      checkBox = itemView.findViewById(R.id.listItemCheckbox);
      deleteButton = itemView.findViewById(R.id.listItemDeleteButton);
    }

    public void bind() {
      Log.i("SHOPPING LIST ADAPTER", "bind");
      String[] allItemInfo = SaveFile.list.get(getAdapterPosition()).split(";break;");
      title.setText(allItemInfo[0]);
      info.setText(allItemInfo[1]);
      if (card.getCheckedIcon() != null) {
        card.setCheckedIcon(null);
      }
      if (allItemInfo[4].equals("T")) {
        card.setChecked(true);
        checkBox.setChecked(true);
      } else if (allItemInfo[4].equals("F")) {
        card.setChecked(false);
        checkBox.setChecked(false);
      }
      deleteButton.setOnClickListener(
          view -> {
            SaveFile.list.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            mListener.onSave();
          });
      card.setOnClickListener(
          view -> {
            Log.i("SHOPPING LIST ADAPTER", "onClick: card");
            card.setChecked(!card.isChecked());
            checkBox.setChecked(card.isChecked());
            if (card.isChecked()) {
              SaveFile.list.set(
                  getAdapterPosition(), reconstructItemInfo(allItemInfo).append("T").toString());
              notifyItemChanged(getAdapterPosition());
            } else if (!card.isChecked()) {
              SaveFile.list.set(
                  getAdapterPosition(), reconstructItemInfo(allItemInfo).append("F").toString());
              notifyItemChanged(getAdapterPosition());
            }
            mListener.onSave();
          });
      checkBox.setOnClickListener(
          view -> {
            card.setChecked(!card.isChecked());
            checkBox.setChecked(card.isChecked());
            if (card.isChecked()) {
              SaveFile.list.set(
                  getAdapterPosition(), reconstructItemInfo(allItemInfo).append("T").toString());
              notifyItemChanged(getAdapterPosition());
            } else if (!card.isChecked()) {
              SaveFile.list.set(
                  getAdapterPosition(), reconstructItemInfo(allItemInfo).append("F").toString());
              notifyItemChanged(getAdapterPosition());
            }
            mListener.onSave();
          });
    }
  }

  public StringBuilder reconstructItemInfo(String[] oldInfo) {
    StringBuilder listItemInfo = new StringBuilder();
    listItemInfo.append(oldInfo[0]).append(";break;");
    listItemInfo.append(oldInfo[1]).append(";break;");
    listItemInfo.append("Unused").append(";break;");
    listItemInfo.append("Unused").append(";break;");
    return listItemInfo;
  }
}
