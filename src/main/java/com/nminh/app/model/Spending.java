package com.nminh.app.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spending {
    private Integer id;
    @SerializedName("userId")
    private Integer userId;
    private String name;
    private String type;
    private String category;
    private int quantity;
    private float cost;
    private Long date;
    private String content;
}
