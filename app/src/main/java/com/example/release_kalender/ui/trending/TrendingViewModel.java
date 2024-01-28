package com.example.release_kalender.ui.trending;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrendingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TrendingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is trending fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}