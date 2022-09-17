package com.oushu.controller;


import com.oushu.model.LoginParam;
import com.oushu.model.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

    @GetMapping("/hello")
    public ResponseModel Hello(){
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success("你好");
    }

    @GetMapping("/content")
    public ResponseModel content(){
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success("content");
    }

    @PostMapping("/login")
    public ResponseModel login(@RequestBody LoginParam param, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ResponseModel responseModel = new ResponseModel();
        //TODO 后续对接ranger
        if (param.getUserName().equals("oushu") && param.getPassword().equals("oushu")){
            HttpSession session = request.getSession();
            session.setAttribute("oushu", "oushu");
            return responseModel.success("登录成功");
        } else {
            return responseModel.success("登录失败");
        }
    }

    @PostMapping("/logout")
    public ResponseModel logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = request.getSession();
        session.invalidate();
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success("退出成功");
    }
}
