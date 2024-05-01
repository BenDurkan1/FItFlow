package com.example.FitFlow;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FoodActivityDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        String detail = getIntent().getStringExtra("story");

        TextView tvDetail = findViewById(R.id.tvDetail);
        tvDetail.setText(detail);
    }
}
