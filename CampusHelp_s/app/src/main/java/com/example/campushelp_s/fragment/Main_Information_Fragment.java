package com.example.campushelp_s.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.campushelp_s.R;
import com.example.campushelp_s.databinding.PersonalMainFragBinding;
import java.util.List;
import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class Main_Information_Fragment extends Fragment/* implements View.OnClickListener*/ {

    private PersonalMainFragBinding binding;
    private User user;
    private com.example.campushelp_s.Model.userModel userModel;
    private int number;
    private String num;
    private View mRoot;
    private Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PersonalMainFragBinding.inflate(getLayoutInflater());
        userModel = new ViewModelProvider(getActivity()).get(com.example.campushelp_s.Model.userModel.class);
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        userModel.setUser(user);
        collection_Number();
        follow_Number();
        mRoot=binding.getRoot();
        mActivity=getActivity();
        return binding.getRoot();
    }

    /**
     * 初始化数据并且显示
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d()
        refresh(user);
        binding.wdBj.setOnClickListener(Navigation
                .createNavigateOnClickListener(R.id.action_navigation_my_to_edit_fragment));//个人信息编辑按钮
        binding.wdScjT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump_to_collection();
            }
        });//收藏夹跳转按钮
        binding.wdGzT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_navigation_my_to_attention_fragment);
            }
        });//关注跳转按钮
    }

    /**
     * 用于初始化以及之后编辑子碎片返回时方便刷新
     *
     * @param user
     */
    public void refresh(User user) {
//        tv_user_account.setText(user.getBalance().toString()+" U币");
        num = userModel.collection_Number();
        BmobFile icon = user.getImage();
        if(icon!=null){
            icon.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e!=null){
                        Log.d("downloadIcon", e.toString());
                    }else{
//                        image_head.setImageBitmap(BitmapFactory.decodeFile(s));
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }else{
//            image_head.setImageResource(R.drawable.default_user_icon);
        }

    }

    /**
     * 简单收集一下收藏任务的数量，转换为string类型输出
     * @return
     */
    public void collection_Number() {

        BmobQuery<Task> taskQuery = new BmobQuery<>();//查找收藏的任务

        taskQuery.addWhereRelatedTo("collection", new BmobPointer(user));
        taskQuery.findObjects(new FindListener<Task>() {
            @Override
            public void done(List<Task> list, BmobException e) {
                if (e == null){
                    number = list.size();
                    Log.d("number",""+number);
                    Log.d("listsize",""+list.size());
                    TextView tv_collectionNum = mRoot.findViewById(R.id.wd_scj);
                    tv_collectionNum.setText(Integer.toString(number));
                } else {
                    Log.d("error1",e.toString());
                }
            }
        });

    }

    /**
     * 简单收集一下关注用户的数量，转换为string类型输出
     * @return
     */
    public void follow_Number() {

        BmobQuery<User> taskQuery = new BmobQuery<>();//查找收藏的任务

        taskQuery.addWhereRelatedTo("follow", new BmobPointer(user));
        taskQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    number = list.size();
                    Log.d("number",""+number);
                    Log.d("listsize",""+list.size());
                    TextView tv_followNum = mRoot.findViewById(R.id.wd_gz);
                    tv_followNum.setText(Integer.toString(number));
                } else {
                    Log.d("error1",e.toString());
                }
            }
        });

    }



    //跳转到编辑页面
    public void jump_to_edit(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //String textItem =  ((TextView) view).getText().toString();
        Edit_fragment edit_fragment = new Edit_fragment();
        Bundle bundle = new Bundle();
        bundle.putString("currentUserObjectId", user.getObjectId());//原来是注释掉的
        edit_fragment.setArguments(bundle);
        transaction
                .addToBackStack(null)  //将当前fragment加入到返回栈中
                .add(R.id.personal_replace,edit_fragment)
                .show(edit_fragment)
                .commit();
    }

    //跳转到收藏页面
    public void jump_to_collection(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //String textItem =  ((TextView) view).getText().toString();
        Collection_Fragment collection_fragment = new Collection_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("currentUserObjectId", user.getObjectId());//原来是注释掉的
        collection_fragment.setArguments(bundle);
        transaction
                .addToBackStack(null)  //将当前fragment加入到返回栈中
                .add(R.id.personal_replace,collection_fragment)
                .show(collection_fragment)
                .commit();
    }
}
