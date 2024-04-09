package com.example.FitFlow.repository.UI.fragments;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FitFlow.Other.SortType;
import com.example.FitFlow.Other.TrackingUtil;
import com.example.FitFlow.R;
import com.example.FitFlow.Run;
import com.example.FitFlow.adapters.RunAdapter;
import com.example.FitFlow.repository.UI.ViewModels.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class RunFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "RunFragment";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 0;
    private MainViewModel viewModel;
    private RunAdapter runAdapter;
    private Spinner spFilter;

    public RunFragment() {
        super();
    }
     SortType sortType = SortType.DATE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(v -> NavHostFragment.findNavController(RunFragment.this)
                .navigate(R.id.action_runFragment_to_trackingFragment));

        if (TrackingUtil.hasLocationPermissions(requireContext())) {
            setupRecyclerView(view);
            spFilter = view.findViewById(R.id.spFilter);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.filter_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFilter.setAdapter(adapter);

            SortType sortType = viewModel.getSortType();
            switch (sortType) {
                case DATE:
                    spFilter.setSelection(0);
                    break;
                case RUNNING_TIME:
                    spFilter.setSelection(1);
                    break;
                case DISTANCE:
                    spFilter.setSelection(2);
                    break;
                case AVG_SPEED:
                    spFilter.setSelection(3);
                    break;
                case CALORIES_BURNED:
                    spFilter.setSelection(4);
                    break;
            }

            spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    switch (pos) {
                        case 0:
                            viewModel.setSortType(SortType.DATE);
                            break;
                        case 1:
                            viewModel.setSortType(SortType.RUNNING_TIME);
                            break;
                        case 2:
                            viewModel.setSortType(SortType.DISTANCE);
                            break;
                        case 3:
                            viewModel.setSortType(SortType.AVG_SPEED);
                            break;
                        case 4:
                            viewModel.setSortType(SortType.CALORIES_BURNED);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(getContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                }
            });

            observeRunsSortedByDate();
        } else {
            requestPermissions();
        }
    }

    private void setupRecyclerView(View view) {
        runAdapter = new RunAdapter();
        RecyclerView rvRuns = view.findViewById(R.id.rvRuns);
        rvRuns.setAdapter(runAdapter);
        rvRuns.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void observeRunsSortedByDate() {
        viewModel.getRuns().observe(getViewLifecycleOwner(), new Observer<List<Run>>() {
            @Override
            public void onChanged(List<Run> runs) {
                Log.d(TAG, "Number of runs: " + runs.size());
                runAdapter.submitList(runs);
            }
        });
    }

    private void requestPermissions() {
        String[] locationPermissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app.",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    locationPermissions
            );
        } else {
            EasyPermissions.requestPermissions(
                    (Fragment) this,
                    "You need to accept location permissions to use this app.",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    locationPermissions
            );
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        setupRecyclerView(getView());
        observeRunsSortedByDate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
