package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExerciseActivity extends AppCompatActivity implements exerciseRVAdapter.ExerciseClickInterface {

    private RecyclerView exerciseRV;
    private ArrayList<ExerciseRV> exerciseRVArrayList;
    private exerciseRVAdapter exerciseRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        exerciseRV= findViewById(R.id.rvExercise);
        exerciseRVArrayList = new ArrayList<>();
        exerciseRVAdapter = new exerciseRVAdapter(exerciseRVArrayList,this, this::onExerciseClick);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        exerciseRV.setLayoutManager(manager);
        exerciseRV.setAdapter(exerciseRVAdapter);
        addData();
    }

    private void addData() {
exerciseRVArrayList.add(new ExerciseRV("Side Plank",getResources().getString(R.string.side_plank),"https://lottie.host/07cc884e-9e24-484a-b90c-13e0e2a5c201/pz9uA90EWB.lottie", 20, 10));
        exerciseRVArrayList.add(new ExerciseRV("Push ups",getResources().getString(R.string.push_ups),"https://lottie.host/90b337fe-3a2e-4867-9dba-e346277727d6/7FyA7QFARn.lottie", 20, 10));
        exerciseRVArrayList.add(new ExerciseRV("Lunges",getResources().getString(R.string.Lunges),"https://lottie.host/cb95d6fe-2a5b-49b4-8db8-b55c983192e2/psYilwKdKb.lottie", 30, 10));
        exerciseRVArrayList.add(new ExerciseRV("High Stepping",getResources().getString(R.string.stepping),"https://lottie.host/e54e9513-e739-49d7-a8f0-06b3e1ae017d/2r05NBL7le.lottie", 40, 10));
        exerciseRVArrayList.add(new ExerciseRV("Ab crunches",getResources().getString(R.string.abs_crunching),"https://lottie.host/0ac53295-37ed-4096-8288-e5c8db658199/xlirFbcME0.lottie", 20, 20));
        exerciseRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExerciseClick(int position) {

Intent intent = new Intent(ExerciseActivity.this, exerciseDetails.class);
intent.putExtra("exerciseName", exerciseRVArrayList.get(position).getExerciseName());
        intent.putExtra("imgUrl", exerciseRVArrayList.get(position).getImgURL());
        intent.putExtra("time", exerciseRVArrayList.get(position).getTime());
        intent.putExtra("calories", exerciseRVArrayList.get(position).getCalories());
        intent.putExtra("description", exerciseRVArrayList.get(position).getExerciseDescription());

        startActivity(intent);


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}