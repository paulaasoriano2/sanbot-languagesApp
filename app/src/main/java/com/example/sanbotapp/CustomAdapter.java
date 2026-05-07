package com.example.sanbotapp;


import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Pictograma> localDataSet;

    public void addItem(Pictograma item) {
        localDataSet.add(item);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteItem(Pictograma item) {
        if (!localDataSet.isEmpty()) {
            int position = localDataSet.size() - 1; // Elimina el último elemento añadido
            localDataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ArrayList<Pictograma> getDataSet() {
        return localDataSet;
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            imageView = view.findViewById(R.id.imageView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public CustomAdapter(ArrayList<Pictograma> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_agenda, viewGroup, false); // cada elemento de la lista se usará en el layout item_agenda

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Pictograma pictograma = localDataSet.get(position);

        int imageResource = viewHolder.itemView.getContext()
                .getResources()
                .getIdentifier(
                        pictograma.getImagen(),
                        "drawable",
                        viewHolder.itemView.getContext().getPackageName()
                );

        viewHolder.getImageView().setImageResource(imageResource);
        viewHolder.itemView.setOnClickListener(v -> {

            int pos = viewHolder.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                localDataSet.remove(pos);
                notifyItemRemoved(pos);
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}