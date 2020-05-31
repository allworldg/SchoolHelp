package com.example.campushelp_s.ui.task_detail;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import com.example.campushelp_s.R;
import com.example.campushelp_s.ui.user_detail.UserDetailFragment;

import java.util.List;
import java.util.Map;

import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class TaskDetailFragment extends Fragment {

    private Task currentTask;
    private User currentUser;
    private User taskPublisher;
    private String currentTaskObjectId;
    private String currentUserObjectId;
    private String tasPublisherObjectId;

    private TaskDetailModel taskDetailModel;

    public View mRoot;
    public Activity mActivity;

    /*传入task与user的构造函数*/
    public TaskDetailFragment(Task task,User user){
        this.currentTask=task;
        this.currentUser=user;
        this.currentTaskObjectId=task.getObjectId();
        this.currentUserObjectId=task.getObjectId();
    }

    /*传入task与user的构造函数*/
    public TaskDetailFragment(Task task){
        this.currentTask=task;
        this.currentTaskObjectId=task.getObjectId();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        taskDetailModel =
                ViewModelProviders.of(this).get(TaskDetailModel.class);
        View root = inflater.inflate(R.layout.fragment_task_detail, container, false);
        //final TextView textView = root.findViewById(R.id.text_task_detail);
        //textView.setText(currentTask.getTitle());
        FrameLayout replace = getActivity().findViewById(R.id.task_detail_replace);
        replace.setVisibility(View.VISIBLE);
        //textView.setText();

        currentUserObjectId=getArguments().getString("currentUserObjectId");

        taskDetailModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });



        findCurrentUserAndTask();

        ImageView publisherIcon = root.findViewById(R.id.rwxq_tx);
        publisherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump_to_user_detail();
            }
        });
        mRoot=root;
        mActivity=getActivity();
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        FrameLayout replace = mActivity.findViewById(R.id.task_detail_replace);
        replace.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    /*刷新页面*/
    public void refresh(View view) {
        findCurrentUserAndTask();
        toastShow("刷新成功");
    }

    /*刷新页面
     * 可用于被其他方法调用的重载
     * */
    public void refresh() {
        findCurrentUserAndTask();
        findTaskCollected();
    }


    /*查询当前用户及任务，子线程接收用户信息，后序操作在子线程中*/
    public void findCurrentUserAndTask() {

        /*查询当前操作用户*/
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId", currentUserObjectId);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e != null) {
                    toastShow(e.toString());
                } else {
                    Message message = userHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    userHandler.sendMessage(message);
                }
            }
        });
    }


    /*吐司消息显示*/
    public void toastShow(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
    }

    /*
     * 用于用户类的异步传输
     * 完成传输后，子线程进行任务查询
     * */
    private Handler userHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    currentUser = list.get(0);
                    break;
            }

            /*查询当前操作任务 */
            BmobQuery<Task> taskQuery = new BmobQuery<>();
            taskQuery.addWhereEqualTo("objectId", currentTaskObjectId);
            taskQuery.findObjects(new FindListener<Task>() {
                @Override
                public void done(List<Task> list, BmobException e) {
                    if (e != null) {
                        toastShow(e.toString());
                    } else {
                        Message message = taskHandler.obtainMessage();
                        message.what = 0;
                        //以消息为载体
                        message.obj = list;//这里的list就是查询出list
                        //向handler发送消息
                        taskHandler.sendMessage(message);
                    }
                }
            });
        }
    };


    /*
     * 接收任务类的异步传输
     * 完成后续流程
     * */
    private Handler taskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<Task> list = (List<Task>) msg.obj;
                    currentTask = list.get(0);

                    //TextView tv_task_title = mRoot().findViewById(R.id.rwxq_);
                    TextView tv_task_text = mRoot.findViewById(R.id.rwxq_wz);
                    TextView tv_task_status = mRoot.findViewById(R.id.rwxq_rwzt);
                    TextView tv_task_OAR = mRoot.findViewById(R.id.rwxq_xs);
                    TextView tv_task_type =mRoot.findViewById(R.id.rwxq_lb);

                    tv_task_status.setText(currentTask.getStatus());
                    //tv_task_title.setText(currentTask.getTitle());
                    tv_task_text.setText(currentTask.getDescription());
                    tv_task_OAR.setText(Integer.toString(currentTask.getOAR().intValue()));
                    tv_task_type.setText(currentTask.getType());



                    Button bt_task_acceptOrAbandon = mRoot.findViewById(R.id.rwxq_lq);
                    Button bt_task_collect = mRoot.findViewById(R.id.rwxq_sc);
                    Button bt_task_done = mRoot.findViewById(R.id.rwxq_wcrw);
                    Button bt_task_remove = mRoot.findViewById(R.id.rwxq_scrw);
                    ImageView iv_task_picture = mRoot.findViewById(R.id.rwxq_tp);
                    //加载图片
                    BmobFile picture = list.get(0).getPicture();
                    if(picture!=null){
                        picture.download(new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e!=null){
                                    Log.d("downloadIcon", e.toString());
                                }else{
                                    iv_task_picture.setImageBitmap(BitmapFactory.decodeFile(s));
                                }
                            }

                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    }else{
                        iv_task_picture.setImageResource(R.drawable.default_user_icon);
                    }


                    findTaskCollected();//查询当前任务是否被当前用户收藏

                    if (currentTask.getPublisher().getObjectId().equals(currentUserObjectId)) {
                        //若当前用户与任务发布者匹配，则进行任务完成或删除操作
                        bt_task_acceptOrAbandon.setVisibility(View.INVISIBLE);
                        bt_task_collect.setVisibility(View.INVISIBLE);
                        bt_task_done.setVisibility(View.VISIBLE);
                        bt_task_remove.setVisibility(View.VISIBLE);
                        bt_task_done.setText("确认完成");
                        bt_task_remove.setText("删除任务");

                        bt_task_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeStatus_Publisher();
                            }
                        });
                    } else {
                        //若当前用户不是发布者，则进行任务接受或放弃操作
                        bt_task_acceptOrAbandon.setVisibility(View.VISIBLE);
                        bt_task_collect.setVisibility(View.VISIBLE);
                        bt_task_done.setVisibility(View.INVISIBLE);
                        bt_task_remove.setVisibility(View.INVISIBLE);
                        bt_task_acceptOrAbandon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String status = currentTask.getStatus();
                                if (status.equals("已接受")) {
                                    bt_task_acceptOrAbandon.setText("放弃");
                                } else if (status.equals("未接受")) {
                                    bt_task_acceptOrAbandon.setText("接受");
                                }
                                changeStatus_notPublisher();
                            }
                        });
                    }
                    break;
            }

            /*查询当前操作任务发布者 */
            String publisherObjecId = currentTask.getPublisher().getObjectId();
            BmobQuery<User> publisherQuery = new BmobQuery<>();
            publisherQuery.addWhereEqualTo("objectId",publisherObjecId);
            publisherQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e != null) {
                        toastShow(e.toString());
                    } else {
                        TextView tv_task_publisher_name = mRoot.findViewById(R.id.rwxq_nc);
                        TextView tv_task_publisher_id = mRoot.findViewById(R.id.rwxq_id);
                        ImageView iv_task_publisher_icon = mRoot.findViewById(R.id.rwxq_tx);
                        String text =  "发布者："+list.get(0).getName();
                        tv_task_publisher_name.setText(text);
                        tv_task_publisher_id.setText("ID: "+list.get(0).getUserID());


                       /*查询当前任务的发布者用于传入下一个页面*/
                        Message message = publisherHandler.obtainMessage();
                        message.what = 0;
                        //以消息为载体
                        message.obj = list;//这里的list就是查询出list
                        //向handler发送消息
                        publisherHandler.sendMessage(message);
                    }
                }
            });
        }
    };

    /*接收任务发布者并赋值到全局变量*/
    private Handler publisherHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    taskPublisher = list.get(0);

                    TextView tv_task_publisher_name = mRoot.findViewById(R.id.rwxq_nc);
                    TextView tv_task_publisher_id = mRoot.findViewById(R.id.rwxq_id);
                    ImageView iv_task_publisher_icon = mRoot.findViewById(R.id.rwxq_tx);
                    String text =  "发布者："+list.get(0).getName();
                    tv_task_publisher_name.setText(text);
                    tv_task_publisher_id.setText("ID: "+list.get(0).getUserID());

                    //加载图片
                    BmobFile icon = list.get(0).getImage();
                    if(icon!=null){
                        icon.download(new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e!=null){
                                    Log.d("downloadIcon", e.toString());
                                }else{
                                    iv_task_publisher_icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                }
                            }

                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    }else{
                        iv_task_publisher_icon.setImageResource(R.drawable.default_user_icon);
                    }
                    break;
            }
        }
    };




    /*非任务的发布者进行任务领取或放弃*/
    public void changeStatus_notPublisher() {
        Button bt_task_opertator = mRoot.findViewById(R.id.rwxq_lq);

        String status = currentTask.getStatus();
        if (status.equals("未接受")) {
            currentTask.setAccepter(currentUser);
            currentTask.setStatus("已接受");
            currentTask.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        Log.d("update", e.toString());
                        toastShow(e.toString());
                    } else {
                        toastShow("Accecpted");
                        bt_task_opertator.setText("放弃");
                    }
                }
            });
        } else if (status.equals("已接受")) {
            currentTask.remove("accepter");
            currentTask.setStatus("未接受");
            currentTask.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        toastShow(e.toString());
                    } else {
                        toastShow("Abandoned");
                        bt_task_opertator.setText("接收");
                    }
                }
            });
        }
    }

    /*任务发布者的任务完成与删除*/
    public void changeStatus_Publisher() {
        Button bt_task_opertator = mRoot.findViewById(R.id.rwxq_wcrw);

        String status = currentTask.getStatus();
        //如果状态以接收则可以确认完成
        if (status.equals("已接受")) {
            currentTask.setStatus("已完成");
            currentTask.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        Log.d("update", e.toString());
                        toastShow(e.toString());
                    } else {
                        BmobQuery<User> query = new BmobQuery<>();
                        query.addWhereEqualTo("objectId", currentTask.getAccepter().getObjectId());
                        query.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                if (e != null) {
                                    toastShow("悬赏到账失败");
                                } else {
                                    User accepter = list.get(0);
                                    Number originBalance = accepter.getBalance();
                                    Number OAR = currentTask.getOAR();
                                    Number doneNumber = accepter.getDoneNumber();
                                    accepter.setBalance(originBalance.intValue() + OAR.intValue());
                                    accepter.setDoneNumber(doneNumber.intValue()+1);
                                    accepter.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e!=null){
                                                toastShow("悬赏更新失败");
                                            }else{
                                                toastShow("任务完成，悬赏已到对方账户！");
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        bt_task_opertator.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else if (status.equals("未接受")) {
            //如果状态为未接收则不能确认完成
            toastShow("无人领取，无法设置完成");
        }else if(status.equals("已完成")){
            toastShow("任务已完成，无法进行操作");
        }
    }

    /*任务的收藏与否的查询*/
    public void findTaskCollected(){

        BmobQuery<Task> collectionQuery = new BmobQuery<>();
        collectionQuery.addWhereRelatedTo("collection",new BmobPointer(currentUser));
        collectionQuery.findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                boolean isCollected=false;
                if(e!=null){
                    toastShow("查询是否收藏失败");
                }else{
                    for(int i=0;i<list.size();i++){//查找用户的collection集合中有无task
                        if(list.get(i).getObjectId().equals(currentTask.getObjectId())){
                            isCollected = true;
                        }
                    }
                    //toastShow("查询是否收藏成功");
                }

                Button bt_task_collection = mRoot.findViewById(R.id.rwxq_sc);

                if(isCollected){
                    bt_task_collection.setText("取消收藏");
                    bt_task_collection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelCollect();
                        }
                    });
                }else{
                    bt_task_collection.setText("收藏");
                    bt_task_collection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            collect();
                        }
                    });
                }
            }
        });
    }

    /*收藏*/
    public void collect(){
        BmobRelation taskRelation = new BmobRelation();
        taskRelation.add(currentTask);
        currentUser.setCollection(taskRelation);
        currentUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null){
                    toastShow("收藏失败");
                }else{
                    toastShow("收藏成功");
                }
            }
        });
        refresh();
    }

    /*取消收藏*/
    public void cancelCollect(){
        BmobRelation taskRelation = new BmobRelation();
        taskRelation.remove(currentTask);
        currentUser.setCollection(taskRelation);
        currentUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null){
                    toastShow("取消收藏失败");
                }else{
                    toastShow("取消收藏成功");
                }
            }
        });
        refresh();
    }


    public void jump_to_user_detail(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //String textItem =  ((TextView) view).getText().toString();
        UserDetailFragment userDetailFragment = new UserDetailFragment(currentUser,taskPublisher);
        Bundle bundle = new Bundle();
        bundle.putString("currentUserObjectId", currentUserObjectId);//原来是注释掉的
        userDetailFragment.setArguments(bundle);
        transaction
                .addToBackStack(null)  //将当前fragment加入到返回栈中
                .add(R.id.user_detail_replace,userDetailFragment)
                .show(userDetailFragment)
                .commit();
    }
}
