package com.example.campushelp_s.ui.submit_task;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavArgument;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campushelp_s.FileUtils;
import com.example.campushelp_s.R;
import com.example.campushelp_s.TaskAdapter;
import com.example.campushelp_s.ui.square.SquareViewModel;
import com.example.campushelp_s.ui.task_detail.TaskDetailFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;

public class SubmitTaskFragment extends Fragment {

    private SubmitTaskViewModel submitTaskViewModel;

    private String currentUserObjectId;
    private User currentUser = new User();

    private static int year;
    private static int month;
    private static int dayOfMonth;
    private static int hour;
    private static int minute;
    private static int second;

    private Task newTask;
    BmobDate bombDate;

    Activity mActivity;
    Context mContext;
    View mRoot;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        submitTaskViewModel =
                ViewModelProviders.of(this).get(SubmitTaskViewModel.class);
        View root = inflater.inflate(R.layout.fragment_submit_task, container, false);
        //final TextView textView = root.findViewById(R.id.text_submit_task);
        submitTaskViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                initCurrentUser();
            }
        });

        newTask = new Task();
        //接收主活动的值
        Map<String, NavArgument> map = NavHostFragment.findNavController(this).getGraph().getArguments();
        NavArgument navArgument = map.get("currentUserObjectId");
        assert navArgument != null;
        currentUserObjectId = (String) navArgument.getDefaultValue();

        mActivity=getActivity();
        mContext=getContext();
        mRoot=root;


        initCurrentUser();//初始化当前用户
        Button bt_submit = root.findViewById(R.id.fb_df);
        ImageView taskImage = root.findViewById(R.id.fb_picture);
        taskImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUploadImage();
            }
        });

        try {

            initDeadlinePicker(root);
        }catch (Exception e){
            Log.d("calender", "onCreateView: ");
        }

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initDeadlinePicker(View root){
        //获取当前日期
        DatePicker submit_task_deadlinePicker_date = root.findViewById(R.id.fb_datepicker);
        TimePicker submit_task_deadlinePicker_time = root.findViewById(R.id.fb_timepicker);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        //初始化日期选择器并设置日期改变监听器
        submit_task_deadlinePicker_date.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //toastShow("日期变更");
                SubmitTaskFragment.year=year;
                SubmitTaskFragment.month=monthOfYear;
                SubmitTaskFragment.dayOfMonth=dayOfMonth;
                toastShow(Integer.toString(year)+"年"
                        +Integer.toString(monthOfYear+1)+"月"
                        +Integer.toString(dayOfMonth)+"日");
                setDateTime();
            }
        });


        submit_task_deadlinePicker_time.setHour(hour);
        submit_task_deadlinePicker_time.setMinute(minute);
        submit_task_deadlinePicker_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                toastShow(Integer.toString(hourOfDay)+"点"
                               +Integer.toString(minute)+"分");
                SubmitTaskFragment.hour=hourOfDay;
                SubmitTaskFragment.minute=minute;
                setDateTime();
                int y=0;
            }
        });

    }

    public void setDateTime(){
        int monthTemp=month+1;
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = year+"-"+
                (monthTemp)+"-"+
                dayOfMonth+" "+
                hour+":"+
                minute+":"+
                "00";
        try{
            Date date = sdf.parse(dateTime);
            bombDate=new BmobDate(date);
        }catch (Exception e){
            Log.d("setTime", e.toString());;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void submit(){

        EditText submit_task_title=getView().findViewById(R.id.fb_bt);
        EditText submit_task_text=getView().findViewById(R.id.fb_text);
        EditText submit_task_OAR=getView().findViewById(R.id.fb_xs);
        DatePicker submit_task_deadlinePicker_date = getView().findViewById(R.id.fb_datepicker);
        TimePicker submit_task_deadlinePicker_time = getView().findViewById(R.id.fb_timepicker);
        Spinner submit_task_type_spinner = getView().findViewById(R.id.fb_lb);

//        String[] str = {"1","2","3"};
//        SpinnerAdapter spinnerAdapter = new SpinnerAdapter();
//        spinnerAdapter.getDropDownView()

        newTask.setType(submit_task_type_spinner.getSelectedItem().toString());


        newTask.setTitle(submit_task_title.getText().toString());
        newTask.setDescription(submit_task_text.getText().toString());
        newTask.setOAR(Integer.parseInt(submit_task_OAR.getText().toString()));
        newTask.setStatus("未接受");
        newTask.setPublisher(currentUser);
        //task.setType(submit_task_type_spinner.toString());

//        //task.setDeadline();
//        BmobFile picture = selectPicture();
//        if(picture.getFilename()!=null){
//            newTask.setPicture(picture);
//        }else{ newTask.setPicture(null);}

        newTask.setDeadline(bombDate);

        if(currentUser.getBalance().intValue()>=newTask.getOAR().intValue())//若余额>悬赏
        {
            currentUser.setBalance(currentUser.getBalance().intValue()-newTask.getOAR().intValue());
            newTask.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e!=null){
                        toastShow("发布失败");
                    }else{
                        toastShow("发布成功");
                    }
                }
            });
            currentUser.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e!=null){
                        toastShow("余额更新失败");
                    }else{

                    }
                }
            });
        }else{
            toastShow("余额不足");
        }
    }



    /*相册中选图片返回一个File*/
    private BmobFile selectPicture(){
        BmobFile picture = new BmobFile();
        return picture;
    }

    /*初始化当前用户*/
    private void initCurrentUser(){
        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId",currentUserObjectId);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e!=null){
                    Log.d("pullTaskList", "error");
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

    private Handler userListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<User> list = (List<User>) msg.obj;
                    currentUser = list.get(0);
                    break;
            }
        }
    };

    private void toastShow(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String img_url = uri.getPath();//这是本机的图片路径
                ContentResolver cr = mActivity.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ImageView imageView = mRoot.findViewById(R.id.fb_picture);
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
        String path = FileUtils.getPath(mActivity.getApplicationContext(),uri);

        BmobFile taskImage = new BmobFile(new File(path));
        taskImage.upload(new UploadFileListener() {
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
            newTask.setPicture(taskImage);
        }catch (Exception e){
            Log.d("setImage", e.toString());
        }
    }
}
