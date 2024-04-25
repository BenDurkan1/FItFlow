package com.example.FitFlow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Food extends AppCompatActivity {

    RecyclerView rvTips;
    List<TipModel> tipList;
    TipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);

      //  rvTips = findViewById(R.id.rvTips);
        tipList = new ArrayList<>();

        rvTips.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseTips = FirebaseDatabase.getInstance().getReference("tips");

        databaseTips.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tipList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TipModel tip = postSnapshot.getValue(TipModel.class);
                    Log.d("Food", "Loaded Tip: " + tip.getTitle());
                    tipList.add(tip);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Food.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new TipAdapter(this, tipList, new TipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TipModel tip) {
                Intent intent = new Intent(Food.this, FoodActivityDetails.class);
                intent.putExtra("story", tip.getDetail());
                startActivity(intent);
            }
        });

        rvTips.setAdapter(adapter);
    }
}