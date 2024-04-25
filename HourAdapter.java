package com.example.FitFlow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HourAdapter extends ArrayAdapter<LocalTime> {
    private Context context;
    private List<LocalTime> hourTimes;
    private Map<LocalTime, List<ExerciseModel>> hourExercisesMap = new HashMap<>();

    public HourAdapter(Context context, List<LocalTime> hourTimes) {
        super(context, 0, hourTimes);
        this.context = context;
        this.hourTimes = hourTimes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_recycle, parent, false);
        }

        LocalTime time = getItem(position);
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));

        List<ExerciseModel> exercises = hourExercisesMap.get(time);
        TextView[] events = new TextView[]{
                convertView.findViewById(R.id.event1),
                convertView.findViewById(R.id.event2),
                convertView.findViewById(R.id.event3),
                convertView.findViewById(R.id.event4),
                convertView.findViewById(R.id.event5),
                convertView.findViewById(R.id.event6),
                convertView.findViewById(R.id.event7),
                convertView.findViewById(R.id.event8)
        };

        for (TextView event : events) {
            event.setVisibility(View.GONE);
        }

        if (exercises != null) {
            for (int i = 0; i < exercises.size() && i < events.length; i++) {
                ExerciseModel exercise = exercises.get(i);
                TextView event = events[i];
                event.setText(exercise.getName());
                event.setVisibility(View.VISIBLE);
                event.setOnClickListener(v -> launchFitnessActivity(exercise));
            }
        }

        return convertView;
    }

    private void launchFitnessActivity(ExerciseModel exercise) {
        Intent intent = new Intent(context, Fitness2.class);
        intent.putExtra("ExerciseModel", exercise);
        context.startActivity(intent);
    }

    public void updateHourExercises(LocalTime hour, List<ExerciseModel> exercises) {
        hourExercisesMap.put(hour, exercises);
        notifyDataSetChanged();
    }
}
