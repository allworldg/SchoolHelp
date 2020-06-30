package com.example.campushelp_s.ui.square;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
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

import com.example.campushelp_s.MainActivity;
import com.example.campushelp_s.R;
import com.example.campushelp_s.TaskAdapter;
import com.example.campushelp_s.ui.task_detail.TaskDetailFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import bean.Task;
import bean.User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SquareFragment extends Fragment {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView rvList;
    private SquareViewModel squareViewModel;
    private User currentUser;
    private String currentUserObjectId;
    private List<User> followList;
    private View mRoot;
    private Activity mActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        squareViewModel =
                ViewModelProviders.of(this).get(SquareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_square, container, false);
        //final TextView textView = root.findViewById(R.id.text_square);
        squareViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        mRoot=root;
        mActivity=getActivity();
        //View ret = inflater.inflate(R.layout.fragment_square,container,false);

        //接收主活动的值
        Map<String, NavArgument> map = NavHostFragment.findNavController(this).getGraph().getArguments();
        NavArgument navArgument = map.get("currentUserObjectId");
        assert navArgument != null;
        currentUserObjectId = (String) navArgument.getDefaultValue();
        Button bt_jump_square = mRoot.findViewById(R.id.bt_jump_square);
        bt_jump_square.setVisibility(View.VISIBLE);
        Bmob.initialize(getContext(),"3fdb919b080c6aec487233c1f30126ab");
        rvList=root.findViewById(R.id.rv_task);//在ret已经建立的情况下需要使用ret来进行控件绑定
        initTaskData_all();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initTaskData_all(){

        Button bt_jump_square = mRoot.findViewById(R.id.bt_jump_square);
        bt_jump_square.setText("查看关注");
        //下一次点击跳转到我的关注，进行关注者任务初始化
        bt_jump_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTaskData_follow();
            }
        });

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

    private void initTaskData_follow(){
        Button bt_jump_square = mRoot.findViewById(R.id.bt_jump_square);
        bt_jump_square.setText("查看所有");
        //下一次点击跳转到所有，把所有任务都显示出来
        bt_jump_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTaskData_all();
            }
        });

        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId",currentUserObjectId);
        userQuery.order("-createdAt").findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e!=null){
                    Log.d("pullUserList", e.toString());
                }else{
                    Message message = userListHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    userListHandler.sendMessage(message);
                }
            }
        });
    }

    private Handler userListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    currentUser = list.get(0);

                    BmobQuery<User> followQuery = new BmobQuery<User>();
                    followQuery.addWhereRelatedTo("follow",new BmobPointer(currentUser));
                    followQuery.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if(e!=null){
                                Log.d("findFollow", e.toString());
                            }else {
                                Message message = followListHandler.obtainMessage();
                                message.what = 0;
                                //以消息为载体
                                message.obj = list;//这里的list就是查询出list
                                //向handler发送消息
                                followListHandler.sendMessage(message);
                            }
                        }
                    });

                    break;
            }
        }
    };

    private Handler followListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    followList=list;
                    BmobQuery<Task> taskQuery = new BmobQuery<>();
//                    User[] follows = {};

                    List<String> followObjectId = new ArrayList<>() ;

                    for(int i = 0;i<list.size();i++){
                        followObjectId.add(list.get(i).getObjectId());
                    }

                    taskQuery.addWhereContainedIn("publisher",followObjectId);

                    //查找所有关注者发布的任务
                    taskQuery.findObjects(new FindListener<Task>() {
                        @Override
                        public void done(List<Task> list, BmobException e) {
                            if(e!=null){
                                Log.d("findFollowTask", e.toString());
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
                break;
            }
        }
    };

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
                            bundle.putString("currentUserObjectId", currentUserObjectId);//原来是注释掉的
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

    public void toastShow (String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
