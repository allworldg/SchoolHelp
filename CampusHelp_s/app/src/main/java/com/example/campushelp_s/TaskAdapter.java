package com.example.campushelp_s;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import bean.Task;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class TaskAdapter extends RecyclerView.Adapter <TaskAdapter.ViewHolder>{

    private List<Task> mTaskData;
    private Context mContext;
    private int resourceId;

    private OnRecyclerViewItemClickListener myClickItemListener;// 声明自定义的接口

    /**
     * 定义public方法用以将接口暴露给外部
     * @param listener
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.myClickItemListener = listener;
    }

    /**
     * 自定义接口
     */
    public interface OnRecyclerViewItemClickListener {
        public void onItemClick(View view, int postion,Task task);
    }

    public TaskAdapter(Context context,int resourceId,List<Task> data){
        this.mContext=context;
        this.mTaskData=data;
        this.resourceId=resourceId;
    }

    public List<Task> getTaskList(){
        return mTaskData;
    }

 @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        View view = LayoutInflater.from(mContext).inflate(resourceId , parent , false);

        ViewHolder holder = new ViewHolder(view,myClickItemListener);

        return holder;
    }

@Override
public void onBindViewHolder(ViewHolder holder , int position) {
    Task task = mTaskData.get(position);
       holder.tv_Task_Title.setText("任务标题："+task.getTitle());
       holder.tv_Task_Status.setText(task.getStatus());
       holder.tv_Task_OAR.setText(task.getOAR().toString()+"U币");
       holder.tv_left_time.setText(task.getDeadline().toString());
       BmobDate deadLine = task.getDeadline();
       Calendar now = Calendar.getInstance();
       Date dateNow = new Date(now.YEAR,now.MONTH,now.DAY_OF_MONTH,now.HOUR,now.MINUTE);
       BmobDate timeNow = new BmobDate(dateNow);
       String str = deadLine.getDate();
       holder.tv_left_time.setText(str);

       //holder.tv_Task_Description.setText("任务描述："+task.getDescription());

    /*查询当前操作任务发布者 */
    String publisherObjecId = task.getPublisher().getObjectId();
    BmobQuery<User> publisherQuery = new BmobQuery<>();
    publisherQuery.addWhereEqualTo("objectId",publisherObjecId);
    publisherQuery.findObjects(new FindListener<User>() {
        @Override
        public void done(List<User> list, BmobException e) {
            if (e != null) {
                //toastShow(e.toString());
            } else if(list.size()==0) {

            }else{

                holder.tv_task_PublisherName.setText(list.get(0).getName());
                holder.tv_task_PublisherId.setText(list.get(0).getUserID());
                BmobFile icon = list.get(0).getImage();
                if(icon!=null){
                    icon.download(new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e!=null){
                                Log.d("downloadIcon", "done: ");
                            }else{
                                holder.iv_user_Icon.setImageBitmap(BitmapFactory.decodeFile(s));
                            }
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });
                }
                //tv_task_publisherName.setText("发布者："+list.get(0).getName());
            }
        }
    });

    publisherQuery.findObjects(new FindListener<User>() {
        @Override
        public void done(List<User> list, BmobException e) {
            if (e != null) {
                //toastShow(e.toString());
            } else {

                holder.tv_task_PublisherName.setText(list.get(0).getName());
                holder.tv_task_PublisherId.setText(list.get(0).getUserID());
                BmobFile icon = list.get(0).getImage();
                if(icon!=null){
                    icon.download(new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e!=null){
                                Log.d("downloadIcon", e.toString());
                            }else{
                                holder.iv_user_Icon.setImageBitmap(BitmapFactory.decodeFile(s));
                            }
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });
                }else{
                    holder.iv_user_Icon.setImageResource(R.drawable.default_user_icon);
                }


                //tv_task_publisherName.setText("发布者："+list.get(0).getName());
            }
        }
    });
       holder.task=task;
//       if (news.getImageId() != -1) {
//        holder.ivImage.setImageResource(news.getImageId());
//           }
        }

    @Override
    public int getItemCount() {
       return mTaskData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Task task;
        TextView tv_Task_Title;
        TextView tv_Task_Status;
        TextView tv_Task_OAR;
        //TextView tv_Task_Description;
        TextView tv_task_PublisherName;
        TextView tv_task_PublisherId;
        TextView tv_deadline;
        ImageView iv_user_Icon;

        TextView tv_left_time;

        ImageView ivImage;
        private OnRecyclerViewItemClickListener mListener;// 声明自定义的接口

        public ViewHolder(View view,OnRecyclerViewItemClickListener mListener) {
            super(view);
            itemView.setOnClickListener(this);
            this.mListener = mListener;

            tv_Task_Title = view.findViewById(R.id.rwx_wz);
            tv_Task_Status = view.findViewById(R.id.rwx_zt);
            tv_Task_OAR = view.findViewById(R.id.rwx_xs);
            //tv_Task_Description=view.findViewById(R.id.ls_tv_task_description);
            tv_task_PublisherName=view.findViewById(R.id.rwx_nc);
            tv_task_PublisherId=view.findViewById(R.id.rwx_id);
            iv_user_Icon=view.findViewById(R.id.rwx_tx);
            tv_left_time = view.findViewById(R.id.rwx_sytime);
        }

        @Override
        public void onClick(View v) {
            //getAdapterPosition()为Viewholder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去
            mListener.onItemClick(v, getAdapterPosition(),this.task);
        }
    }
}
