package com.example.FitFlow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.Other.DateViewHolder;
import com.example.FitFlow.R;

import java.time.LocalDate;
import java.util.List;

public class DateGridAdapter extends RecyclerView.Adapter<DateViewHolder> {
    private List<LocalDate> dates;
    private List<LocalDate> activeDays;
    private DateSelectionListener listener;

    public DateGridAdapter(List<LocalDate> dates, List<LocalDate> activeDays, DateSelectionListener listener) {
        this.dates = dates;
        this.activeDays = activeDays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_recycle_view, parent, false);

        int totalWeeks = (dates.size() + 7 - 1) / 7;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = parent.getHeight() / totalWeeks;
        view.setLayoutParams(layoutParams);

        return new DateViewHolder(view, listener, dates);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        LocalDate date = dates.get(position);
        boolean isActive = activeDays.contains(date);
        holder.bind(date, isActive);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public interface DateSelectionListener {
        void onDateSelected(int position, LocalDate date);
    }
}
