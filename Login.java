package com.example.FitFlow;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText LoginEmail, LoginPwd;
    private TextView registerRed;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // getSupportActionBar().setTitle("Login");
        LoginEmail = findViewById(R.id.login_email);
        LoginPwd = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.progress_bar);
        registerRed = findViewById(R.id.regRedirect);
        authProfile = FirebaseAuth.getInstance();


        registerRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });


        Button buttonLogin = findViewById(R.id.login_btn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = LoginEmail.getText().toString();
                String textPwd = LoginPwd.getText().toString();


                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    LoginEmail.setError("Email is required");
                    LoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    LoginEmail.setError("Valid Email is required");
                    LoginEmail.requestFocus();
                }
                if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    LoginPwd.setError("Password is required");
                    LoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);


                }
            }
        });

    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    if (firebaseUser.isEmailVerified()) {
                     //   Toast.makeText(Login.this, "User has logged in successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, Profile.class);
                        startActivity(intent);
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        LoginEmail.setError("User does not exist");
                        LoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        LoginEmail.setError("Invalid credentials.Kindly, check and re-enter");
                        LoginEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Verification email sent");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(Login.this, "You can now Login", Toast.LENGTH_SHORT).show();
    }
}
