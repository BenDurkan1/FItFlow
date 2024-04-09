package com.example.FitFlow.repository.UI.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.FitFlow.Run;
import com.example.FitFlow.repository.MainRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final MainRepository mainRepository;

    @Inject
    public StatisticsViewModel(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public LiveData<Long> getTotalTimeRun() {
        return mainRepository.getTotalTimeInMillis();
    }

    public LiveData<Integer> getTotalDistance() {
        return mainRepository.getTotalDistance();
    }

    public LiveData<Integer> getTotalCaloriesBurned() {
        return mainRepository.getTotalCaloriesBurned();
    }

    public LiveData<Double> getTotalAvgSpeed() {
        return mainRepository.getTotalAvgSpeed();
    }

    public LiveData<List<Run>> getRunsSortedByDate() {
        return mainRepository.getAllRunsSortedByDate();
    }
}
