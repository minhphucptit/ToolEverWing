package com.nminh.app.service;

import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.util.Map;

public class IntendedService {
    public static HttpResponse<JsonNode> getListIntended(Map<String,Object> params){

        HttpRequest request = Unirest.request("GET","http://localhost:8080/v1/intended").header("Content-Type","application/json")
                .queryString(params);

        return request.asJson();
    }

    public static HttpResponse<JsonNode> createIntended(JSONObject data){

        HttpRequest request = Unirest.request("POST","http://localhost:8080/v1/intended").header("Content-Type","application/json")
                .body(data);

        return request.asJson();
    }

    public static HttpResponse<JsonNode> updateIntended(Integer id,JSONObject data){

        HttpRequest request = Unirest.request("PATCH","http://localhost:8080/v1/intended/{id}").header("Content-Type","application/json")
                .routeParam("id",Integer.toString(id)).body(data);

        return request.asJson();
    }

    public static HttpResponse<JsonNode> deleteIntended(Integer id){

        HttpRequest request = Unirest.request("DELETE","http://localhost:8080/v1/intended/{id}").header("Content-Type","application/json")
                .routeParam("id",Integer.toString(id));

        return request.asJson();
    }
}
