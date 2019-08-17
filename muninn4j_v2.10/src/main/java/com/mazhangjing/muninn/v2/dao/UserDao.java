package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.User;

public class UserDao {

    public User getUser(String name, String passWord) {
        if (passWord.equalsIgnoreCase("java")) return new User(name,name != null ? name.toUpperCase() : null,20,name+"@muninn.cn",passWord);
        else return null;
    }

}
