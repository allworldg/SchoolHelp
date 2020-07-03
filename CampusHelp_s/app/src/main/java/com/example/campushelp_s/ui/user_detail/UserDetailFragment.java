package com.example.campushelp_s.ui.user_detail;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.campushelp_s.R;

import java.util.List;

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

public class UserDetailFragment extends Fragment {

    private User currentUser;
    private User detailedUser;
    private String currentUserObjectId;
    private String detailedUserObjectId;
    private UserDetailModel userDetailModel;

    public View mRoot;
    public Activity mActivity;

    /*传入user与user的构造函数*/
    public UserDetailFragment(User currentUser,User detailedUser){
        this.currentUser=currentUser;
        this.detailedUser=detailedUser;
        this.currentUserObjectId=currentUser.getObjectId();
        this.detailedUserObjectId=detailedUser.getObjectId();
    }

    /*传入user与userObject的构造函数*/
    public UserDetailFragment(User currentUser,String detailedUserObjectId){
        this.currentUser=currentUser;
        this.currentUserObjectId=currentUser.getObjectId();
        this.detailedUserObjectId=detailedUserObjectId;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userDetailModel =
                ViewModelProviders.of(this).get(UserDetailModel.class);
        View root = inflater.inflate(R.layout.fragment_user_detail, container, false);
        //final TextView textView = root.findViewById(R.id.text_task_detail);
        //textView.setText(currentTask.getTitle());
        FrameLayout replace = getActivity().findViewById(R.id.user_detail_replace);
        replace.setVisibility(View.VISIBLE);
        //textView.setText();
        userDetailModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        currentUserObjectId=getArguments().getString("currentUserObjectId");

        mRoot=root;
        mActivity=getActivity();
        init(root);
        findCurrentUserAndDetailedUser();
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        FrameLayout replace = getActivity().findViewById(R.id.user_detail_replace);
        replace.setVisibility(View.INVISIBLE);
    }

    public void init(View view){
        ImageView iv_user_detail_icon = view.findViewById(R.id.ck_tx);
        TextView tv_user_detail_name = view.findViewById(R.id.tv_user_detail_name);
        TextView tv_user_detail_id = view.findViewById(R.id.tv_user_detail_id);
        TextView tv_user_detail_sex = view.findViewById(R.id.ck_xb);
        TextView tv_user_detail_phone = view.findViewById(R.id.ck_sjh);
        TextView tv_user_detail_email = view.findViewById(R.id.ck_yx);
        TextView tv_user_detail_balance = view.findViewById(R.id.ck_ye);
        TextView tv_user_detail_follow = view.findViewById(R.id.ck_gz);
        TextView tv_user_detail_collection = view.findViewById(R.id.ck_scj);
        TextView tv_user_detail_doneNumber = view.findViewById(R.id.ck_wcsl);
        TextView tv_user_detail_info = view.findViewById(R.id.ck_grjj);

        //setImage暂时省略
        tv_user_detail_name.setText(detailedUser.getName());
        tv_user_detail_id.setText(detailedUser.getUserID());
        tv_user_detail_sex.setText(detailedUser.getSex());
        tv_user_detail_phone.setText(detailedUser.getPhone());
        tv_user_detail_email.setText(detailedUser.getEmail());
        tv_user_detail_balance.setText(detailedUser.getBalance().toString());
        //tv_user_detail_follow.setText(detailedUser.g);
        tv_user_detail_doneNumber.setText(Integer.toString(detailedUser.getDoneNumber().intValue()));
        tv_user_detail_info.setText(detailedUser.getInfo());
    }

