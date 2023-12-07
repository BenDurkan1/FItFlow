package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        final EditText mobilityIssues = findViewById(R.id.mob_issues);
        final EditText preferredActivities = findViewById(R.id.preferredAct);

        final Button continuebtn = findViewById(R.id.continue_btn);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityDetails.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}