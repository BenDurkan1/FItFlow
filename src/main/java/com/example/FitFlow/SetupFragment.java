package com.example.FitFlow.repository.UI.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.FitFlow.R;
import com.example.FitFlow.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetupFragment extends Fragment {

    private TextView tvWelcome, tvFullName;
    private String fullName, email, doB, gender, mobile, weight;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvFullName = view.findViewById(R.id.tvFullName);
        progressBar = view.findViewById(R.id.progressBar); // Assuming the ProgressBar has the ID 'progressBar' in your layout

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

        }else {
            progressBar.setVisibility(View.VISIBLE);
            showUser(firebaseUser, view);
        }
    }
    private void showUser(FirebaseUser firebaseUser, View view) {

        String userID = firebaseUser.getUid();

        // extract User reference from Database
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullName = readUserDetails.fullName;
                    weight = readUserDetails.weight;

                    tvWelcome.setText("Welcome " +  fullName);


                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
        // Retrieve the fullname and weight from the arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("fullName") && args.containsKey("weight")) {
            fullName = args.getString("fullName", "");
            weight = args.getString("weight", "");

            tvWelcome.setText("Welcome " + fullName);
            tvFullName.setText(fullName);

            // If you need to do something with the weight, you can use it here
            // For example, you can store it in a variable for later use
            // String storedWeight = weight;
        }

        view.findViewById(R.id.tvContinue).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("fullName", fullName);
            bundle.putString("weight", weight);

            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_setupFragment_to_runFragment, bundle);
        });
    }
}