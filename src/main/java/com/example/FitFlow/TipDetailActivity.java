package com.example.FitFlow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TipDetailActivity extends AppCompatActivity {
    private LinearLayout layout;
    private TextView detailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tip_item);

        layout = findViewById(R.id.layoutButtons);
        detailView = findViewById(R.id.tvDetail);

        String category = getIntent().getStringExtra("ExerciseName");
        generateCategoryButtons(category);
    }

    private void generateCategoryButtons(String category) {
        String[] subCategories;

        if (category.equals("Nutrition & Healthy Eating")) {
            subCategories = new String[]{"Carbs: Essential for energy, focus on whole grains.", "Proteins: Vital for muscle repair, include varied sources.", "Fats: Important for brain health, choose healthy sources."};
        } else if (category.equals("Injury Prevention and Recovery")) {
            subCategories = new String[]{"Warm-Ups: Increase blood flow and prepare the body.", "Cool-Downs: Reduce heart rate and prevent stiffness.", "Rehab Exercises: Strengthen muscles and aid recovery."};
        } else if (category.equals("Motivation and Goal Setting")) {
            subCategories = new String[]{"Setting SMART Goals: Ensure goals are achievable and time-bound.", "Keeping Motivated: Stay positive and celebrate small victories."};
        } else if (category.equals("Fitness Myths")) {
            subCategories = new String[]{"Myth Busters: Debunk common fitness myths and misconceptions."};
        } else if (category.equals("Exercise Form and Technique")) {
            subCategories = new String[]{"Proper Form Tips: Avoid injury and improve efficacy.", "Common Mistakes: Learn to spot and correct them."};
        } else if (category.equals("Mindfulness & Stress Management")) {
            subCategories = new String[]{"Meditation: Enhance well-being and reduce stress.", "Breathing Techniques: Improve breathing effectiveness."};
        } else if (category.equals("Fitness foyr Specific Goals")) {
            subCategories = new String[]{"Weight Loss: Effective strategies for sustainable health.", "Strength Building: Focus on building muscle strength.", "Endurance: Improve stamina with cardiovascular activities."};
        } else {
            subCategories = new String[]{};
        }

        layout.removeAllViews();
        for (String subCategory : subCategories) {
            Button button = new Button(this);
            button.setText(subCategory.split(":")[0]);
            button.setOnClickListener(v -> showDetails(subCategory));
            layout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }


    private void showDetails(String subCategory) {
        detailView.setText(subCategory.split(":")[1]);
    }
}
