package com.gotokeep.jmeter.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by zhaoqian on 19/4/12.
 */
public class JWTUtil {
    private String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
        if (b != null) {
            //s = new BASE64Encoder().encode(b);
            s = Base64.getEncoder().encodeToString(b);
        }
        return s;
    }

    public String creatToken(String env, String uid, String username, String gender) {
        Map<String,Object> header=new HashMap<>();
        header.put("typ","JWT");
        header.put("alg","HS256");
        Map<String,Object>claims=new HashMap<>();
        claims.put("_id",uid);
        claims.put("username",username);
        claims.put("avatar","");
        claims.put("gender",gender);
        claims.put("deviceId","");
        claims.put("iss", "http://www.gotokeep.com/");
        claims.put("exp", PTDateUtil.jwtDate(300));
        claims.put("iat", PTDateUtil.jwtDate(0));
        String key="";
        if(env.equals("pre")){
            key= "dev-test";
        }else if(env.equals("online")){
            key="Si6Af6ghiSck7aK8";
        }
        JwtBuilder builder = Jwts.builder().setHeader(header).setClaims(claims).signWith(SignatureAlgorithm.HS256, this.getBase64(key));
        return builder.compact();
    }
}
