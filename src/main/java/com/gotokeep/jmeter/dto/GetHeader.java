package com.gotokeep.jmeter.dto;

import lombok.Data;

/**
 *
 * Created by zhaoqian on 2019/9/12.
 */
@Data
public class GetHeader {
    private String accept_encoding;
    private String connection;
    private String ua;
    private String x_abtest_tags;
    private String x_ads;
    private String x_app_platform;
    private String x_carrier;
    private String channel;
    private String x_connection_type;
    private String deviceId;
    private String x_geo;
    private String isnewdevice;
    private String timezone;
    private String locale;
    private String manufacturer;
    private String model;
    private String os;
    private String osversion;
    private String x_screen_height;
    private String x_screen_width;
    private String x_user_id;
    private String x_version_code;
    private String timestamp;
    private String keepversion;
    private String routekey;
}
