package com.toyproject.scraping;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ApiResponse {
    private int code;
    private String message;
    private JSONArray data;

    public ApiResponse(int code, String message, JSONArray data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", this.code);
        jsonObject.put("message", this.message);
        jsonObject.put("data", this.data);
        return jsonObject;
    }
}
