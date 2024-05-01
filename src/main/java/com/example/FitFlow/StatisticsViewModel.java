package com.example.FitFlow.repository.UI.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
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

    public LiveData<Boolean> isUserAuthenticated() {
        return mainRepository.isUserAuthenticated();
    }

    public LiveData<Long> getTotalTimeRun() {
        return Transformations.switchMap(isUserAuthenticated(), isAuthenticated -> {
            if (Boolean.TRUE.equals(isAuthenticated)) {
                return mainRepository.getTotalTimeInMillis();
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }

    public LiveData<Integer> getTotalDistance() {
        return Transformations.switchMap(isUserAuthenticated(), isAuthenticated -> {
            if (Boolean.TRUE.equals(isAuthenticated)) {
                return mainRepository.getTotalDistance();
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }


    public LiveData<Integer> getTotalCaloriesBurned() {
        return Transformations.switchMap(isUserAuthenticated(), isAuthenticated -> {
            if (Boolean.TRUE.equals(isAuthenticated)) {
                return mainRepository.getTotalCaloriesBurned();
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }


    public LiveData<Double> getTotalAvgSpeed() {
        return Transformations.switchMap(isUserAuthenticated(), isAuthenticated -> {
            if (Boolean.TRUE.equals(isAuthenticated)) {
                return mainRepository.getTotalAvgSpeed();
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }

    public LiveData<List<Run>> getRunsSortedByDate() {
        return Transformations.switchMap(isUserAuthenticated(), isAuthenticated -> {
            if (Boolean.TRUE.equals(isAuthenticated)) {
                return mainRepository.getAllRunsSortedByDate();
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }
}
