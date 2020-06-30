package com.example.campushelp_s;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.campushelp_s.databinding.ActivityLoginBinding;
import java.util.ArrayList;
import java.util.List;
import bean.User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    ImageView image_head;
    private Boolean bPwdSwitch = false;//图像切换判断

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bmob.initialize(this,"3fdb919b080c6aec487233c1f30126ab");
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();//隐藏系统自带的顶部导航栏
        }
        image_head = findViewById(R.id.dl_tx);
       binding.dlDl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"登陆ing......",Toast.LENGTH_SHORT).show();
                findUser(LoginActivity.this,binding.dlZh,binding.dlMm);
            }
        });
        binding.dlZczh.setOnClickListener(new View.OnClickListener() {
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
        userAccount_BmobQuery.addWhereEqualTo("userID", binding.dlZh.getText().toString());//查询账号是否存在
        BmobQuery<User> userPwd_BmobQuery = new BmobQuery<>();
        userPwd_BmobQuery.addWhereEqualTo("password", binding.dlMm.getText().toString());//查询密码是否存在
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
        bPwdSwitch = !bPwdSwitch;
        if(bPwdSwitch){
            ivPwdSwitch.setImageResource(R.drawable.ic_visibility_black_24dp);
            binding.dlMm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            ivPwdSwitch.setImageResource(R.drawable.ic_visibility_off_black_24dp);
            binding.dlMm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            binding.dlMm.setTypeface(Typeface.DEFAULT);
        }
    }
    public void jump_to_register(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
