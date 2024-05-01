package com.example.FitFlow.Other;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.FitFlow.R;
import com.example.FitFlow.adapters.DateGridAdapter;

import java.time.LocalDate;
import java.util.List;

public class DateViewHolder extends RecyclerView.ViewHolder {
    public TextView dateText;
    public View backgroundView;

    public DateViewHolder(View itemView, DateGridAdapter.DateSelectionListener listener, List<LocalDate> dates) {
        super(itemView);
        dateText = itemView.findViewById(R.id.cellDayText);
        backgroundView = itemView.findViewById(R.id.backgroundView);

        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onDateSelected(position, dates.get(position));
            }
        });
    }

    public void bind(LocalDate date, boolean isActive) {
        dateText.setText(String.valueOf(date.getDayOfMonth()));
        if (isActive) {
            backgroundView.setBackgroundColor(Color.parseColor("#967BB6"));
            dateText.setTextColor(Color.WHITE);
        } else {
            backgroundView.setBackgroundColor(Color.TRANSPARENT);
            dateText.setTextColor(Color.BLACK);
        }
    }
}