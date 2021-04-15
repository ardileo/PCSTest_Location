package com.ardileo.pcstest.Model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Result {
    public UserData user;
    public RespError error;
    public List<LocModel> locations;

    public String toString() {
        return new Gson().toJson(this);
    }

    public static class RespError {
        public int code;
        public String message, title;

        public RespError(int code, String message, String title) {
            this.code = code;
            this.message = message;
            this.title = title;
        }

        public String getTitle() {
            return title != null ? title : "";
        }

        public String getMessages() {
            return message != null ? message : "";
        }

        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
