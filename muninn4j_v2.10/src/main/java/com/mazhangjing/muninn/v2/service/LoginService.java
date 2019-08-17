package com.mazhangjing.muninn.v2.service;

import com.mazhangjing.muninn.v2.dao.UserDao;
import com.mazhangjing.muninn.v2.entry.User;
import com.mazhangjing.muninn.v2.entry.UserToken;
import com.mazhangjing.muninn.v2.entry.UserType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class LoginService {

    private UserDao dao = new UserDao();


    public User login(UserToken token) {
        return Optional.ofNullable(token).filter(toke -> toke.getPassWord() != null && toke.getName() != null)
                .map(tok -> dao.getUser(tok.getName(), tok.getPassWord())).orElse(null);
    }

    public boolean loginAndRemember(UserToken token, HttpSession session) {
        return Optional.ofNullable(login(token)).map(user -> {
            user.setUserType(UserType.USER);
            session.setAttribute("user", user);
            return true;
        }).orElse(false);
    }


    public void logout(User user, HttpSession session, SessionStatus status) {
        session.setAttribute("user",null);
        session.removeAttribute("user");
        session.invalidate();
        status.setComplete();
    }


}
