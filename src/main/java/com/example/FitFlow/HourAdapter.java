package com.example.FitFlow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.FitFlow.Other.HourEvent;
import com.example.FitFlow.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent> {
    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents) {
        super(context, 0, hourEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourEvent hourEvent = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_recycle, parent, false);
        }

        displayHourDetails(convertView, hourEvent);
        return convertView;
    }

    private void displayHourDetails(View itemView, HourEvent hourEvent) {
        TextView timeLabel = itemView.findViewById(R.id.timeTV);
        // Convert LocalTime to String for display
        String timeText = hourEvent.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeLabel.setText(timeText);

        TextView[] eventViews = new TextView[] {
                itemView.findViewById(R.id.event1),
                itemView.findViewById(R.id.event2),
                itemView.findViewById(R.id.event3)
        };

        // Reset visibility to handle recycled views
        for (TextView eventView : eventViews) {
            eventView.setVisibility(View.GONE);
        }

        // Display events up to the number of TextViews available
        for (int i = 0; i < hourEvent.getEvents().size() && i < eventViews.length; i++) {
            TextView eventView = eventViews[i];
            eventView.setText(hourEvent.getEvents().get(i).getName());
            eventView.setVisibility(View.VISIBLE);
        }

        // If there are more events than TextViews, indicate the number of additional events
        if (hourEvent.getEvents().size() > eventViews.length) {
            int additionalEventsCount = hourEvent.getEvents().size() - eventViews.length;
            TextView lastEventView = eventViews[eventViews.length - 1];
            lastEventView.setText(getContext().getString(R.string.more_events, additionalEventsCount));
            lastEventView.setVisibility(View.VISIBLE);
        }
    }
}
