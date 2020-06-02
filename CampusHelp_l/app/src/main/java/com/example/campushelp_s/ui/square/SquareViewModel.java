package com.example.campushelp_s.ui.square;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SquareViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SquareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is square fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}