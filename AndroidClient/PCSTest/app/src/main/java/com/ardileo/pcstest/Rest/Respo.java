package com.ardileo.pcstest.Rest;

import com.ardileo.pcstest.Model.Result;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class Respo {
    private int code;
    private String message;
    private Result result;
    private String resultString;

    public Respo(Response<ResponseBody> response) {
        String contentType = response.headers().get("content-type");
        contentType = contentType.split(";")[0];
        code = response.code();
        result = new Result();

        if (contentType.equals("application/json")) {
            if (response.isSuccessful()) {
                try {
                    resultString = response.body().string();

                    Respo r = new Gson().fromJson(resultString, this.getClass());
                    result = r.result != null ? r.result : result;
                    message = r.message;
                } catch (IOException e) {
                    result.error = new Result.RespError(e.hashCode(), e.getMessage(), null);
                }
            } else {
                try {
                    resultString = response.errorBody().string();

                    Respo r = new Gson().fromJson(resultString, this.getClass());
                    result = r.result != null ? r.result : result;
                    message = r.message;
                    result.error = result.error != null ? result.error : new Result.RespError(code, response.message(), null);
                } catch (IOException e) {
                    result.error = new Result.RespError(500, "Something went wrong, cant access response from server", "Error");
                }
            }
        } else {
            result.error = new Result.RespError(500, "Something went wrong, cant access response from server", "Error");
            resultString = result.toString();
        }
    }

    public String getResultToString() {
        try {
            JSONObject job = new JSONObject(resultString);
            return job.getString("result");
        } catch (JSONException ignored) {
            return null;
        }
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public Result.RespError getError() {
        return this.result.error;
    }

    public Result getResult() {
        return result;
    }
}
