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
import android.graphics.Bitmap;
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
 //   private float weight = 90f;
    private DatabaseReference weightReference;

   private  FragmentManager parentFragmentManager;




    private FirebaseRunDAO firebaseRunDAO;

    private TextView tvTimer;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<List<LatLng>> pathPoints = new ArrayList<>();
    private boolean isTracking;
    private long curTimeInMillis = 0L;

    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // only called if this is set to true
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);

    }
    public TrackingFragment() {
        super(R.layout.fragment_tracking);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        weightReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId).child("weight");

        btnToggleRun = view.findViewById(R.id.btnToggleRun);
        btnFinishRun = view.findViewById(R.id.btnFinishRun);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        tvTimer= view.findViewById(R.id.tvTimer);
        firebaseRunDAO = new FirebaseRunDAO(FirebaseDatabase.getInstance().getReference("running_table"));


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());



        btnToggleRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRun();
            }
        });

        if (savedInstanceState != null) {
            // Check if the parentFragmentManager is not null
            if (parentFragmentManager != null) {
                // Find the CancelTrackingDialog fragment by tag
                CancelTrackingDialog cancelTrackingDialog = (CancelTrackingDialog) parentFragmentManager.findFragmentByTag(
                        CANCEL_TRACKING_DIALOG_TAG);
                // Check if the cancelTrackingDialog is not null
                if (cancelTrackingDialog != null) {
                    // Set the yesListener for the cancelTrackingDialog
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
                seeWholeTrack();
                endRunAndSaveToDb();
            }
        });

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // Call addAllPolylines here after initializing your view components
        addAllPolylines();

        // Now, subscribe to observers
        subscribeToObservers();
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
                // Update your local variable accordingly
                pathPoints = updatedPathPoints;
                addLatestPolyline();
                moveCameraToUser();
            }
        });

        TrackingService.timeRunInMillis.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long timeInMillis) {
                curTimeInMillis = timeInMillis;
                // Include the second parameter to indicate whether milliseconds should be included
                String formattedTime = TrackingUtil.getFormattedStopWatchTime(curTimeInMillis,true);
                tvTimer.setText(formattedTime);
            }
        });
    }
    // State whether tracking or not
    private void toggleRun() {
        if (isTracking) {
// Get the first item from the menu
            MenuItem menuItem = menu.getItem(0);
            if (menuItem != null) {
                // Set the visibility of the item to true
                menuItem.setVisible(true);
            }            sendCommandToService(ACTION_PAUSE_SERVICE);
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE);
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tracking_menu, menu);
        this.menu = menu;
    }
