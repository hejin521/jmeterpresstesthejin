package com.gotokeep.jmeter.dto;

import lombok.Data;

import java.util.Map;

/**
 *
 * Created by zhaoqian on 2019/4/29.
 */
@Data
public class HttpClientParamDTO {
    private String env;
    private String type;
    private String url;
    //curl -u username:
    private String userId;
    private String postBodyJson;
    private Map<String,String> postBodyForm;
    private GetHeader header;
    private String domain;
    private String XTOKENID;
}