    /*测试中用于删除粉丝*/
    public void testremovefans(){
        BmobRelation fansRelation = new BmobRelation();
        fansRelation.remove(currentUser);
        detailedUser.setFans(fansRelation);
        detailedUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null){
                    toastShow("取关失败");
                }else{
                    toastShow("取关成功");
                }
            }
        });

        fansRelation.remove(detailedUser);
        detailedUser.setFans(fansRelation);
        detailedUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null){
                    toastShow("取关失败");
                }else{
                    toastShow("取关成功");
                }
            }
        });
    }

    /*吐司消息显示*/
    public void toastShow(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    public void refresh(View view){
        findCurrentUserAndDetailedUser();
    }

    public void findCurrentUserAndDetailedUser(){
        /*查询当前操作用户*/
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId", currentUserObjectId);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e != null) {
                    toastShow(e.toString());
                } else {
                    Message message = currentUserHandler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    currentUserHandler.sendMessage(message);
                }
            }
        });
    }


    /*
     * 接收currentUser的查询结果并完成后续操作
     * 用于当前操作用户的异步传输
     * 完成传输后，子线程进行当前查询用户的拉取
     * */
    private Handler currentUserHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    currentUser = list.get(0);
                    break;
            }

            /*查询当前查询用户 */
            BmobQuery<User> detailedUserQuery = new BmobQuery<>();
            detailedUserQuery.addWhereEqualTo("objectId", detailedUserObjectId);
            detailedUserQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e != null) {
                        toastShow(e.toString());
                    } else {
                        Message message = detailedUserHandler.obtainMessage();
                        message.what = 0;
                        //以消息为载体
                        message.obj = list;//这里的list就是查询出list
                        //向handler发送消息
                        detailedUserHandler.sendMessage(message);
                    }
                }
            });
        }
    };

    /*
     * 接收当前查询用户的信息
     * 完成当前查询用户信息显示
     * 设置关注或取关按钮
     * */
    private Handler detailedUserHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    detailedUser = list.get(0);

                    Button bt_user_operator = mRoot.findViewById(R.id.ck_follow);

                    //testremovefans();
                    ImageView iv_user_icon = mRoot.findViewById(R.id.ck_tx);
                    BmobFile icon = list.get(0).getImage();
                    if(icon!=null){
                        icon.download(new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e!=null){
                                    Log.d("downloadIcon", e.toString());
                                }else{
                                    iv_user_icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                }
                            }

                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    }else{
                        iv_user_icon.setImageResource(R.drawable.default_user_icon);
                    }

                    if (currentUserObjectId.equals(detailedUserObjectId)) {
                        //若当前操作用户与查询用户匹配，无法进行关注与取关
                        bt_user_operator.setVisibility(View.INVISIBLE);

                    } else {
                        //若当前操作用户与查询用户匹配，首先查询当前操作用户关注列表中有无查询用户，可进行关注与取关
                        bt_user_operator.setVisibility(View.VISIBLE);
                        BmobQuery<User> fansQuery = new BmobQuery<>();
                        fansQuery.addWhereRelatedTo("fans",new BmobPointer(detailedUser));
                        fansQuery.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                boolean isExist = false;
                                for(int i=0;i<list.size();i++) {
                                    if(list.get(i).getObjectId().equals(currentUser.getObjectId())){
                                        isExist=true;
                                    }
                                }

                                if(isExist){
                                    //若被查询用户的粉丝表中有当前操作用户，点击按钮取关
                                    bt_user_operator.setText("取关");
                                    bt_user_operator.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //当前操作用户currentUser的follow集中删除查询用户DetailedUser
                                            BmobRelation followRelation = new BmobRelation();
                                            followRelation.remove(detailedUser);
                                            currentUser.setFollow(followRelation);
                                            currentUser.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e!=null){
                                                        toastShow("取关失败");
                                                    }else{
                                                        toastShow("取关成功");
                                                    }
                                                }
                                            });

                                            //查询用户DetailedUser的fans集中删除当前操作用户currentUser
                                            BmobRelation fansRelation = new BmobRelation();
                                            fansRelation.remove(currentUser);
                                            detailedUser.setFans(fansRelation);
                                            detailedUser.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e!=null){
                                                        toastShow("取关失败");
                                                    }else{
                                                        toastShow("取关成功");
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    //若被查询用户的粉丝表中无当前操作用户，点击按钮关注
                                    bt_user_operator.setText("关注");
                                    bt_user_operator.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //当前操作用户follow中添加查询用户
                                            BmobRelation followRelation = new BmobRelation();
                                            followRelation.add(detailedUser);
                                            currentUser.setFollow(followRelation);
                                            currentUser.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e!=null){
                                                        toastShow("关注失败");
                                                    }else{
                                                        toastShow("关注成功");
                                                    }
                                                }
                                            });

                                            //当前查询用户fans中添加当前操作用户
                                            BmobRelation fansRelation = new BmobRelation();
                                            fansRelation.add(currentUser);
                                            detailedUser.setFans(fansRelation);
                                            detailedUser.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e!=null){
                                                        toastShow("关注失败");
                                                    }else{
                                                        toastShow("关注成功");
                                                    }
                                                }
                                            });

                                        }
                                    });
                                }
                            }
                        });
                    }
                    findCurrentUserAndDetailedUser();
                    break;
            }
        }
    };
}
