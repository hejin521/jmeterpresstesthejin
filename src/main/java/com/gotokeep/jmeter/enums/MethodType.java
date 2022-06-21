package com.gotokeep.jmeter.enums;

/**
 *
 * Created by zhaoqian on 2019/4/29.
 */
public enum MethodType {
    POST("POST","POST"),
    GET("GET","GET"),
    GETBASICAUTH("GETBASICAUTH","Basic Auth"),
    DELETE("DELETE","DELETE"),
    PUT("PUT","PUT");


    MethodType(String iid, String idesc){
        id = iid;
        desc = idesc;
    }

    private String id;
    private String desc;

    public String getValue() {
        return id;
    }
    public String getDesc() {
        return desc;
    }
}
