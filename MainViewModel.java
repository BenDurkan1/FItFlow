package com.example.FitFlow.repository.UI.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.FitFlow.Other.SortType;
import com.example.FitFlow.Run;
import com.example.FitFlow.repository.MainRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final MainRepository mainRepository;
    private final MediatorLiveData<List<Run>> runs = new MediatorLiveData<>();
    private SortType sortType = SortType.DATE;

    @Inject
    public MainViewModel(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
        initializeMediatorLiveData();
    }

    private void initializeMediatorLiveData() {
        LiveData<List<Run>> allRunsLiveData = mainRepository.getAllRuns();
        runs.addSource(allRunsLiveData, runsList -> {
            sortRunsList(runsList);
        });
    }

    public LiveData<List<Run>> getRuns() {
        return runs;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
        switch (sortType) {
            case DATE:
                runs.addSource(mainRepository.getAllRunsSortedByDate(), runsList -> sortRunsList(runsList));
                break;
            case RUNNING_TIME:
                runs.addSource(mainRepository.getAllRunsSortedByTimeInMillis(), runsList -> sortRunsList(runsList));
                break;
            case DISTANCE:
                runs.addSource(mainRepository.getAllRunsSortedByDistance(), runsList -> sortRunsList(runsList));
                break;
            case AVG_SPEED:
                runs.addSource(mainRepository.getAllRunsSortedByAvgSpeed(), runsList -> sortRunsList(runsList));
                break;
            case CALORIES_BURNED:
                runs.addSource(mainRepository.getAllRunsSortedByCaloriesBurned(), runsList -> sortRunsList(runsList));
                break;
        }
    }

    private void sortRunsList(List<Run> runsList) {

        runs.setValue(runsList);

    }
}
