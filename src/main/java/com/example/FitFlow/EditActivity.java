package com.example.FitFlow;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private EditText editTextFullName, editTextEmail, editTextMobile, editTextWeight;
    private TextView textViewDOB, textViewGender;
    private ImageView imageViewProfile;
    private Button buttonSaveChanges, buttonChangeImage;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        textViewDOB = findViewById(R.id.textViewDOB);
        textViewGender = findViewById(R.id.textViewGender);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextWeight = findViewById(R.id.editTextWeight);
        buttonSaveChanges = findViewById(R.id.btnContinue);
        buttonChangeImage = findViewById(R.id.btnChangeImage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");

        loadUserData();
        loadImage();


        buttonChangeImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        });

        textViewDOB.setOnClickListener(v -> showDatePickerDialog());

        textViewGender.setOnClickListener(v -> showGenderSelectionDialog());

        buttonSaveChanges.setOnClickListener(v -> saveUserData());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("users/" + user.getUid() + "/profile.jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        DatabaseReference userRef = databaseReference.child(user.getUid());
                        userRef.child("profileImageUrl").setValue(imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    imageViewProfile.setImageURI(imageUri); // Set image in ImageView after successful upload
                                })
                                .addOnFailureListener(e -> Log.e("EditActivity", "Failed to save image URL to DB", e));
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        Log.e("EditActivity", "Failed to upload image to storage", e);
                    });
        }
    }

    private void showDatePickerDialog() {
        String dobText = textViewDOB.getText().toString();
        String[] parts = dobText.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int year = Integer.parseInt(parts[2]);

        datePickerDialog = new DatePickerDialog(EditActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> textViewDOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }


    private void showGenderSelectionDialog() {
        final String[] genders = {"Male", "Female", "Other"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(genders, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        textViewGender.setText(genders[which]);
                    }
                }).show();
    }

    private void loadUserData() {
        editTextFullName.setText(getIntent().getStringExtra("fullName"));
        editTextEmail.setText(getIntent().getStringExtra("email"));
        textViewDOB.setText(getIntent().getStringExtra("dob"));
        textViewGender.setText(getIntent().getStringExtra("gender"));
        editTextMobile.setText(getIntent().getStringExtra("mobile"));
        editTextWeight.setText(getIntent().getStringExtra("weight"));
    }
    private void loadImage() {
        String imageUrl = getIntent().getStringExtra("profileImageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_baseline_person_2_24)
                    .error(R.drawable.ic_baseline_person_2_24)
                    .circleCrop()
                    .into(imageViewProfile);
        }
    }

    private void saveUserData() {
        String fullName = editTextFullName.getText().toString();
        String email = editTextEmail.getText().toString();
        String dob = textViewDOB.getText().toString();
        String gender = textViewGender.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String weight = editTextWeight.getText().toString();

        if (user != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", fullName);
            updates.put("email", email);
            updates.put("doB", dob);
            updates.put("gender", gender);
            updates.put("phone", mobile);
            updates.put("weight", weight);

            databaseReference.child(user.getUid()).updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            navigateBackToProfile();
                        } else {
                            Toast.makeText(EditActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Not authenticated. Please log in and try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateBackToProfile() {
        Intent intent = new Intent(EditActivity.this, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
