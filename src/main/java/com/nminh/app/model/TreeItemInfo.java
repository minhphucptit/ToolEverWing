/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nminh.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author ubuntu
 */
@Data
@AllArgsConstructor
public class TreeItemInfo {
    private String displayText;
    
    private String screenName;
    
    private String styleClass;
}
