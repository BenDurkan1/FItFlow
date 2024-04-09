package com.example.FitFlow;

import java.util.List;

public interface RunDAO {

    void insertRun(Run run);

    void deleteRun(Run run);

    void getAllRunsSortedByDate(DataCallback<List<Run>> callback);

    void getAllRunsSortedByTimeInMillis(DataCallback<List<Run>> callback);

    void getAllRunsSortedByCaloriesBurned(DataCallback<List<Run>> callback);

    void getAllRunsSortedByAvgSpeed(DataCallback<List<Run>> callback);

    void getAllRunsSortedByDistance(DataCallback<List<Run>> callback);

    void getTotalTimeInMillis(DataCallback<Long> callback);

    void getTotalCaloriesBurned(DataCallback<Integer> callback);

    void getTotalDistance(DataCallback<Integer> callback);

    void getTotalAvgSpeed(DataCallback<Double> callback);

    void saveRunToFirebase(Run run, DataCallback<Boolean> callback);

    interface DataCallback<T> {
        void onDataLoaded(T data);

        void onError(Exception e);
    }
}
