package com.example.FitFlow.repository.UI.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.FitFlow.StepCount;
import com.example.FitFlow.repository.MainRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StepCountViewModel extends ViewModel {
    private final MainRepository mainRepository;

    @Inject
    public StepCountViewModel(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public LiveData<List<StepCount>> getStepCounts() {
        return mainRepository.fetchStepCounts();
    }

    public LiveData<Integer> getTotalSteps() {
        return mainRepository.getTotalSteps();
    }

    public LiveData<List<StepCount>>  getStepCountsSortedByDate() {
        return mainRepository.getStepCountsSortedByDate();
    }

    public LiveData<Double> getAverageSteps() {
        return mainRepository.getAverageSteps();
    }


    public void addSteps(int steps) {
        mainRepository.updateStepCount(steps);
    }
    public LiveData<Integer> getDaysCount() {
        return mainRepository.getDaysCount();
    }



}