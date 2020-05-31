package com.example.campushelp_s.ui.submit_task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubmitTaskViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SubmitTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is SubmitTask fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
