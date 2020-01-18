package com.example.besocial.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.eventViewHolder>  {



    @NonNull
    @Override
    public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull eventViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class eventViewHolder extends RecyclerView.ViewHolder {
        public eventViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
