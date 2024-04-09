package com.example.FitFlow;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FoodActivityDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details); // Ensure this matches your layout file name

        // Get the detail passed through the intent
        String detail = getIntent().getStringExtra("story");

        // Find the TextView by ID and set the detail text
        TextView tvDetail = findViewById(R.id.tvDetail);
        tvDetail.setText(detail);
    }
}
