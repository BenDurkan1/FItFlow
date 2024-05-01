package com.example.FitFlow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.FitFlow.ExerciseModel;
import com.example.FitFlow.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<ExerciseModel> exercises;
    public boolean isSelectionMode = false;
    private Set<String> selectedExerciseIds = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(ExerciseModel exerciseModel);
    }

    private OnItemClickListener listener;

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
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }
    public boolean hasSelectedItems() {
        return !selectedExerciseIds.isEmpty();
    }

    public void updateData(List<ExerciseModel> newExercises) {
        this.exercises.clear();
        this.exercises.addAll(newExercises);
        notifyDataSetChanged();
    }

    public Set<String> getSelectedExerciseIds() {
        return selectedExerciseIds;
    }

    public void toggleSelection(String id) {
        if (selectedExerciseIds.contains(id)) {
            selectedExerciseIds.remove(id);
        } else {
            selectedExerciseIds.add(id);
        }
        notifyDataSetChanged();
    }


    public void clearSelections() {
        if (!selectedExerciseIds.isEmpty()) {
            selectedExerciseIds.clear();
            notifyDataSetChanged();
        }
    }
    public void setSelectionMode(boolean isSelectionMode) {
        this.isSelectionMode = isSelectionMode;
        notifyDataSetChanged();
    }
    public Set<String> getSelectedItems() {
        return new HashSet<>(selectedExerciseIds);
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        CheckBox exerciseCheckbox;
        LottieAnimationView exerciseAnimationView;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseName);
            exerciseCheckbox = itemView.findViewById(R.id.exerciseCheckbox);
            exerciseAnimationView = itemView.findViewById(R.id.exerciseAnimation);

                itemView.setOnClickListener(v -> {
                    ExerciseModel exercise = exercises.get(getAdapterPosition());
                    if (isSelectionMode) {
                        toggleSelection(exercise.getId());
                    } else {
                        if (listener != null) {
                            listener.onItemClick(exercise);
                        }
                    }
                });
            }


        void bind(final ExerciseModel exerciseModel) {
            exerciseNameTextView.setText(exerciseModel.getName());
            setupAnimation(exerciseModel);

            exerciseCheckbox.setChecked(selectedExerciseIds.contains(exerciseModel.getId()));
            exerciseCheckbox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (isSelectionMode) {
                    toggleSelection(exerciseModel.getId());
                } else {
                    if (listener != null) {
                        listener.onItemClick(exerciseModel);
                    }
                }
            });

            exerciseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedExerciseIds.add(exerciseModel.getId());
                } else {
                    selectedExerciseIds.remove(exerciseModel.getId());
                }
            });
        }

        private void setupAnimation(ExerciseModel exerciseModel) {
            if (exerciseModel.getLottieFile() != null && !exerciseModel.getLottieFile().isEmpty()) {
                exerciseAnimationView.setAnimation(exerciseModel.getLottieFile());
                exerciseAnimationView.playAnimation();
                exerciseAnimationView.setVisibility(View.VISIBLE);
            } else {
                exerciseAnimationView.setVisibility(View.GONE);
            }
        }
    }
}
