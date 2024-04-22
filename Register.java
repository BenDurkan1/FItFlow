package com.example.FitFlow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1001;
    private static final int PERMISSION_CODE = 1002;

    private EditText email, name, doB, username, password, retypepassword, phone, weight;
    private ImageView imageViewProfile;
    private ProgressBar progressBar;
    private RadioGroup registerGender;
    private DatePickerDialog datePickerDialog;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setListeners();
    }

    private void initializeViews() {
        imageViewProfile = findViewById(R.id.imageViewProfile);
        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.register);
        email = findViewById(R.id.reg_email);
        doB = findViewById(R.id.DOB);
        username = findViewById(R.id.reg_username);
        password = findViewById(R.id.reg_password);
        retypepassword = findViewById(R.id.re_password);
        phone = findViewById(R.id.mobile);
        weight = findViewById(R.id.weight);
        registerGender = findViewById(R.id.radio_group_register_gender);
        registerGender.clearCheck();
    }

    private void setListeners() {
        doB.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.loginRedirect).setOnClickListener(v -> startActivity(new Intent(Register.this, Login.class)));

        imageViewProfile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        });

        findViewById(R.id.reg_btn).setOnClickListener(v -> attemptRegistration());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                doB.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void attemptRegistration() {
        String namet = name.getText().toString().trim();
        String phonet = phone.getText().toString().trim();
        String emailt = email.getText().toString().trim();
        String dobt = doB.getText().toString().trim();
        String usernamet = username.getText().toString().trim();
        String passwordt = password.getText().toString().trim();
        String repasswordt = retypepassword.getText().toString().trim();
        String weightt = weight.getText().toString().trim();
        int selectedId = registerGender.getCheckedRadioButtonId();
        RadioButton registeredGenderSelected = findViewById(selectedId);
        String gender = registeredGenderSelected.getText().toString();

        if (!validateInputs(namet, emailt, passwordt, repasswordt, phonet, weightt)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerUser(namet, emailt, passwordt, dobt, usernamet, gender, phonet, weightt);
    }

    private boolean validateInputs(String name, String email, String password, String retypePassword, String phone, String weight) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty() || phone.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(retypePassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(String name, String email, String password, String doB, String username, String gender, String phone, String weight) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    uploadImageToFirebase(userId, name, phone, email, doB, username, gender, weight);
                }
            } else {
                if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                    Toast.makeText(this, "Password too weak", Toast.LENGTH_SHORT).show();
                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void uploadImageToFirebase(String userId, String name, String phone, String email, String dob, String username, String gender, String weight) {
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("users/" + userId + "/profile.jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveUserInformation(userId, name, phone, email, dob, username, gender, weight, imageUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
        } else {
            saveUserInformation(userId, name, phone, email, dob, username, gender, weight, null);
        }
    }

    private void saveUserInformation(String userId, String name, String phone, String email, String dob, String username, String gender, String weight, String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);
        ReadWriteUserDetails userDetails = new ReadWriteUserDetails(name, dob, gender, phone, weight, imageUrl);
        userRef.setValue(userDetails).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Profile.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save user information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }
}
