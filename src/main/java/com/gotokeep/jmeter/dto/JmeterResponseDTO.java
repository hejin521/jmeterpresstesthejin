package com.gotokeep.jmeter.dto;

import lombok.Data;

/**
 *
 * Created by zhaoqian on 2019/8/15.
 */
@Data
public class JmeterResponseDTO {
    private String responseCode;
    private boolean success;
    private String url;
    private long contenttime;
    private String response;
}
