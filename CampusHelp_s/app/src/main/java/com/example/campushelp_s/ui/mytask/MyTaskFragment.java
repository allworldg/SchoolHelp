package com.example.campushelp_s.ui.mytask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavArgument;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campushelp_s.R;
import com.example.campushelp_s.TaskAdapter;
import com.example.campushelp_s.ui.task_detail.TaskDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyTaskFragment extends Fragment {

    private List<Task> taskList = new ArrayList<>();
    private RecyclerView rvList;
    private MyTaskViewModel homeViewModel;
    private User currentUser;
    private String currentUserObjectId;

    private Activity mActivity;
    private Context mContext;
    private View mRoot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(MyTaskViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mytask, container, false);
        //final TextView textView = root.findViewById(R.id.text_mytask);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        Map<String, NavArgument> map = NavHostFragment.findNavController(this).getGraph().getArguments();
        NavArgument navArgument = map.get("currentUserObjectId");
        assert navArgument != null;
        currentUserObjectId = (String) navArgument.getDefaultValue();

        mActivity=getActivity();
        mContext=getContext();
        mRoot=root;

        initUserData();
        iniPublished();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUserData(){
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId",currentUserObjectId);
        userQuery.order("-createdAt").findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e!=null){
                    Log.d("pullTaskList", "error");
                }else{
                    Message message = taskListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    userHandler.sendMessage(message);
                }
            }
        });

    }

//    private void initTaskData_published(){
//        BmobQuery<Task> taskQuery = new BmobQuery<>();
//        taskQuery.order("-createdAt").findObjects(new FindListener<Task>() {
//            @Override
//            public void done(List<Task> list, BmobException e) {
//                if(e!=null){
//                    Log.d("pullTaskList", "error");
//                }else{
//                    Message message = taskListHandler.obtainMessage();
//                    message.what = 0;
//                    //以消息为载体
//                    message.obj = list;//这里的list就是查询出list
//                    //向handler发送消息
//                    taskListHandler.sendMessage(message);
//                }
//            }
//        });
//
//    }

    private void initTaskData_accepted(){
        BmobQuery<Task> taskQuery = new BmobQuery<>();
        taskQuery.order("-createdAt").findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if(e!=null){
                    Log.d("pullTaskList", "error");
                }else{
                    Message message = taskListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    taskListHandler.sendMessage(message);
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
                    rvList=getView().findViewById(R.id.rv_task);
                    rvList.setLayoutManager(llm);
                    taskAdapter.setOnItemClickListener(new TaskAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int postion,Task task) {
                            toastShow(task.getTitle());

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            //String textItem =  ((TextView) view).getText().toString();
                            TaskDetailFragment taskDetailFragment = new TaskDetailFragment(task);
                            Bundle bundle = new Bundle();
                            //bundle.putString("taskTitle", textItem);
                            taskDetailFragment.setArguments(bundle);
                            transaction
                                    .addToBackStack(null)  //将当前fragment加入到返回栈中
                                    .add(R.id.task_detail_replace,taskDetailFragment)
                                    .show(taskDetailFragment)
                                    .commit();
                        }
                    });
                    rvList.setAdapter(taskAdapter);
                    rvList=(RecyclerView) getView().findViewById(R.id.rv_task);
                    break;
            }
        }
    };


    private Handler userHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    currentUser = list.get(0);
                    Button bt_published = getActivity().findViewById(R.id.bt_published);
                    Button bt_accepted = getActivity().findViewById(R.id.bt_accepted);

                    iniPublished();
                    bt_published.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickPublished();
                        }
                    });

                    bt_accepted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickAccepted();
                        }
                    });
            }
        }
    };

    public void toastShow (String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void clickPublished(){
        Button bt_published = getActivity().findViewById(R.id.bt_published);
        Button bt_accepted = getActivity().findViewById(R.id.bt_accepted);
        bt_published.setTextColor(getResources().getColor(R.color.colorPrimary));
        bt_accepted.setTextSize(16);
        bt_accepted.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        bt_published.setTextSize(20);
        BmobQuery<Task> taskQuery = new BmobQuery<>();
        taskQuery.addWhereEqualTo("publisher",currentUser);
        taskQuery.order("-createdAt").findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if(e!=null){
                    Log.d("pullTaskList", "error");
                }else{
                    Message message = taskListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    taskListHandler.sendMessage(message);
                }
            }
        });
    }

    public void clickAccepted(){
        Button bt_published = getActivity().findViewById(R.id.bt_published);
        Button bt_accepted = getActivity().findViewById(R.id.bt_accepted);
        bt_published.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        bt_published.setTextSize(16);
        bt_accepted.setTextColor(getResources().getColor(R.color.colorPrimary));
        bt_accepted.setTextSize(20);

        BmobQuery<Task> taskQuery = new BmobQuery<>();
        taskQuery.addWhereEqualTo("accepter",currentUser);
        taskQuery.order("-createdAt").findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if(e!=null){
                    Log.d("pullTaskList", "error");
                }else{
                    Message message = taskListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    taskListHandler.sendMessage(message);
                }
            }
        });
    }

    public void iniPublished(){
        Button bt_published = mRoot.findViewById(R.id.bt_published);
        Button bt_accepted = mRoot.findViewById(R.id.bt_accepted);
        bt_published.setTextColor(getResources().getColor(R.color.colorPrimary));
        bt_accepted.setTextSize(16);
        bt_accepted.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        bt_published.setTextSize(20);
        BmobQuery<Task> taskQuery = new BmobQuery<>();
        taskQuery.addWhereEqualTo("publisher",currentUser);
        taskQuery.order("-createdAt").findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if(e!=null){
                    Log.d("pullTaskList", "error");
                }else{
                    Message message = taskListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    taskListHandler.sendMessage(message);
                }
            }
        });
    }
}
