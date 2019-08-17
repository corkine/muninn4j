package com.mazhangjing.muninn.v2.entry;

import java.util.Objects;

public class UserToken {

    private String name;
    private String email;
    private String passWord;

    @Override
    public String toString() {
        return "UserToken{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return Objects.equals(name, userToken.name) &&
                Objects.equals(email, userToken.email) &&
                Objects.equals(passWord, userToken.passWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, passWord);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public UserToken() {
    }
}
