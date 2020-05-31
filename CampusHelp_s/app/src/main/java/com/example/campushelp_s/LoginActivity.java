package com.example.campushelp_s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import bean.Task;
import bean.User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    User user;
    ImageView image_head;
    EditText et_account;
    EditText et_passwd;
    Button btn_login;
    private Boolean bPwdSwitch = false;//图像切换判断
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this,"3fdb919b080c6aec487233c1f30126ab");
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();//隐藏系统自带的顶部导航栏
        }
        image_head = findViewById(R.id.dl_tx);
        et_account = findViewById(R.id.dl_zh);
        et_passwd = findViewById(R.id.dl_mm);
        btn_login = findViewById(R.id.dl_dl);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findUser(LoginActivity.this,et_account,et_passwd);
            }
        });
        Button btn_register = findViewById(R.id.dl_zczh);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump_to_register();
            }
        });
    }

    /**
     * 找到loginuser并且进行页面跳转，传入user对象
     * @param context
     * @param et_account
     * @param et_passwd
     */
    public  void findUser(Context context,EditText et_account, EditText et_passwd) {
        BmobQuery<User> userAccount_BmobQuery = new BmobQuery<>();
        userAccount_BmobQuery.addWhereEqualTo("userID", et_account.getText().toString());//查询账号是否存在
        BmobQuery<User> userPwd_BmobQuery = new BmobQuery<>();
        userPwd_BmobQuery.addWhereEqualTo("password", et_passwd.getText().toString());//查询密码是否存在
        List<BmobQuery<User>> andqueries = new ArrayList<BmobQuery<User>>();//两者条件同时成立

        andqueries.add(userAccount_BmobQuery);
        andqueries.add(userPwd_BmobQuery);

        BmobQuery<User> query = new BmobQuery<>();
        query.and(andqueries);//查询符合整个and条件的人

        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null){
                    if(list.size() != 0){//如果长度不为0，则查询到用户，进行页面跳转
                        Intent intent = new Intent(context,MainActivity.class);
                        intent.putExtra("user",list.get(0));
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this,"用户账号或者密码输错，请重新输入",Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });


    }

    public void clickEyes(View view){
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        et_passwd = findViewById(R.id.dl_mm);
        bPwdSwitch = !bPwdSwitch;
        if(bPwdSwitch){
            ivPwdSwitch.setImageResource(R.drawable.ic_visibility_black_24dp);
            et_passwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            ivPwdSwitch.setImageResource(R.drawable.ic_visibility_off_black_24dp);
            et_passwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            et_passwd.setTypeface(Typeface.DEFAULT);
        }

    }

    /**
     * 用做测试
     * @return
     */
    public void addPointer(View view) {
        Task task=new Task();
        //task.setDeadline();
        task.setOAR(15);
        task.setDescription("te");
        task.setTaskId("1");
        task.setTitle("hello");
        task.setType("well");
        User user=new User();
        user.setObjectId("3kra111X");
        task.setAccepter(user);
        task.setPublisher(user);
        task.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e!=null){
                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this,"success",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static User init() {
        User user = new User();
        user.setSex("1");
        user.setName("2");
        user.setPassword("3");
        user.setUserID("4");
        user.setBalance(1);
        user.setDoneNumber(2);
        user.setEmail("22323");
        user.setInfo("info");
        user.setPhone("phone");
        return user;
    }

    public void jump_to_register(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
