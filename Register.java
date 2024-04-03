package com.example.FitFlow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

private EditText email, name, dob, username, password, retypepassword,phone, weight;

private TextView loginRed;
private ProgressBar progressBar;
    private RadioGroup registerGender;
    private DatePickerDialog picker;

    private RadioButton registeredGenderSelected;
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Set your layout XML file here

        Toast.makeText(Register.this, "You can Register now", Toast.LENGTH_SHORT).show();
        progressBar = findViewById(R.id.progressBar);
         name = findViewById(R.id.register);
        email = findViewById(R.id.reg_email);
         dob = findViewById(R.id.DOB);
         loginRed = findViewById(R.id.loginRedirect);
       username = findViewById(R.id.reg_username);
        password = findViewById(R.id.reg_password);
         retypepassword = findViewById(R.id.re_password);
         phone = findViewById(R.id.mobile);
         weight = findViewById(R.id.weight);
        registerGender = findViewById(R.id.radio_group_register_gender);
        registerGender.clearCheck();
        //calender
        dob.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       final Calendar calendar = Calendar.getInstance();
                                       int day = calendar.get(Calendar.DAY_OF_MONTH);
                                       int month = calendar.get(Calendar.MONTH);
                                       int year = calendar.get(Calendar.YEAR);

                                       // Date Picker
                            picker = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                      dob.setText(dayOfMonth + "/" + (month+ 1) + "/" + year );
                                }
                            }, year,month,day);
                            picker.show();
                                   }
                               });

        loginRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });



        Button registerButton = findViewById(R.id.reg_btn);
         TextView loginRedirectTextView = findViewById(R.id.loginRedirect);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = registerGender.getCheckedRadioButtonId();
                registeredGenderSelected = findViewById(selectedGenderId);

                // get data from editText into Strings
                String namet = name.getText().toString();
                String phonet = phone.getText().toString();
                String emailt = email.getText().toString();
                String dobt = dob.getText().toString();
                String usernamet = username.getText().toString();
                String passwordt = password.getText().toString();
                String repasswordt = retypepassword.getText().toString();
                String weightt = weight.getText().toString();
                String textGender; // wait for verification

                //Validate Mobile using matcher and pattern
                // Define a regular expression pattern for Irish mobile numbers
                String regex = "(?:\\+353|0)[1-9][0-9]{8}";

                // Compile the regular expression
                // Pattern pattern = Pattern.compile(regex);
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(regex);
                // Match the input phone number with the pattern
                mobileMatcher = mobilePattern.matcher(phonet);




                //check if user fill all the fields before sending data to firebase
                if (namet.isEmpty() || phonet.isEmpty() || usernamet.isEmpty() || passwordt.isEmpty() || repasswordt.isEmpty() || weightt.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                }
                //check passwords match
                else if (!passwordt.equals(repasswordt)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();

                } else if (passwordt.length() < 6) {
                    Toast.makeText(Register.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    password.setError("Password to weak");
                    password.requestFocus();
                    // Clear the entered passwords
                    password.clearComposingText();
                    retypepassword.clearComposingText();
                } else if (phonet.length() != 10) {
                    Toast.makeText(Register.this, "Please enter mobile again", Toast.LENGTH_SHORT).show();
                    phone.setError("Mobile No. should be 10 digits");
                    phone.requestFocus();
                }else if (!mobileMatcher.find()){
                    Toast.makeText(Register.this, "Please re-enter mobile again", Toast.LENGTH_SHORT).show();
                    phone.setError("Mobile No. is not valid");
                    phone.requestFocus();

                } else {
                    textGender = registeredGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    UserClass(namet, phonet, emailt, passwordt, dobt, usernamet, textGender, weightt);
                }
            }
        });


    }

            //  Register user using the credentials given
            private void UserClass(String namet, String phonet, String emailt, String passwordt, String dobt, String usernamet, String textGender, String weightt) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                // Create User Profile
                auth.createUserWithEmailAndPassword(emailt, passwordt).addOnCompleteListener(Register.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();

                                    // Enter User Data into this FireBase Realtime Database
                                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails( namet,dobt, textGender,phonet, weightt);

                                    //Extracting User reference from Database for "Registered Users"
                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                //send verification email
                                              firebaseUser.sendEmailVerification();

                                               Toast.makeText(Register.this, "User registered successfully," +
                                                        " Please verify your email", Toast.LENGTH_LONG).show();


                                              //   //open user profile after successful verification
                                                Intent intent = new Intent(Register.this,Profile.class);
                                                // Prevent User from returning back to RegisterActivity on pressing back button after registration
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();// close Activity


                                            } else {
                                                // Handle the failure
                                                Log.e(TAG, "Data writing to Realtime Database failed: " + task.getException().getMessage());
                                                Toast.makeText(Register.this, "User registration failed, Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    });



                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        password.setError("Your Password is to weak");
                                        password.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        password.setError("Your Password is to weak");
                                        password.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            }
                        });
            }
}


