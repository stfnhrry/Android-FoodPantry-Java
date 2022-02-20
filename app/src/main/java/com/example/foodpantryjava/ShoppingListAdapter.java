package com.example.foodpantryjava;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
  //Listener for implementing functions when pressing cards
  public interface listCardListener {
    void onDelete(int position);
  }

  public ShoppingListAdapter() {}

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
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
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.listItemName);
      info = itemView.findViewById(R.id.listItemExtra);
      card = itemView.findViewById(R.id.background);
      checkBox = itemView.findViewById(R.id.listItemCheckbox);
    }

    public void bind() {
      Log.i("LIST ADAPTER", "bind");
      title.setText(SaveFile.list.get(getAdapterPosition()));
      card.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          checkBox.setChecked(!checkBox.isChecked());
        }
      });
    }
  }
}
