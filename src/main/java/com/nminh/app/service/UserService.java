package com.nminh.app.service;

import com.nminh.app.model.User;
import kong.unirest.*;
import kong.unirest.json.JSONElement;
import kong.unirest.json.JSONObject;

public class UserService {

    public static HttpResponse<JsonNode> check(String email, String password){
        return Unirest.request("POST","http://localhost:8080/v1/user").header("Content-Type","application/json")
                .body(new JSONObject().put("email",email).put("password",password)).asJson();
    }
}
