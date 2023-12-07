package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    // Sample code, modify based on your needs
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("526071068982-0vaereeavjh7kai3djm1ljj0sk5dbl3o.apps.googleusercontent.com")
            .requestEmail()
            .build();

    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    // Trigger sign-in
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        final EditText Height = findViewById(R.id.height);
        final EditText Weight = findViewById(R.id.weight);
        final EditText dob = findViewById(R.id.DOB);
        final Button continuebtn= findViewById(R.id.continue_btn);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Trigger sign-in
        //        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
         //       startActivityForResult(signInIntent, Profile.class);

                Intent intent = new Intent(MainActivity.this, ActivityDetails.class);
                startActivity(intent);
            }
        });
    }

    }