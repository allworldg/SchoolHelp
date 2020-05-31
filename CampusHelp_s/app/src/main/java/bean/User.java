package bean;

import androidx.annotation.Nullable;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobObject implements Serializable {

    public User(){}
    @Nullable
    public String getUserID() {
        return userID;
    }

    public void setUserID(@Nullable String userID) {
        this.userID = userID;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    public BmobRelation getFollow() {
        return follow;
    }

    public User setFollow(BmobRelation follow) {
        this.follow = follow;
        return this;
    }

    public BmobRelation getCollection() {
        return collection;
    }

    public User setCollection(BmobRelation collection) {
        this.collection = collection;
        return this;
    }

    public BmobRelation getFans() {
        return fans;
    }

    public void setFans(BmobRelation fans) {
        this.fans = fans;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public BmobFile getImage() {
        return image;
    }

    public User setImage(BmobFile image) {
        this.image = image;
        return this;
    }

    public Number getDoneNumber() {
        return doneNumber;
    }

    public void setDoneNumber(Number doneNumber) {
        this.doneNumber = doneNumber;
    }

    public Number getBalance() {
        return balance;
    }

    public void setBalance(Number balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getSex() {
        return sex;
    }

    public void setSex(@Nullable String sex) {
        this.sex = sex;
    }

    @Nullable
    private String userID;      //用户ID
    @Nullable
    private String password;    //密码
    private BmobRelation follow; //关注的用户
    private BmobRelation collection; //收藏的任务
    private BmobRelation fans;   //粉丝
    private String Info;        //个人简介
    private BmobFile image;         //头像
    private Number doneNumber;  //完成任务数量
    private Number balance;     //余额
    private String email;       //邮箱
    private String phone;       //电话
    @Nullable
    private String name;        //用户名
    @Nullable
    private String sex;         //性别
}


