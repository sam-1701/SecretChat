package com.chat.saumya.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by rohit on 13-01-2017.
 */

public class Chat {

   public  String authorid="";
    public  String msg="";
    public String timestamp="";
    public String picurl="";
    public String name="";
    public boolean ismine=false;
    public Chat(){

    }
    public Chat(String timestamp,String authorid,String msg,boolean ismine ){




        this.timestamp=timestamp;
        this.authorid=authorid;
        this.msg=msg;
        this.ismine=ismine;

    }
    @Exclude
    public Map<String,Object>tomap(){

        HashMap<String,Object>result=new HashMap<String, Object>();
        result.put("msg", msg);
        result.put("timestamp",timestamp);
        result.put("authorid",authorid);
        result.put("ismine",ismine);
        return result;


    }




}
