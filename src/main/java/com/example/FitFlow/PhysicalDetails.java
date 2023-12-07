package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class PhysicalDetails extends AppCompatActivity {
    EditText userHeight, userWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_details);
         userHeight = findViewById(R.id.height);
         userWeight = findViewById(R.id.weight);

        final Button continuebtn = findViewById(R.id.continue_btn);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PhysicalDetails.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}