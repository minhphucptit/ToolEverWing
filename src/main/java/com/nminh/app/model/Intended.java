package com.nminh.app.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Intended {
    private Integer id;
    @SerializedName("user_id")
    private Integer userId;
    private String name;
    private String status;
    private Integer quantity;
    private Float cost;
    private String content;
    @SerializedName("create_at")
    private Long createAt;
    @SerializedName("finish_at")
    private Long finishAt;
}
