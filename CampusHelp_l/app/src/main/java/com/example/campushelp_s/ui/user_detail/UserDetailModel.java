package com.example.campushelp_s.ui.user_detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserDetailModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UserDetailModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is taskDetail fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}