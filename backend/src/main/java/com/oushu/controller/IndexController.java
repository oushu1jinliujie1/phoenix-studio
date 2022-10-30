package com.oushu.controller;

import com.oushu.model.Create2IdxRequest;
import com.oushu.model.LoginParam;
import com.oushu.model.ResponseModel;
import com.oushu.model.User;
import com.oushu.util.Util;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class IndexController {

    @PostMapping("/index/run")
    public ResponseModel runIndex(@RequestBody Create2IdxRequest param) throws Exception {
        ResponseModel responseModel = new ResponseModel();
        String runIndexShell = param.getRunIndexShell();
        Util.callScript(runIndexShell);
        return responseModel.success("正在创建索引");
    }
}
