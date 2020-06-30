package com.example.campushelp_s.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import bean.User;


public class UserViewModel extends ViewModel {
    private User user;
    public User getUser(){
        if(user ==  null){
            user = new User();
        }
        return user;
    }
    //get login user
    public void setUser(User loginUser){
        user = loginUser;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        user = null;
    }
}
