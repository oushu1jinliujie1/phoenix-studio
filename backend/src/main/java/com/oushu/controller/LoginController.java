package com.oushu.controller;


import com.oushu.model.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/hello")
    public ResponseModel Hello(){
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success("你好");
    }
}
