package com.example.foodpantryjava;

import android.content.res.Resources;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {
  //Listener for implementing functions on clicking item cards
  public interface itemCardListener {
    void onDelete(int position);
    void onEdit(int position);
    void onAddToList(int position);
  }
  private final itemCardListener mListener;
  ArrayList<Item> data = SaveFile.data;
  Resources mResource;

  static int daysTillExpiryWarning = 30;

  public AdapterClass(itemCardListener listener, Resources resources) {
    mListener = listener;
    mResource = resources;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
    Log.i("ADAPTER", "onCreateViewHolder: ");
//    data = sortByAge(data);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Log.i("ADAPTER", "onBindViewHolder: ");
    /*
    if (data.get(position).age > 30) {
      holder.name.setBackgroundColor(Color.BLUE);
    }
    else {
      holder.name.setBackgroundColor(Color.YELLOW);
    } */
    holder.bind();
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView name, category, number, size, expiryDate, expiryText;
    CardView card;
    ImageButton removeB, editB, addToListB;
    public ViewHolder (@NonNull View itemView) {
      super(itemView);
      Log.i("ADAPTER", "ViewHolder: the class itself");
      card = itemView.findViewById(R.id.cardView);
      icon = itemView.findViewById(R.id.itemIcon);
      name = itemView.findViewById(R.id.titleText);
      category = itemView.findViewById(R.id.categoryText);
      number = itemView.findViewById(R.id.amountText);
      size = itemView.findViewById(R.id.sizeText);
      expiryDate = itemView.findViewById(R.id.expiryDateText);
      expiryText = itemView.findViewById(R.id.expiryText);
      removeB = itemView.findViewById(R.id.removeButton);
      editB = itemView.findViewById(R.id.editButton);
      addToListB = itemView.findViewById(R.id.toShoppingListButton);
    }

    public void bind() {
      icon.setImageResource(data.get(getAdapterPosition()).icon);
      name.setText(data.get(getAdapterPosition()).name);
      category.setText(data.get(getAdapterPosition()).category);
      number.setText(data.get(getAdapterPosition()).number.toString() + " left in pantry");
      size.setText(data.get(getAdapterPosition()).size);
      expiryDate.setText(data.get(getAdapterPosition()).expiryDate);
      //Set the displayed counter till expiry text and color based on the difference to the current day
      if (getDateDifferenceAsLong(expiryDate.getText().toString()) < daysTillExpiryWarning && getDateDifferenceAsLong(expiryDate.getText().toString()) > 0) {
        expiryText.setText("Expires in " + getDateDifferenceAsString(expiryDate.getText().toString()) + " days");
        expiryText.setTextColor(mResource.getColor(R.color.orange_warning, null));
      } else if (getDateDifferenceAsLong(expiryDate.getText().toString()) < 1) {
        expiryText.setText("Expired " + Math.abs(getDateDifferenceAsLong(expiryDate.getText().toString())) + " days ago");
        expiryText.setTextColor(mResource.getColor(R.color.red_alert, null));
      } else {
        expiryText.setText("Expires in " + getDateDifferenceAsString(expiryDate.getText().toString()) + " days");
        expiryText.setTextColor(mResource.getColor(R.color.blue_item, null));
      }

      if (Integer.parseInt(data.get(getAdapterPosition()).number.toString()) > 0 && Integer.parseInt(data.get(getAdapterPosition()).number.toString()) < 6) {
        number.setTextColor(mResource.getColor(R.color.orange_warning, null));
        expiryText.setVisibility(View.VISIBLE);
      } else if (Integer.parseInt(data.get(getAdapterPosition()).number.toString()) < 1) {
        number.setTextColor(mResource.getColor(R.color.red_alert, null));
        expiryText.setVisibility(View.INVISIBLE);
      } else {
        number.setTextColor(mResource.getColor(R.color.blue_item, null));
        expiryText.setVisibility(View.VISIBLE);
      }

      card.setOnClickListener(
              view -> {
                Log.i("ADAPTER", "onClick: TAPPED A CARD");
                Toast.makeText(
                        view.getContext(),
                        data.get(getAdapterPosition()).name + " is clicked",
                        Toast.LENGTH_SHORT)
                    .show();
              });

      card.setOnLongClickListener(
              view -> {
                Log.i("ADAPTER", "onClick: LONG PRESSED A CARD");
                Toast.makeText(
                        view.getContext(),
                        data.get(getAdapterPosition()).name + " is long clicked",
                        Toast.LENGTH_SHORT)
                    .show();
                return true;
              });

      removeB.setOnClickListener(
              view -> {
                Log.i("ADAPTER", "onClick: REMOVE BUTTON");
                mListener.onDelete(getAdapterPosition());
              });

      editB.setOnClickListener(
              view -> {
                Log.i("ADAPTER", "onClick: EDIT BUTTON");
                mListener.onEdit(getAdapterPosition());
              });

      addToListB.setOnClickListener(
              view -> {
                Log.i("ADAPTER", "onClick: ADD TO SHOPPING LIST BUTTON");
                mListener.onAddToList(getAdapterPosition());
              });
      }
  }

  /**
   * Calculates the amount of days.
   * @param expiryDate the expiry date
   * @return - the amount of days left as a string
   */
  public String getDateDifferenceAsString(String expiryDate) {
    Date calendar = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    try {
      Date date2;
      date2 = dateFormat.parse(expiryDate);
      long difference = 99999;
      if (date2 != null) {
        difference = (date2.getTime() - calendar.getTime());
      }
      long differenceDates = difference / (24 * 60 * 60 * 1000);

      return Long.toString(differenceDates);

    } catch (Exception exception) {
      Log.i("DATE", "Cannot find day difference as string");
      return "null";
    }
  } // getDateDifferenceAsString

  /**
   * Calculates the date difference as a long.
   * @param expiryDate the expiry date
   * @return - amount of days left as a long value
   */
  public long getDateDifferenceAsLong(String expiryDate) {
    Date calendar = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    try {
      Date date2;
      date2 = dateFormat.parse(expiryDate);
      long difference = 99999;
      if (date2 != null) {
        difference = (date2.getTime() - calendar.getTime());
      }

      return difference / (24 * 60 * 60 * 1000);

    } catch (Exception exception) {
      Log.i("DATE", "Cannot find day difference as long");
      return 99999;
    }
  } // getDateDifferenceAsLong

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
