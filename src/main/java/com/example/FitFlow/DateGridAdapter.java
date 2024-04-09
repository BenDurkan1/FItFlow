package com.example.FitFlow.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.Other.CalendarUtils;
import com.example.FitFlow.Other.DateViewHolder;
import com.example.FitFlow.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class DateGridAdapter extends RecyclerView.Adapter<DateViewHolder> {
    private ArrayList<LocalDate> dates;
    private DateSelectionListener dateSelectionListener;

    public DateGridAdapter(ArrayList<LocalDate> dates, DateSelectionListener dateSelectionListener) {
        this.dates = dates;
        this.dateSelectionListener = dateSelectionListener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_recycle_view, parent, false); // Ensure this layout is correct
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // Adjust height based on the number of dates (to distinguish between month and week views)
        if (dates.size() > 15) { // Assuming month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        } else { // Assuming week view
            layoutParams.height = parent.getHeight();
        }
        view.setLayoutParams(layoutParams);

        return new DateViewHolder(view, dateSelectionListener, dates);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        LocalDate date = dates.get(position);
        if (date != null) {
            holder.dateText.setText(String.valueOf(date.getDayOfMonth()));
            // Highlight the selected date
            if (date.equals(CalendarUtils.selectedDate)) {
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                // Reset background color for non-selected dates
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            holder.dateText.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public interface DateSelectionListener {
        void onDateSelected(int position, LocalDate date);
    }
}
