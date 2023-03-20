package com.cpis498.motherella.models;

import android.text.format.DateFormat;


import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Pregnant {
    String id = "";
    String fullname;
    String email;
    String password;
    int age;
    int trimester = 1;
    Date dueDate;
    int cycleLength = 28;
    int pregnancyWeek;
    int remainigWeeks;
    int pregnanyDays;
    int reminingDays;

    Date lmp;
    int REGULAR_PERIOD = 28;
    int INTERVAL_YEARS = 1;
    int INTERVAL_MONTH = 3;
    int INTERVAL_DAYS = 7;


    public Pregnant() {
    }

    public Pregnant(Map<String,Object>map){
        this.id= (String) map.get("id");
        this.fullname=(String) map.get("fullname");
        Long ageLong= (Long) map.get("age");
        this.age=  ageLong.intValue();
        Long cycleLong= (Long) map.get("cycle_length");
        this.cycleLength=  cycleLong.intValue();
        Timestamp timestamp=(Timestamp) map.get("lmp");
        this.lmp= timestamp.toDate();


    }



    public void calculatePregnancyInfo() {
        calculateDueDate();
        calculateWeek();
        calculateTrimester();
    }

    public void calculateWeek() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        long diffInMillies = Math.abs(now.getTime() - lmp.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        this.pregnanyDays = (int) diffInDays;
        this.pregnancyWeek = (int) Math.ceil(this.pregnanyDays / 7);
        //get remainigs
        long remainingInMillies = Math.abs(dueDate.getTime() - now.getTime());
        long remainingInDays = TimeUnit.DAYS.convert(remainingInMillies, TimeUnit.MILLISECONDS);
        this.reminingDays = (int) remainingInDays;
        this.remainigWeeks = (int) Math.floor(this.pregnanyDays / 7);


    }

    private void calculateDueDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lmp);
        //get difference of irregular periods
        int diffInDays = this.cycleLength - REGULAR_PERIOD;
        calendar.add(Calendar.DATE, diffInDays);
        calendar.add(Calendar.DATE, INTERVAL_DAYS);
        calendar.add(Calendar.MONTH, -INTERVAL_MONTH);
        calendar.add(Calendar.YEAR, INTERVAL_YEARS);
        this.dueDate = calendar.getTime();
    }

    private void calculateTrimester() {
        if (this.pregnancyWeek <= 13)
            this.trimester = 1;
        else if (this.pregnancyWeek <= 26)
            this.trimester = 2;
        else
            this.trimester = 3;
    }

    public String getTrimesterLabel(){
        if(this.trimester==1)
            return  "الأول";
        else if(this.trimester==2)
            return  "الثاني";
        else return "الأخير";

    }

    public void  setId(String id){
        this.id=id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(int cycleLength) {
        this.cycleLength = cycleLength;
    }

    public int getPregnancyWeek() {
        return pregnancyWeek;
    }

    public int getPregnanyDays() {
        return pregnanyDays;
    }

    public Date getLmp() {
        return lmp;
    }

    public void setLmp(Date lmp) {
        this.lmp = lmp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getTrimester() {
        return trimester;
    }

    public void setTrimester(int trimester) {
        this.trimester = trimester;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", this.id);
        map.put("fullname", this.fullname);
        map.put("age", this.age);
        map.put("lmp", this.lmp);
        map.put("cycle_length",this.cycleLength);
        return map;
    }
}
