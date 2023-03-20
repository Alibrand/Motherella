package com.cpis498.motherella.models;

import java.util.HashMap;
import java.util.Map;

public class Reminder {
    String id;
    Map<String,Boolean> trimster1;
    Map<String,Boolean> trimster2;
    Map<String,Boolean> trimster3;
    String uid;

    public Reminder(Map<String,Object> map) {
        this.trimster1= (Map<String, Boolean>) map.get("trimester1");
        this.trimster2= (Map<String, Boolean>) map.get("trimester2");
        this.trimster3= (Map<String, Boolean>) map.get("trimester3");
        this.uid=(String)map.get("uid");
    }

    public Reminder(){
        Map<String,Boolean> trimester1=new HashMap<String,Boolean>();
        trimester1.put("folic_acid",false);
        trimester1.put("vitamin_b12",false);
        trimester1.put("ultra_sonic",false);
        Map<String,Boolean> trimester2=new HashMap<String,Boolean>();
        trimester2.put("glucose_test",false);
        trimester2.put("supplements",false);
        trimester2.put("ultra_sonic",false);
        Map<String,Boolean> trimester3=new HashMap<String,Boolean>();
        trimester3.put("glucose_test",false);
        trimester3.put("supplements",false);
        trimester3.put("vaccine",false);
        this.trimster1=trimester1;
        this.trimster2=trimester2;
        this.trimster3=trimester3;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map=new HashMap<>();
        map.put("trimester1",this.trimster1);
        map.put("trimester2",this.trimster2);
        map.put("trimester3",this.trimster3);
        map.put("uid",this.uid);
        return map;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Boolean> getTrimster1() {
        return trimster1;
    }

    public void setTrimster1(Map<String, Boolean> trimster1) {
        this.trimster1 = trimster1;
    }

    public Map<String, Boolean> getTrimster2() {
        return trimster2;
    }

    public void setTrimster2(Map<String, Boolean> trimster2) {
        this.trimster2 = trimster2;
    }

    public Map<String, Boolean> getTrimster3() {
        return trimster3;
    }

    public void setTrimster3(Map<String, Boolean> trimster3) {
        this.trimster3 = trimster3;
    }
}
