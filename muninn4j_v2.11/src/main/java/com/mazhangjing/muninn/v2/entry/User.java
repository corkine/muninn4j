package com.mazhangjing.muninn.v2.entry;

import java.util.Objects;

public class User {

    private String name;
    private String nickName;
    private Integer age;
    private String email;
    private String passWord;
    private UserType userType = UserType.UNKNOWN;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(nickName, user.nickName) &&
                Objects.equals(age, user.age) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nickName, age, email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public User(String name, String nickName, Integer age, String email, String passWord) {
        this.name = name;
        this.nickName = nickName;
        this.age = age;
        this.email = email;
        this.passWord = passWord;
    }

    public User() {
    }
}