// change visibility of menuItem
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
        // Create an instance of CancelTrackingDialog
        CancelTrackingDialog dialog = new CancelTrackingDialog();
        // Set the yesListener for the dialog
        dialog.setYesListener(new Runnable() {
            @Override
            public void run() {
                stopRun();
                tvTimer.setText("00:00:00:00");

            }
        });
        // Show the dialog using the parentFragmentManager
        dialog.show(parentFragmentManager, "Cancel Dialog");
    }

    private void stopRun() {
        tvTimer.setText("00:00:00:00");
        sendCommandToService(ACTION_STOP_SERVICE);
        // Provide the instance of the TrackingFragment to findNavController
        FragmentKt.findNavController(TrackingFragment.this).navigate(R.id.action_trackingFragment_to_runFragment);
    }



    // Reflect status of button in terns of whether it has stopped or started again
    private void updateTracking(boolean isTracking) {
        this.isTracking = isTracking;

        if (!isTracking &&  curTimeInMillis > 0L) {
            btnToggleRun.setText("Start");
            btnFinishRun.setVisibility(View.VISIBLE);
        } else if (isTracking){
            btnToggleRun.setText("Stop");

            // Check if menu is not null before accessing its items
            if (menu != null) {
                MenuItem menuItem = menu.getItem(0);
                if (menuItem != null) {
                    menuItem.setVisible(true);
                }
            }

            btnFinishRun.setVisibility(View.GONE);
        }
    }

    //Moves the camera to current user location
    private void moveCameraToUser() {
        if (pathPoints != null && !pathPoints.isEmpty() && pathPoints.get(pathPoints.size() - 1) != null && !pathPoints.get(pathPoints.size() - 1).isEmpty()) {
            LatLng lastPoint = pathPoints.get(pathPoints.size() - 1).get(pathPoints.get(pathPoints.size() - 1).size() - 1);

            if (map != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, MAP_ZOOM));
            }
        }
    }


    private void seeWholeTrack() {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
//Iterates through a list of polylines,
        for (List<LatLng> polyline : pathPoints) {
            //Extends the bounding box to include the specified LatLng position.
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
            map.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    int finalDistanceInMeters = 0;
                    for (List<LatLng> polyline : pathPoints) {
                        finalDistanceInMeters += (int) TrackingUtil.calculatePolylineLength(polyline);
                    }

                    // Fetch the weight dynamically from Firebase
                    int finalDistanceInMeters1 = finalDistanceInMeters;
                    weightReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Retrieve the weight as a String
                                String weightAsString = snapshot.getValue(String.class);

                                // Remove non-numeric characters from the weight string
                                String numericWeightString = weightAsString.replaceAll("[^\\d.]", "");

                                try {
                                    // Attempt to convert the cleaned string to a float
                                    float weight = Float.parseFloat(numericWeightString);

                                    // Rest of your code remains unchanged
                                    float avgSpeed = Math.round((finalDistanceInMeters1 / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f;
                                    long dateTimestamp = Calendar.getInstance().getTimeInMillis();
                                    int caloriesBurned = (int) ((finalDistanceInMeters1 / 1000f) * weight);

                                    Run run = new Run(bitmap, dateTimestamp, avgSpeed, finalDistanceInMeters1, curTimeInMillis, caloriesBurned);

                                    // Save the run using FirebaseRunDAO
                                    firebaseRunDAO.saveRunToFirebase(run, new RunDAO.DataCallback<Boolean>() {
                                        @Override
                                        public void onDataLoaded(Boolean data) {
                                            // Handle success if needed
                                            Snackbar.make(
                                                    requireActivity().findViewById(R.id.rootView),
                                                    "Run saved successfully",
                                                    Snackbar.LENGTH_LONG
                                            ).show();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            // Handle error if needed
                                            Snackbar.make(
                                                    requireActivity().findViewById(R.id.rootView),
                                                    "Failed to save run",
                                                    Snackbar.LENGTH_LONG
                                            ).show();
                                        }
                                    });

                                    // Stop the run after saving to Firebase
                                    stopRun();
                                } catch (NumberFormatException e) {
                                    // Handle the case where the weight data cannot be parsed
                                    Snackbar.make(
                                            requireActivity().findViewById(R.id.rootView),
                                            "Error parsing weight data",
                                            Snackbar.LENGTH_LONG
                                    ).show();
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error if needed
                        }
                    });
                }
            });
        }
    }

    // loop through path points create new object and all points together
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
// add new polyline to maps based on latest recorded location in "PathPoints" connects
// second-to-last to last location
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
        readPersistedPathPointsAndUpdateMap();
    }

    private void readPersistedPathPointsAndUpdateMap() {
        SharedPreferences prefs = getActivity().getSharedPreferences("TrackingServicePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("pathPoints", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<List<LatLng>>>() {}.getType();
            List<List<LatLng>> pathPoints = gson.fromJson(json, type);
            // Clear the map and update with latest polylines
            if (map != null) {
                map.clear(); // Clear before adding updated polylines
                updateMapWithPathPoints(pathPoints);
            }
        }
    }
    private void updateMapWithPathPoints(List<List<LatLng>> pathPoints) {
        map.clear(); // Clear existing polylines or markers
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

    // ... (Previous code)

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Check if the app has location permissions
        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            // Enable the user's location on the map
            enableMyLocation();
        } else {
            // Request location permissions
            Toast.makeText(requireContext(), "Location permission is required to show your current location on the map.", Toast.LENGTH_SHORT).show();


        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Check if permission is granted
            map.setMyLocationEnabled(true);

            // Zoom to the user's location if available
            updateMapWithLastLocation();
        } else {
            // Request location permissions
            Toast.makeText(requireContext(), "Location permission is required to show your current location on the map.", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateMapWithLastLocation() {
        // Check location permission before calling getLastLocation
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            });
        } else {
            // Handle the case where location permission is not granted
            Toast.makeText(requireContext(), "Location permission is required to show your current location on the map.", Toast.LENGTH_SHORT).show();

        }
    }
}
// ... (Remaining code)
