package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;

public class Path extends AppCompatActivity {

    private LinearLayout exercise,  stepCounter;
    private LottieAnimationView exerciseLAV, counterLAV;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item))
        {
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        exercise = findViewById(R.id.liftExercise);
        stepCounter = findViewById(R.id.cardioExercise);
        exerciseLAV = findViewById(R.id.idLift);
        counterLAV = findViewById(R.id.idCardio);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    startActivity(new Intent(Path.this, Profile.class));
                } else if (itemId == R.id.runs) {
                    startActivity(new Intent(Path.this, MainActivity.class));
                } else if (itemId == R.id.fitness) {
                    startActivity(new Intent(Path.this, Path.class));
                }

                // Close the drawer when an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);

                // Return true to indicate that the item has been handled
                return true;
            }

        });



        exerciseLAV.setAnimation("lottie/overheadbarbell.json");
        counterLAV.setAnimation("lottie/Runner.json");

        exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Path.this, FitnessActivity.class); // Make sure FitnessActivity.class is specified here
                startActivity(intent);
            }
        });

        stepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Path.this,CounterActivity.class);
                startActivity(intent);
            }
        });
    }
}