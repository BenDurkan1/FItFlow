package com.example.FitFlow;

public interface RunDAO {


    void saveRunToFirebase(Run run, DataCallback<Boolean> callback);

    interface DataCallback<T> {
        void onDataLoaded(T data);

        void onError(Exception e);
    }
}
