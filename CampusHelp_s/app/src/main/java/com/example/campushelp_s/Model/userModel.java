package com.example.campushelp_s.Model;

import android.util.Log;

import androidx.lifecycle.ViewModel;


import java.util.List;

import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class userModel extends ViewModel {

    private User user;
    private String num = "";
    public User getUser(){
        if(user ==  null){
            user = new User();
        }
        return user;
    }

    public String collection_Number() {

        BmobQuery<Task> taskQuery = new BmobQuery<>();//查找收藏的任务

        taskQuery.addWhereRelatedTo("collection", new BmobPointer(user));
        taskQuery.findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if (e == null){
                    num = String.valueOf(list.size());
                    Log.d("number",""+num);
                    Log.d("listsize",""+list.size());
                } else {
                    Log.d("error1",e.toString());
                }
            }
        });
        return num;
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
