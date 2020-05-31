package com.example.campushelp_s.ui.task_detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TaskDetailModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TaskDetailModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is taskDetail fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}