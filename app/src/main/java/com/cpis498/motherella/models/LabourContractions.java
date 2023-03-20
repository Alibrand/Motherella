package com.cpis498.motherella.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LabourContractions {
    String uid;
    List<Contraction> contractions;
    List<Integer> intervals;
    boolean active =false;
    Date labor_date;
    int ACTIVE_CONTRACTION_DURATION=30;//seconds
    int ACTIVE_CONTRACTION_INTERVAL=240;//240 seconds =4 minutes
    int DURATION_TOLERANCE=10;
    int INTERVAL_TOLERANCE=5;

    public LabourContractions(Map<String,Object> map) {
        this.active=(boolean) map.get("active");
        Timestamp timestamp=(Timestamp) map.get("labor_date");
        this.labor_date=timestamp.toDate();
        this.contractions=new  ArrayList<Contraction>();
        this.intervals=new  ArrayList<Integer>();
        for (Map<String,Object> c:
                (List<Map<String,Object>>)  map.get("contractions")) {
            Contraction contraction=new Contraction(c);
            this.addContraction(contraction);

        }

    }

    public LabourContractions(Date date){
        this.labor_date=date;
        this.contractions=new  ArrayList<Contraction>();
        this.intervals=new  ArrayList<Integer>();
    }

    public void addContraction(Contraction contraction){
        if(this.contractions.size()>0){
            Contraction lastContraction=this.contractions.get(this.contractions.size()-1);
            long intervalinMili=contraction.start_time.getTime()-lastContraction.end_time.getTime();
            long diffInSeconds = TimeUnit.SECONDS.convert(intervalinMili/1000, TimeUnit.SECONDS);
            this.intervals.add((int)diffInSeconds);

        }
        this.contractions.add(contraction);
        //check contractions type after each contraction
        this.checkStatus();

    }


    public void checkStatus(){
        int contractionsCount=this.contractions.size();
        int intervalsCount=this.intervals.size();
        if(intervalsCount==0)
        {
            this.active=false;
        }
        else{
            int contractionSum=0;
            for(int i=0;i<contractionsCount;i++)
            {
                contractionSum+=this.contractions.get(i).getDuration();
            }
            int averageDuration=(int) contractionSum/contractionsCount;
            //get average
            int intervalsSum=0;
            for(int i=0;i<intervalsCount;i++)
            {
                intervalsSum+=this.intervals.get(i);
            }
            int averageInterval=(int) intervalsSum/intervalsCount;
            //check results
            //if contractions less than 4
            if(this.contractions.size()<4)
            { this.active=false;
                return;
            }
            boolean fixedDuration=false,fixedInterval=false;
            int lastContraction=this.contractions.get(this.contractions.size()-1).getDuration();
            int lastInterval=this.intervals.get(this.intervals.size()-1);

            if((lastContraction>=averageDuration && lastContraction<=averageDuration+DURATION_TOLERANCE)
            ||(lastContraction<=averageDuration && lastContraction>=averageDuration-DURATION_TOLERANCE) )
                fixedDuration=true;
            if((lastInterval<=averageInterval && lastInterval>=averageInterval-INTERVAL_TOLERANCE)
            ||(lastInterval>=averageInterval && lastInterval<=averageInterval+INTERVAL_TOLERANCE))
                fixedInterval=true;



            if(averageDuration>=ACTIVE_CONTRACTION_DURATION
            && averageInterval<=ACTIVE_CONTRACTION_INTERVAL &&  fixedInterval &&fixedDuration )
                this.active=true;
            else
                this.active=false;

        }

    }

    public  void clear(){
        this.contractions=new ArrayList<Contraction>();
        this.intervals=new ArrayList<Integer>();
    }

    public String toString(){
        String str="";
        for(int i=0;i<this.contractions.size();i++)
        {
            str+= (i + 1) +"# "+this.contractions.get(i).durationString();
            if(this.intervals.size()>0 && i<this.intervals.size())
                str+=" - "+ intervalString(this.intervals.get(i));
            str+="\n";
        }
        return str;
    }


    public String intervalString(int interval){
            int minutes=(int) interval/60;
            int seconds=interval % 60;
            return String.format("%02d:%02d",minutes,seconds);

    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Contraction> getContractions() {
        return contractions;
    }

    public void setContractions(List<Contraction> contractions) {
        this.contractions = contractions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLabor_date() {
        return labor_date;
    }

    public void setLabor_date(Date labor_date) {
        this.labor_date = labor_date;
    }
}
