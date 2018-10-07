package com.example.renlvda.MyEye.entity;

import java.io.File;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by zhaoning on 2018/10/7.
 */

public class Picture extends BmobObject {
    private String username;
    private BmobFile image;
    private String description;
    private BmobDate date;
    private int status;

    public String getUsername() {
        return username;
    }

    public Picture setUsername(String username) {
        this.username = username;
        return this;
    }

    public BmobFile getImage() {
        return image;
    }

    public Picture setImage(BmobFile image) {
        this.image = image;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Picture setDescription(String description) {
        this.description = description;
        return this;
    }


    public BmobDate getDate() {
        return date;
    }

    public Picture setDate(BmobDate date) {
        this.date = date;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Picture setStatus(int status) {
        this.status = status;
        return this;
    }
}
