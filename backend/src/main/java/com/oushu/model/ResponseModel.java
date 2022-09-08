package com.oushu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ToString
@Setter
@Getter
public class ResponseModel {
    private static Logger logger = LoggerFactory.getLogger(ResponseModel.class);
    private static final String OK = "ok";
    private static final String ERROR = "error";

    private Meta meta;
    private Object data;   // response data

    public ResponseModel( ) {
        super();
    }

    public ResponseModel( Meta meta, Object data) {
        this.meta = meta;
        this.data = data;
    }

    public ResponseModel success() {
        this.meta = new Meta(true, OK,"");
        return this;
    }

    public ResponseModel success(Object data) {
        this.meta = new Meta(true, OK,"");
        this.data = data;
        return this;
    }

    public ResponseModel success(String message,String code){
        this.meta = new Meta(true, message, code);
        return this;
    }

    public ResponseModel success(String message,String code,Object data){
        this.meta = new Meta(true, message, code);
        this.data = data;
        return this;
    }

    public ResponseModel failure() {
        this.meta = new Meta(false, ERROR,"");
        return this;
    }

    public ResponseModel failure(String message) {
        this.meta = new Meta(false, message,"");
        return this;
    }

    public ResponseModel failure(String message,String code){
        this.meta = new Meta(false, message, code);
        return this;
    }

    public ResponseModel failureWithCode(String code) {
        this.meta = new Meta(false, "",code);
        return this;
    }

    @JsonIgnore
    public boolean isSuccessful(){
        return meta.success;
    }

    public Meta getMeta() {
        return meta;
    }

    public Object getData() {
        return data;
    }

    @Getter
    @Setter
    @ToString
    public class Meta {

        private boolean success;
        private String message;
        private String statusCode;
        public Meta() {
            super();
        }
        public Meta(boolean success, String message,String statusCode) {
            this.success = success;
            this.message = message;
            this.statusCode = statusCode;
        }
    }

    /**
     * This method transform a json string to a json object ResponseModel.
     * @param jsonStr the json string. e.g. {"meta":{"success":false,"message":"The task is not completed."},"data":null}
     * @return a ResponseModel object
     */
    public static <T> T stringToJson(String jsonStr, Class<T> tClass){
        T responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(jsonStr != null){
                responseModel = mapper.readValue(jsonStr, tClass);
            }

        } catch (IOException e) {
            logger.error("Error during parsing the ResponseModel Json:{}", e.getMessage());
            e.printStackTrace();
        }
        return responseModel;
    }
}
