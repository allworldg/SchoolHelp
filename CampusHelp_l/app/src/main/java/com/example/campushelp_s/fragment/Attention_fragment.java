package com.example.campushelp_s.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campushelp_s.R;
import com.example.campushelp_s.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Attention_fragment extends Fragment {
    private View view;
    private RecyclerView rvlist;
    private User user;
    private com.example.campushelp_s.Model.userModel userModel;
    private ImageView iv_return;
    private List<User> userlist = new ArrayList<>();
    private View mRoot;
    private Activity mActivity;

    Attention_fragment(User user){
        this.user=user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.personal_attetion_fg,container,false);
        rvlist = view.findViewById(R.id.follow_list);
        iv_return = view.findViewById(R.id.gz_return);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        userModel = new ViewModelProvider(getActivity()).get(com.example.campushelp_s.Model.userModel.class);
        //user = userModel.getUser();
        mActivity=getActivity();
        mRoot=view;
        FrameLayout replace = mActivity.findViewById(R.id.personal_replace);
        replace.setVisibility(View.VISIBLE);
        initData();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FrameLayout replace = mActivity.findViewById(R.id.personal_replace);
        replace.setVisibility(View.INVISIBLE);
    }

    public void initData() {
        Log.d("init","init");
        BmobQuery<User> userQuery = new BmobQuery<User>();//关注的人 follow列
        userQuery.addWhereRelatedTo("follow", new BmobPointer(user));
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    userlist = list;
                    Message message = UserListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    UserListHandler.sendMessage(message);

                } else {
                    Log.d("finderoor",e.toString());
                    Toast.makeText(getActivity(),"查找失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Handler UserListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    Log.d("userlist",""+list.size());
                    userlist = list;
                    UserAdapter userAdapter=new UserAdapter(user,getContext(),
                            R.layout.personal_attetion_item,userlist);
                    LinearLayoutManager llm =new LinearLayoutManager(getContext());
                    getContext();
                    rvlist=getView().findViewById(R.id.follow_list);
                    rvlist.setLayoutManager(llm);
                    rvlist.setAdapter(userAdapter);
                    break;
            }
        }
    };
}
