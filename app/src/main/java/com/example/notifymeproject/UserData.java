package com.example.notifymeproject;

public class UserData {
    public String usertype,userid,uname,uemail,udep,ucourse,usem,mob_pass;

    public UserData(){    }


    public UserData(String usertype, String userid, String uname, String uemail, String udep, String ucourse, String usem, String mob_pass) {
        this.usertype = usertype;
        this.userid = userid;
        this.uname = uname;
        this.uemail = uemail;
        this.udep = udep;
        this.ucourse = ucourse;
        this.usem = usem;
        this.mob_pass = mob_pass;
    }

    public String getUsertype() {
        return usertype;
    }

    public String getUserid(){
        return userid;
    }

    public String getUname() {
        return uname;
    }

    public String getUemail() {
        return uemail;
    }

    public String getUdep() {
        return udep;
    }

    public String getUcourse() {
        return ucourse;
    }

    public String getUsem() {
        return usem;
    }

    public String getMob_pass() {
        return mob_pass;
    }
}

