package com.example.campushelp_s;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campushelp_s.ViewModel.UserViewModel;

import java.util.List;

import bean.User;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;


public class UserAdapter extends RecyclerView.Adapter <UserAdapter.ViewHolder>{

    private List<User> mUserData;
    private Context mContext;
    private int resourceId;
    private View view;
    private User loginUser;
    private UserViewModel UserViewModel;
    public UserAdapter(User user,Context context, int resourceId, List<User> data){
        this.mContext=context;
        this.mUserData=data;
        this.resourceId=resourceId;
        this.loginUser = user;
    }

    public List<User> getTaskList(){
        return mUserData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(resourceId , parent , false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user_focus = mUserData.get(position);
        holder.tv_User_Name.setText(user_focus.getName());
        holder.tv_User_ID.setText(user_focus.getUserID());
        BmobFile icon = user_focus.getImage();
        if(icon!=null){
            icon.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e!=null){
                        Log.d("downloadIcon", e.toString());
                    }else{
                        holder.iv_Head.setImageBitmap(BitmapFactory.decodeFile(s));
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }else{
            holder.iv_Head.setImageResource(R.drawable.default_user_icon);
        }
        holder.btn_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btn_focus.setVisibility(View.INVISIBLE);
                BmobRelation bmobRelation = new BmobRelation();
                bmobRelation.remove(user_focus);
                loginUser.setFollow(bmobRelation);
                loginUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            Toast.makeText(mContext,"取消关注成功",Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(mContext,"取消关注失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_User_Name;
        TextView tv_User_ID;
        ImageView iv_Head;
        Button btn_focus;

        public ViewHolder(View view) {
            super(view);
            iv_Head = view.findViewById(R.id.gz_tx);
            tv_User_ID = view.findViewById(R.id.gz_id);
            tv_User_Name = view.findViewById(R.id.gz_nc);
            btn_focus = view.findViewById(R.id.gz_qg);

        }
    }
}
