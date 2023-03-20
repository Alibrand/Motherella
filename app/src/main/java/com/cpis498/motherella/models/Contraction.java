package com.cpis498.motherella.models;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Contraction {
    Date start_time;
    Date end_time;
    int duration;

    public Contraction(Map<String,Object> map){
        Long durationLong= (Long) map.get("duration");
        this.duration=durationLong.intValue();
        Timestamp timestampstart= (Timestamp) map.get("start_time");
        this.start_time=timestampstart.toDate();
        Timestamp timestampend=(Timestamp)map.get("end_time");
        this.end_time=timestampend.toDate();
    }



public Contraction(){}

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
        long diff=this.end_time.getTime()- this.start_time.getTime();
        long diffInSeconds = TimeUnit.SECONDS.convert(diff/1000, TimeUnit.SECONDS);
        this.duration=(int)diffInSeconds;

    }

    public int getDuration() {
        return duration;
    }



    public void setDuration(int duration) {
        this.duration = duration;
    }
    //get duration formatted as 00:00
    public String durationString(){
     int minutes=(int) duration/60;
     int seconds=duration % 60;
     return String.format("%02d:%02d",minutes,seconds);

    }
}
