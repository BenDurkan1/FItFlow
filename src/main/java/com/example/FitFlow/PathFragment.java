package com.example.FitFlow.repository.UI.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.FitFlow.CounterActivity;
import com.example.FitFlow.FitnessActivity;
import com.example.FitFlow.R;

public class PathFragment extends Fragment {

    private View exercise, stepCounter;
    private LottieAnimationView exerciseLAV, counterLAV;
    private static final int COUNTER_ACTIVITY_REQUEST_CODE = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_path, container, false);

        exercise = view.findViewById(R.id.liftExercise);
        stepCounter = view.findViewById(R.id.cardioExercise);
        exerciseLAV = view.findViewById(R.id.idLift);
        counterLAV = view.findViewById(R.id.idCardio);

        exerciseLAV.setAnimationFromUrl("https://lottie.host/6a94a05a-be15-4b89-81ee-c9f0f4a67dd7/0aHcu7T8Qo.lottie");
        counterLAV.setAnimationFromUrl("https://lottie.host/44309984-f81c-4ecc-8f2e-aa1d8a3c4201/mrkGLrA8FD.lottie");

        exercise.setOnClickListener(v -> {
            if(getActivity() != null){
                getActivity().startActivity(new Intent(getActivity(), FitnessActivity.class));
            }
        });

        stepCounter.setOnClickListener(v -> {
            if(getActivity() != null){
                startActivityForResult(new Intent(getActivity(), CounterActivity.class), COUNTER_ACTIVITY_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COUNTER_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            int stepCount = data.getIntExtra("stepCount", 0);
        }
    }
}
