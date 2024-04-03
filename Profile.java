package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView tFullName, tEmail, tDoB, tGender, tPhone, tWelcome, tWeight;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile, weight;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private Button btnContinue;

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
        setContentView(R.layout.activity_profile);
        //    getSupportActionBar().setTitle("Home");
        tFullName = findViewById(R.id.textViewFullName);
        tEmail = findViewById(R.id.textViewEmail);
        tDoB = findViewById(R.id.textViewDOB);
        tGender = findViewById(R.id.textViewGender);
        tPhone = findViewById(R.id.textViewMobile);
        progressBar = findViewById(R.id.progress_bar);
        tWelcome = findViewById(R.id.textViewWelcome);
        tWeight = findViewById(R.id.textViewWeight);
        btnContinue = findViewById(R.id.btnContinue);
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
                    startActivity(new Intent(Profile.this, Profile.class));
                } else if (itemId == R.id.runs) {
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    intent.putExtra("fullName", fullName);  // Pass the fullname to the next activity
                    intent.putExtra("weight", weight);      // Pass the weight to the next activity
                    startActivity(intent);
                    finish();

                } else if (itemId == R.id.fitness) {
                    startActivity(new Intent(Profile.this, Path.class));
                }


                // Close the drawer when an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);

                // Return true to indicate that the item has been handled
                return true;
            }

        });


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();

        }else {
            progressBar.setVisibility(View.VISIBLE);
            showUser(firebaseUser);
        }
    }

    private void showUser(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

        // extract User reference from Database
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullName = readUserDetails.fullName;
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.phone;
                    weight = readUserDetails.weight;

                    tWelcome.setText("Welcome " +  fullName);
                    tFullName.setText(fullName);
                    tEmail.setText(email);
                    tDoB.setText(doB);
                    tGender.setText(gender);
                    tPhone.setText(mobile);
                    tWeight.setText(weight);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Profile.this, MainActivity.class);
                intent.putExtra("fullName", fullName);  // Pass the fullname to the next activity
                intent.putExtra("weight", weight);
                startActivity(intent);
            }
        });
    }
}
