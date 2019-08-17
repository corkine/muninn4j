package com.mazhangjing.muninn.v2.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class HandleAllException {

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleAllRuntimeException(RuntimeException ex) {
            ModelAndView model = new ModelAndView("error");
            model.addObject("err_info",ex);
            return model;
    }

}
