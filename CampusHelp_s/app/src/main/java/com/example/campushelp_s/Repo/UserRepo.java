package com.example.campushelp_s.Repo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.campushelp_s.MainActivity;

import java.util.List;

import bean.User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class UserRepo {
    private static UserRepo instance;//单例模式，获得一个自己的类的实例
    private MutableLiveData<User> mUser;

    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    private MutableLiveData<User> getmUser(String currentUserId) {
        BmobQuery<User> userBmobQuery = new BmobQuery<>();//通过currentUserId来查找当前用户
        userBmobQuery.getObject(currentUserId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Message message = currentUserHandler.obtainMessage();
                    message.what = 0;
                    message.obj =
                }
            }
        });

    }

    private Handler currentUserHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    User currentUser = list.get(0);
            }
        }
    };

}
