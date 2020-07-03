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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.campushelp_s.ViewModel.UserViewModel;
import com.example.campushelp_s.R;
import com.example.campushelp_s.TaskAdapter;

import java.util.List;

import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Collection_Fragment extends Fragment {
    View view;
    private List<Task> taskList;
    private RecyclerView rvList;
    private ImageView iv_return;
    private Activity mActivity;
    UserViewModel UserViewModel;//存储登陆用户的对象
    User user;//当前登陆的用户对象

    private View mRoot;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.personal_favorites_fg, container, false);
        rvList = view.findViewById(R.id.list_favorites);//将recyclerview进行控件绑定
        iv_return = view.findViewById(R.id.scj_return);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        UserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);//获取到共享的userModel
        user = UserViewModel.getUser();//获取到当前登陆用户对象

        mRoot=view;
        mActivity=getActivity();
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


    /**
     * 初始化收藏任务的数据
     */
    public void initData() {
        BmobQuery<Task> taskQuery = new BmobQuery<Task>();//查找收藏的任务
        taskQuery.addWhereRelatedTo("collection", new BmobPointer(user));
        taskQuery.findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if (e == null) {
                    taskList = list;
                    Message message = taskListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    taskListHandler.sendMessage(message);

                } else {
                    Log.d("finderoor",e.toString());
                    Toast.makeText(getActivity(),"查找失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Handler taskListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<Task> list = (List<Task>) msg.obj;
                    taskList = list;

                    TaskAdapter taskAdapter=new TaskAdapter(getContext(),
                            R.layout.list_item_task,taskList);
                    LinearLayoutManager llm =new LinearLayoutManager(getContext());
                    getContext();
                    rvList=getView().findViewById(R.id.list_favorites);
                    rvList.setLayoutManager(llm);
                    taskAdapter.setOnItemClickListener(new TaskAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int postion,Task task) {
                            toastShow(task.getTitle());
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            //String textItem =  ((TextView) view).getText().toString();
                            TaskDetail_for_personal taskDetail_for_personal = new TaskDetail_for_personal(task);
                            Bundle bundle = new Bundle();
                            bundle.putString("currentUserObjectId", user.getObjectId());//原来是注释掉的
                            taskDetail_for_personal.setArguments(bundle);
                            transaction
                                    .addToBackStack(null)  //将当前fragment加入到返回栈中
                                    .add(R.id.collection_replace,taskDetail_for_personal)
                                    .show(taskDetail_for_personal)
                                    .commit();
                        }
                    });

                    rvList.setAdapter(taskAdapter);
                    rvList=(RecyclerView) getView().findViewById(R.id.rv_task);
                    break;
            }
        }
    };
    public void toastShow (String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
