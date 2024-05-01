package com.example.FitFlow.repository.UI.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.FitFlow.Other.Constants.ACTION_PAUSE_SERVICE;
import static com.example.FitFlow.Other.Constants.ACTION_START_OR_RESUME_SERVICE;
import static com.example.FitFlow.Other.Constants.ACTION_STOP_SERVICE;
import static com.example.FitFlow.Other.Constants.MAP_ZOOM;
import static com.example.FitFlow.Other.Constants.POLYLINE_COLOR;
import static com.example.FitFlow.Other.Constants.POLYLINE_WIDTH;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;

import com.example.FitFlow.FirebaseRunDAO;
import com.example.FitFlow.Other.TrackingUtil;
import com.example.FitFlow.R;
import com.example.FitFlow.Run;
import com.example.FitFlow.RunDAO;
import com.example.FitFlow.Services.TrackingService;
import com.example.FitFlow.repository.UI.ViewModels.MainViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackingFragment extends Fragment implements OnMapReadyCallback {
    private String CANCEL_TRACKING_DIALOG_TAG = "CANCEL DIALOG";

    private MainViewModel viewModel;
    private MapView mapView;
    private GoogleMap map;
    private Button btnToggleRun;
    private Button btnFinishRun;
    private DatabaseReference weightReference;
    private FragmentManager parentFragmentManager;
    private FirebaseRunDAO firebaseRunDAO;
    private TextView tvTimer;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<List<LatLng>> pathPoints = new ArrayList<>();
    private boolean isTracking;
    private long curTimeInMillis = 0L;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public TrackingFragment() {
        super(R.layout.fragment_tracking);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No user signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference userRunTableRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(user.getUid()).child("RunningTable");
        weightReference = userRunTableRef.child("weight");
        firebaseRunDAO = new FirebaseRunDAO(userRunTableRef);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                .child(user.getUid());

        weightReference = userRef.child("weight");
     //   fetchWeightData();


        btnToggleRun = view.findViewById(R.id.btnToggleRun);
        btnFinishRun = view.findViewById(R.id.btnFinishRun);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        tvTimer = view.findViewById(R.id.tvTimer);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        btnToggleRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRun();
            }
        });

        if (savedInstanceState != null) {
            if (parentFragmentManager != null) {
                CancelRun cancelTrackingDialog = (CancelRun) parentFragmentManager.findFragmentByTag(
                        CANCEL_TRACKING_DIALOG_TAG);
                if (cancelTrackingDialog != null) {
                    cancelTrackingDialog.setYesListener(new Runnable() {
                        @Override
                        public void run() {
                            stopRun();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Parent Fragment Manager is null");
            }
        }

        btnFinishRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeFinishedTrack();
                endRunAndSaveToDb();
            }
        });

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        addAllPolylines();

        subscribeToObservers();
    }
    private void fetchWeightData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userDetailsRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid());

            userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String weightAsString = snapshot.child("weight").getValue(String.class);
                    if (weightAsString != null && !weightAsString.isEmpty()) {
                        String numericWeightString = weightAsString.replaceAll("[^\\d.]", "");
                        try {
                            double weight = Double.parseDouble(numericWeightString);
                            Log.d(TAG, "Weight retrieved and parsed successfully: " + weight);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing weight data: " + e.getMessage());
                            Toast.makeText(getContext(), "Error parsing weight data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Weight data is missing or empty");
                        Toast.makeText(getContext(), "Weight data is missing or empty", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to read weight: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to read weight", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentFragmentManager = requireActivity().getSupportFragmentManager();
    }

    private void subscribeToObservers() {
        TrackingService.isTracking.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTracking) {
                updateTracking(isTracking);
            }
        });

        TrackingService.pathPoints.observe(getViewLifecycleOwner(), new Observer<List<List<LatLng>>>() {
            @Override
            public void onChanged(List<List<LatLng>> updatedPathPoints) {
                pathPoints = updatedPathPoints;
                addLatestPolyline();
                moveToUser();
            }
        });

        TrackingService.timeRunInMillis.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long timeInMillis) {
                curTimeInMillis = timeInMillis;
                String formattedTime = TrackingUtil.getFormattedStopWatchTime(curTimeInMillis,true);
                tvTimer.setText(formattedTime);
            }
        });
    }

    private void toggleRun() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isTracking) {
                sendCommandToService(ACTION_PAUSE_SERVICE);
            } else {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE);
            }
        } else {
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tracking_menu, menu);
        this.menu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (curTimeInMillis > 0L) {
            this.menu.getItem(0).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miCancelTracking) {
            showCancelTrackingDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showCancelTrackingDialog() {
        CancelRun dialog = new CancelRun();
        dialog.setYesListener(new Runnable() {
            @Override
            public void run() {
                stopRun();
                tvTimer.setText("00:00:00:00");
            }
        });
        dialog.show(parentFragmentManager, "Cancel Dialog");
    }

    private void stopRun() {
        tvTimer.setText("00:00:00:00");
        sendCommandToService(ACTION_STOP_SERVICE);
        FragmentKt.findNavController(TrackingFragment.this).navigate(R.id.action_trackingFragment_to_runFragment);
    }

    private void updateTracking(boolean isTracking) {
        this.isTracking = isTracking;

        if (!isTracking &&  curTimeInMillis > 0L) {
            btnToggleRun.setText("Start");
            btnFinishRun.setVisibility(View.VISIBLE);
        } else if (isTracking){
            btnToggleRun.setText("Stop");

            if (menu != null) {
                MenuItem menuItem = menu.getItem(0);
                if (menuItem != null) {
                    menuItem.setVisible(true);
                }
            }

            btnFinishRun.setVisibility(View.GONE);
        }
    }

    private void moveToUser() {
        if (pathPoints != null && !pathPoints.isEmpty() && pathPoints.get(pathPoints.size() - 1) != null && !pathPoints.get(pathPoints.size() - 1).isEmpty()) {
            LatLng lastPoint = pathPoints.get(pathPoints.size() - 1).get(pathPoints.get(pathPoints.size() - 1).size() - 1);

            if (map != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, MAP_ZOOM));
            }
        }
    }

    private void seeFinishedTrack() {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        // Iterates through a list of polylines,
        for (List<LatLng> polyline : pathPoints) {
            for (LatLng pos : polyline) {
                bounds.include(pos);
            }
        }

        if (map != null) {
            map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            mapView.getWidth(),
                            mapView.getHeight(),
                            (int) (mapView.getHeight() * 0.05f)
                    )
            );
        }
    }

    private void endRunAndSaveToDb() {
        if (map != null) {
            map.snapshot(bitmap -> {
                int finalDistanceInMeters = 0;
                for (List<LatLng> polyline : pathPoints) {
                    finalDistanceInMeters += (int) TrackingUtil.calculatePolylineLength(polyline);
                }
                final int finalDistanceForInnerClass = finalDistanceInMeters;

                if (weightReference == null) {
                    Log.e(TAG, "weightReference is null, cannot add listener");
                    Snackbar.make(requireActivity().findViewById(R.id.rootView),
                            "Error: weight data is unavailable.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                weightReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            try {
                                String weightAsString = snapshot.getValue(String.class);
                                String numericWeightString = weightAsString.replaceAll("[^\\d.]", "");
                                float weight = Float.parseFloat(numericWeightString);

                                float avgSpeed = Math.round((finalDistanceForInnerClass / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f;
                                long dateTimestamp = Calendar.getInstance().getTimeInMillis();
                                int caloriesBurned = (int) ((finalDistanceForInnerClass / 1000f) * weight);
                                Run run = new Run(bitmap, dateTimestamp, avgSpeed, finalDistanceForInnerClass, curTimeInMillis, caloriesBurned);

                                firebaseRunDAO.saveRunToFirebase(run, new RunDAO.DataCallback<Boolean>() {
                                    @Override
                                    public void onDataLoaded(Boolean data) {
                                        Snackbar.make(requireActivity().findViewById(R.id.rootView),
                                                "Run saved successfully", Snackbar.LENGTH_LONG).show();
                                        navigateBack();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Snackbar.make(requireActivity().findViewById(R.id.rootView),
                                                "Failed to save run", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            } catch (NumberFormatException e) {
                                Snackbar.make(requireActivity().findViewById(R.id.rootView),
                                        "Error parsing weight data", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(requireActivity().findViewById(R.id.rootView),
                                    "Weight data is missing", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(requireActivity().findViewById(R.id.rootView),
                                "Database error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            });
        } else {
            Snackbar.make(requireActivity().findViewById(R.id.rootView),
                    "Map snapshot is not available", Snackbar.LENGTH_LONG).show();
        }
    }

    private void navigateBack() {
        FragmentKt.findNavController(TrackingFragment.this).navigate(R.id.action_trackingFragment_to_runFragment);
    }




    private void addAllPolylines() {
        for (List<LatLng> polyline : pathPoints) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .addAll(polyline);

            if (map != null) {
                map.addPolyline(polylineOptions);
            }
        }
    }


    private void addLatestPolyline() {
        if (pathPoints != null && !pathPoints.isEmpty() && pathPoints.get(pathPoints.size() - 1).size() > 1) {
            LatLng preLastLatLng = pathPoints.get(pathPoints.size() - 1).get(pathPoints.get(pathPoints.size() - 1).size() - 2);
            LatLng lastLatLng = pathPoints.get(pathPoints.size() - 1).get(pathPoints.get(pathPoints.size() - 1).size() - 1);

            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(preLastLatLng)
                    .add(lastLatLng);

            if (map != null) {
                map.addPolyline(polylineOptions);
            }
        }
    }

    private void sendCommandToService(String action) {
        Intent intent = new Intent(requireContext(), TrackingService.class);
        intent.setAction(action);
        requireContext().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        readPointsUpdateMap();
    }

    private void readPointsUpdateMap() {
        SharedPreferences prefs = getActivity().getSharedPreferences("TrackingServicePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("pathPoints", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<List<LatLng>>>() {}.getType();
            List<List<LatLng>> pathPoints = gson.fromJson(json, type);
            if (map != null) {
                map.clear();
                updateMapWithPathPoints(pathPoints);
            }
        }
    }

    private void updateMapWithPathPoints(List<List<LatLng>> pathPoints) {
        map.clear();
        for (List<LatLng> segment : pathPoints) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(segment)
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH);
            map.addPolyline(polylineOptions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            enableMyLocation();
        } else {
            Toast.makeText(requireContext(), "Location permission is required to show your current location on the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            updateMapWithLastLocation();
        } else {
            Toast.makeText(requireContext(), "Location permission is required to show your current location on the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapWithLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            });
        } else {
            Toast.makeText(requireContext(), "Location permission is required to show your current location on the map.", Toast.LENGTH_SHORT).show();
        }
    }
}
