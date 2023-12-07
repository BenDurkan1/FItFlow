package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView tFullName, tEmail, tDoB, tGender, tPhone, tWelcome;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private Button btnContinue;



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
        btnContinue = findViewById(R.id.btnContinue);

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

                    tWelcome.setText("Welcome " +  fullName);
                    tFullName.setText(fullName);
                    tEmail.setText(email);
                    tDoB.setText(doB);
                    tGender.setText(gender);
                    tPhone.setText(mobile);

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

                    Intent intent = new Intent(Profile.this, ActivityDetails.class);
                    startActivity(intent);
            }
        });
    }
}
