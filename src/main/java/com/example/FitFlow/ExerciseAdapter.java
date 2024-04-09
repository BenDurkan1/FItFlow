package com.example.FitFlow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.ExerciseModel;
import com.example.FitFlow.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<ExerciseModel> exercises;
    private boolean isSelectionMode = false;
    private Set<Integer> selectedItems = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(ExerciseModel exerciseModel);
    }

    private OnItemClickListener listener;

    // Constructor
    public ExerciseAdapter(List<ExerciseModel> exercises, boolean isSelectionMode) {
        this.exercises = exercises;
        this.isSelectionMode = isSelectionMode;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exerciseitem, parent, false);
        return new ExerciseViewHolder(view, isSelectionMode);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position), position);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }
    public void updateData(List<ExerciseModel> newExercises) {
        this.exercises.clear();
        this.exercises.addAll(newExercises);
        notifyDataSetChanged(); // Notify any registered observers that the data set has changed.
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        CheckBox exerciseCheckbox;
        LottieAnimationView exerciseAnimationView; // Added LottieAnimationView

        ExerciseViewHolder(View itemView, boolean isSelectionMode) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseName);
            exerciseCheckbox = itemView.findViewById(R.id.exerciseCheckbox);
            exerciseAnimationView = itemView.findViewById(R.id.exerciseAnimation); // Initialize LottieAnimationView

            exerciseCheckbox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        }

        void bind(final ExerciseModel exerciseModel, final int position) {
            exerciseNameTextView.setText(exerciseModel.getName());

            // Set up Lottie Animation
            if (exerciseModel.getLottieFile() != null && !exerciseModel.getLottieFile().isEmpty()) {
                exerciseAnimationView.setAnimation(exerciseModel.getLottieFile());
                exerciseAnimationView.playAnimation();
                exerciseAnimationView.setVisibility(View.VISIBLE);
            } else {
                // Handle case where there is no animation file provided
                exerciseAnimationView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (!isSelectionMode && listener != null) {
                    listener.onItemClick(exerciseModel);
                } else if (isSelectionMode) {
                    exerciseCheckbox.setChecked(!exerciseCheckbox.isChecked());
                }
            });

            exerciseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedItems.add(position);
                } else {
                    selectedItems.remove(position);
                }
            });

            exerciseCheckbox.setChecked(selectedItems.contains(position));
        }
    }
