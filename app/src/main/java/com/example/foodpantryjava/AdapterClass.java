package com.example.foodpantryjava;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {

  ArrayList<Item> data;

  public AdapterClass(ArrayList<Item> data) {
    this.data = data;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//    data = sortByAge(data);
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, null));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//    if (data.get(position).age > 30) {
//      holder.name.setBackgroundColor(Color.BLUE);
//    }
//    else {
//      holder.name.setBackgroundColor(Color.YELLOW);
//    }
//    holder.icon.setImageResource(R.drawable.cookies);
    holder.name.setText(data.get(position).name);
    holder.category.setText(data.get(position).category);
    holder.number.setText(data.get(position).number.toString());
    holder.size.setText(data.get(position).size);
    holder.expiryDate.setText(data.get(position).expiryDate);

    holder.card.setOnClickListener(
            v -> Toast.makeText(
                    v.getContext(),
                    data.get(holder.getAdapterPosition()).name + " is clicked",
                    Toast.LENGTH_SHORT)
                .show());

    holder.card.setOnLongClickListener(
            v -> {
              Toast.makeText(
                      v.getContext(),
                      data.get(holder.getAdapterPosition()).name + " is long clicked",
                      Toast.LENGTH_SHORT)
                  .show();
              return true;
            });

    holder.removeB.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Integer index = holder.getAdapterPosition();
            Log.i("SAVE", "The index of this item is: " + index);
            Log.i("SAVE", "Data looks like: " + data);
            data.remove(index);
            Log.i("SAVE", "After removing, data looks like: " + data);
//            notifyItemRemoved(index);
            //            if (main != null) {
            //              Log.i("SAVE", "onClick: Main is not null");
            ////              main.map.remove(index);
            ////              main.removeItemFromPantry(index);
            //            }
            //            else{
            //              Log.i("SAVE", "onClick: Main activity is null");
            //            }
          }
        });
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView name, category, number, size, expiryDate;
    CardView card;
    ImageButton removeB, editB, addToListB;
    public ViewHolder (@NonNull View itemView) {
      super(itemView);
      card = itemView.findViewById(R.id.cardView);
      icon = itemView.findViewById(R.id.itemIcon);
      name = itemView.findViewById(R.id.titleText);
      category = itemView.findViewById(R.id.categoryText);
      number = itemView.findViewById(R.id.amountText);
      size = itemView.findViewById(R.id.sizeText);
      expiryDate = itemView.findViewById(R.id.expiryDateText);
      removeB = itemView.findViewById(R.id.removeButton);
      editB = itemView.findViewById(R.id.editButton);
      addToListB = itemView.findViewById(R.id.toShoppingListButton);
    }

  }

//  public ArrayList<Item> sortByAge(ArrayList<Item> data) {
//    Collections.sort(data, new Comparator<Item>() {
//      @Override
//      public int compare(Item o1, Item o2) {
//        if(o1.category >o2.category)
//          return 1;
//        else if(o1.category <o2.category)
//          return -1;
//        else return 0;
//      }
//    });
//    return data;
//  }
}
