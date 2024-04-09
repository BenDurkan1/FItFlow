package com.example.FitFlow;

import static com.example.FitFlow.Other.Constants.ACTION_SHOW_TRACKING_FRAGMENT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
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
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check if the activity was started with an intent to show the TrackingFragment
        //Even if App is Destroyed
        navigateToTrackingFragmentIfNeeded(getIntent());
      //  setSupportActionBar(findViewById(R.id.toolbar));

        FragmentContainerView navHostFragment = findViewById(R.id.navHostFrag);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = NavHostFragment.findNavController(navHostFragment.getFragment());
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // Handle item reselection event here
            }
        });

        // Initialize DrawerLayout, NavigationView, and ActionBarDrawerToggle
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
                Log.d("Navigation", "onNavigationItemSelected: " + itemId);

                Intent intent = null;
                if (itemId == R.id.home) {
                    // Handle the "Home" menu item click for NavigationView
                    Log.d("Navigation", "Home clicked for NavigationView");
                    intent = new Intent(MainActivity.this, Profile.class);
                } else if (itemId == R.id.runs) {
                    // Handle the "Runs" menu item click for NavigationView
                    Log.d("Navigation", "Runs clicked for NavigationView");
                    intent = new Intent(MainActivity.this, MainActivity.class);
                } else if (itemId == R.id.fitness) {
                    // Handle the "Fitness" menu item click for NavigationView
                    Log.d("Navigation", "Fitness clicked for NavigationView");
                    intent = new Intent(MainActivity.this, Path.class);
                }

                // Close the drawer when an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);

                // Add flags to the intent
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                return true;
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.settingsFragment || itemId == R.id.runFragment || itemId == R.id.statsFragment) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    return navigate(itemId);
                } else {
                    bottomNavigationView.setVisibility(View.GONE);
                    return false;
                }
            }
        });
    }

    private boolean navigate(int itemId) {
        if (itemId == R.id.settingsFragment) {
            // Navigate to the settings fragment
            navController.navigate(R.id.settingsFragment);
            return true;
        } else if (itemId == R.id.runFragment) {
            // Navigate to the run fragment
            navController.navigate(R.id.runFragment);
            return true;
        } else if (itemId == R.id.statsFragment) {
            // Navigate to the stats fragment
            navController.navigate(R.id.statsFragment);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        navigateToTrackingFragmentIfNeeded(intent);
    }
// Navigate to tracking fragment
    private void navigateToTrackingFragmentIfNeeded(Intent intent) {
        if (intent != null && ACTION_SHOW_TRACKING_FRAGMENT.equals(intent.getAction())) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFrag);
            if (navHostFragment != null) {
                navHostFragment.getNavController().navigate(R.id.action_global_trackingFragment);
            }
        }
    }
}
