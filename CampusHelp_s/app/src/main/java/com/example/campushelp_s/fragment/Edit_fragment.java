package com.example.campushelp_s.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.campushelp_s.R;

import bean.User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class Edit_fragment extends Fragment implements View.OnClickListener {
    private EditText et_name;
    private EditText et_sex;
    private EditText et_phone;
    private EditText et_email;
    private EditText et_introduction;
    private Button btn_reset;
    private Button btn_save;
    private ImageView iv_return;

    private View view;
    private User user;
    private com.example.campushelp_s.Model.userModel userModel;

    private View mRoot;
    private Activity mActivity;

    private Fragment currentFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.personal_edit_fg, container, false);
        userModel = new ViewModelProvider(getActivity()).get(com.example.campushelp_s.Model.userModel.class);//同一个活动下碎片之间共享usermodel里的user数据。
        user = userModel.getUser();//获取当前user对象
        currentFragment = Edit_fragment.this;
        refresh(user);

        mActivity = getActivity();
        mRoot=view;
        FrameLayout replace = mActivity.findViewById(R.id.personal_replace);
        replace.setVisibility(View.VISIBLE);

        btn_save.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FrameLayout replace = mActivity.findViewById(R.id.personal_replace);
        replace.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bj_bc:
                update(user);
                break;
            case R.id.bj_cz:
                refresh(user);
                break;
            case R.id.bj_return:

                getActivity().onBackPressed();//返回键
                break;

                default:
                    break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            currentFragment = Edit_fragment.this;
            userModel = new ViewModelProvider(getActivity()).get(com.example.campushelp_s.Model.userModel.class);
            userModel.getUser();
            refresh(user);
        }
    }


    /**
     * 更新个人信息
     *
     * @param user
     */
    public void update(User user) {
        user.setName(et_name.getText().toString());
        user.setSex(et_sex.getText().toString());
        user.setPhone(et_phone.getText().toString());
        user.setEmail(et_email.getText().toString());
        user.setInfo(et_introduction.getText().toString());

        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if(getActivity() == null){
                        getActivity().getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                    userModel.setUser(user);//更新本地user数据，方便更新其他碎片中的信息
                } else {
                    if(getActivity() == null){
                        getActivity().getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    Log.d("error",e.toString());
                    Toast.makeText(getActivity(),"更新失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 显示信息
     *
     * @param user
     */
    public void refresh(User user) {
        et_name = view.findViewById(R.id.bj_nc);
        et_email = view.findViewById(R.id.bj_yx);
        et_sex = view.findViewById(R.id.bj_xb);
        et_phone = view.findViewById(R.id.bj_sjh);
        et_introduction = view.findViewById(R.id.bj_grjj);
        btn_reset = view.findViewById(R.id.bj_cz);
        btn_save = view.findViewById(R.id.bj_bc);
        iv_return = view.findViewById(R.id.bj_return);

        et_name.setText(user.getName());//初始化显示信息
        et_sex.setText(user.getSex());
        et_phone.setText(user.getPhone());
        et_email.setText(user.getEmail());
        et_introduction.setText(user.getInfo());

    }



}
