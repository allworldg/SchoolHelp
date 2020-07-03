package com.example.campushelp_s;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import bean.User;
import bean.Task;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegisterActivity extends AppCompatActivity{

    private final int REQUEST_EXTERNAL_STORAGE=1;
    private static String[] PERMISSIONS_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Context context;

    //用于上传图片的变量
    private static final String TAG = "RegisterActivity";
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private ImageView iv_personal_icon;
    private View layout;
    private TextView takePhotoTV;
    private TextView choosePhotoTV;
    private TextView cancelTV;
    private ImageView img_return;;
    private User newUser;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        img_return = findViewById(R.id.zc_return);
        newUser=new User();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();//隐藏系统自带的顶部导航栏
        }
//取消严格模式  FileProvider

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }

        //动态权限申请
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
            }else{
                requestPermissions(PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        }else{
            Bmob.initialize(this, "3fdb919b080c6aec487233c1f30126ab");
            iv_personal_icon = (ImageView) findViewById(R.id.iv_user_icon);
        }
        img_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });



    }

    public void register(View view){

        //控件绑定
        EditText et_userId = findViewById(R.id.et_userId);
        EditText et_psw = findViewById(R.id.et_psw);
        EditText et_name = findViewById(R.id.et_name);
        Spinner sp_sex = findViewById(R.id.sp_sex);
        EditText et_phone = findViewById(R.id.et_phone);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_info = findViewById(R.id.et_info);

        //用户类设置属性
        newUser.setUserID(et_userId.getText().toString());
        newUser.setPassword(et_psw.getText().toString());
        newUser.setName(et_name.getText().toString());
        newUser.setSex(sp_sex.getSelectedItem().toString());
        newUser.setPhone(et_phone.getText().toString());
        newUser.setEmail(et_email.getText().toString());
        newUser.setInfo(et_info.getText().toString());

        newUser.setBalance(100);
        newUser.setDoneNumber(0);

        if(newUser.getPassword()==null){
            toastShow("密码不能为空");
        }

        //检测是否已存在相同id的用户
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("userID",newUser.getUserID());
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e!=null){
                    toastShow(e.toString());
                }else{
                    if(list.size()!=0){
                        toastShow("账号已存在，请更换");
                    }
                    else{
                        newUser.save(new SaveListener<String>() {//上传用户数据
                            @Override
                            public void done(String s, BmobException e) {
                                if(e!=null){
                                    toastShow(e.toString());
                                }else{
                                    toastShow("Succsee!!");
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    public void toastShow(String msg) {
        Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_LONG).show();
    }

    /*用于给按钮点击事件*/
    public void uploadPhoto(View view){startUploadImage();}

    public void startUploadImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String img_url = uri.getPath();//这是本机的图片路径
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ImageView imageView = findViewById(R.id.iv_user_icon);
                    /*上传至服务器*/
                    upImageToServer(uri);
                    /* 将Bitmap设定到ImageView */
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void upImageToServer(Uri uri){
        //String path = UriToPathUtil.getRealFilePath(this,uri);
        String path = FileUtils.getPath(getApplicationContext(),uri);

        BmobFile userIcon = new BmobFile(new File(path));
        userIcon.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null){
                    Log.d("Upload", e.toString());
                    toastShow("上传服务器失败");
                }else{
                    toastShow("上传成功");
                }
            }
        });
        try {
            newUser.setImage(userIcon);
            getActionBar();
        }catch (Exception e){
            Log.d("setImage", e.toString());
        }
    }
}


