package com.gotokeep.jmeter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by zhaoqian on 19/4/12.
 */
public class PTDateUtil {
    //数据库存储时间
    public String dateDataNow() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return date.format(now);
    }

    //数据库存储日期
    public String dayDataNow() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        return date.format(now);
    }

    //token失效时间
    public static int jwtDate(int j) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, j);
        now= calendar.getTime();
        String s=simpleDateFormat.format(now);
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts=0;
        if(date!=null)
            ts= date.getTime() / 1000;
        res = String.valueOf(ts);
        return Integer.parseInt(res);
    }

    //当前时间戳
    public String dateTimstamp() {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String s=simpleDateFormat.format(now);
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts =0;
        if(date!=null)
            ts=date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    //比较当前日期是否大于等于之前日期
    public boolean compareDate(String previous,String current){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date previousDate=simpleDateFormat.parse(previous);
            Date currentDate=simpleDateFormat.parse(current);
            return currentDate.getTime()-previousDate.getTime()>=0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
