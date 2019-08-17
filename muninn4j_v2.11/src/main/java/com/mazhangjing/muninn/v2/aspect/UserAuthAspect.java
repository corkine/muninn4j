package com.mazhangjing.muninn.v2.aspect;

import com.mazhangjing.muninn.v2.annotation.NeedSessionUser;
import com.mazhangjing.muninn.v2.entry.User;
import com.mazhangjing.muninn.v2.entry.UserType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

@Component @Aspect
public class UserAuthAspect {

    @Pointcut("@annotation(com.mazhangjing.muninn.v2.annotation.NeedSessionUser)")
    public void userAnnotation() {}

    @Around("userAnnotation()")
    public Object checkAuth(ProceedingJoinPoint point) {
        Object result = null;
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        NeedSessionUser annotation = method.getAnnotation(NeedSessionUser.class);
        UserType value = annotation.value();
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        Boolean canGo = Optional.ofNullable(user)
                .map(use -> use.getUserType().equals(value)).orElse(false);

        if (canGo) {
            try {
                result = point.proceed();
            } catch (Throwable throwable) { throwable.printStackTrace(); }
        } else {
            result = "redirect:/login?user_callback=no_login";
        }

        return result;
    }


}
