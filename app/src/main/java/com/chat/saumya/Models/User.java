package com.chat.saumya.Models;

/**
 * Created by rohit on 29-12-2016.
 */

public class User
{
    public String name="";
    public String email="";
    public String cno="";
    public String count="";
    public String uid="";
    public String picture_url="";
   public User()
    {

    }
    public User(String name,String email,String cno,String uid,String picture_url)
    {
        this.name=name;
        this.email=email;
        this.uid=uid;
        this.cno=cno;
        this.picture_url=picture_url;


        //this.mob_no=mob_no;
        //this.u_id=u_id;

    }
}
