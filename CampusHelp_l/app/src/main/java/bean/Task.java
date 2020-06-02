package bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

public class Task extends BmobObject {
    private User accepter;//接受者Object<User>
    private Number OAR;//悬赏
    private BmobFile picture;//图片
    private  String description;
    private  String type;//类别
    private  String status;//状态
    private  String title;//标题
    private  String taskId;//任务ID
    private  User publisher;//发布者Object<User>
    private BmobDate deadline;//限期
    /*private  User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/

    public User getAccepter() {
        return accepter;
    }

    public void setAccepter(User accepter) {
        this.accepter = accepter;
    }

    public  Number getOAR(){
        return OAR;
    }

    public void setOAR(Number OAR) {
        this.OAR = OAR;
    }

    public BmobFile getPicture(){
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType(){
        return  type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String  getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String  getTaskId(){
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public BmobDate  getDeadline(){
        return deadline;
    }

    public void setDeadline(BmobDate deadline) {
        this.deadline = deadline;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }
}