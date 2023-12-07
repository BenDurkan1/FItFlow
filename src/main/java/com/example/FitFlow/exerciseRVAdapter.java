package com.example.FitFlow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class exerciseRVAdapter extends RecyclerView.Adapter<exerciseRVAdapter.ExerciseViewHolder> {

    private ArrayList<ExerciseRV> exerciseRVArrayList;
    private Context context;
    private ExerciseClickInterface exerciseClickInterface;

    // Correct constructor declaration
    public exerciseRVAdapter(ArrayList<ExerciseRV> exerciseRVArrayList, Context context, ExerciseClickInterface exerciseClickInterface) {
        this.exerciseRVArrayList = exerciseRVArrayList;
        this.context = context;
        this.exerciseClickInterface = exerciseClickInterface;
    }

    @NonNull
    @Override
    public exerciseRVAdapter.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exerciseitem, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (holder != null) {
            holder.exerciseTV.setText(exerciseRVArrayList.get(position).getExerciseName());
            holder.exerciseLAV.setAnimationFromUrl(exerciseRVArrayList.get(position).getImgURL());
            String time = String.valueOf(exerciseRVArrayList.get(position).getTime()) + "MIN";
            holder.timeTV.setText(time);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exerciseClickInterface.onExerciseClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return exerciseRVArrayList.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseTV, timeTV;
        private LottieAnimationView exerciseLAV;


        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseTV = itemView.findViewById(R.id.tvExerciseName);
            timeTV = itemView.findViewById(R.id.idExerciseTime);
            exerciseLAV = itemView.findViewById(R.id.idExerciseLAV);
        }
    }

    public interface ExerciseClickInterface {
        void onExerciseClick(int position);
    }
}
