package com.nminh.app.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
      private int id;
      private String name;
      private String password;
      private String email;
      private float capital;
      @SerializedName("imgLink")
      private String imgLink;
}
