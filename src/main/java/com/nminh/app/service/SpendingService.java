package com.nminh.app.service;

import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.util.Map;

public class SpendingService {

    public static HttpResponse<JsonNode> getListSpending(Map<String,Object> params){

        HttpRequest request = Unirest.request("GET","http://localhost:8080/v1/spending").header("Content-Type","application/json")
                .queryString(params);

        return request.asJson();
    }

    public static HttpResponse<JsonNode> createSpending(JSONObject data){

        HttpRequest request = Unirest.request("POST","http://localhost:8080/v1/spending").header("Content-Type","application/json")
                .body(data);

        return request.asJson();
    }

    public static HttpResponse<JsonNode> updateSpending(Integer id,JSONObject data){

        HttpRequest request = Unirest.request("PATCH","http://localhost:8080/v1/spending/{id}").header("Content-Type","application/json")
                .routeParam("id",Integer.toString(id)).body(data);

        return request.asJson();
    }

    public static HttpResponse<JsonNode> deleteSpending(Integer id){

        HttpRequest request = Unirest.request("DELETE","http://localhost:8080/v1/spending/{id}").header("Content-Type","application/json")
                .routeParam("id",Integer.toString(id));

        return request.asJson();
    }
}
