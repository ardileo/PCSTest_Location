package com.ardileo.pcstest.Model;

public class ResponseData {
    public int code;
    public String message;
    public Result result;


    public class Result {
        public UserData user;
    }
}
