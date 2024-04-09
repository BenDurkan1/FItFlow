package com.example.FitFlow.repository.UI.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.FitFlow.Run;

import java.util.List;

// Create a ViewModel for managing runs data
public class RunModel extends ViewModel {
    private final MutableLiveData<List<Run>> runsLiveData = new MutableLiveData<>();

    // Method to update runs LiveData
    public void updateRuns(List<Run> runs) {
        runsLiveData.setValue(runs);
    }

    // Getter for runs LiveData
    public LiveData<List<Run>> getRunsLiveData() {
        return runsLiveData;
    }
}
