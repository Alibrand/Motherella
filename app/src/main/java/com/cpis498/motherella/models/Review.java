package com.cpis498.motherella.models;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.util.Date;
import java.util.Map;

public class Review {
    String uid;
    String id;
    String doctor_name;
    String doctor_specialitiy;
    String comments;
    float rating;
    Date review_date;

    public  Review(){}

    public Review(Map<String,Object> map){
        this.uid= (String) map.get("uid");
        this.doctor_name= (String) map.get("doctor_name");
        this.doctor_specialitiy=(String) map.get("doctor_specialitiy");
        double ratingLong= (double) map.get("rating");
        this.rating= (float) ratingLong;
        this.comments= (String) map.get("comments");
        this.id= (String) map.get("id");
    }
    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getReview_date() {
        return review_date;
    }

    public void setReview_date(Date review_date) {
        this.review_date = review_date;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_specialitiy() {
        return doctor_specialitiy;
    }

    public void setDoctor_specialitiy(String doctor_specialitiy) {
        this.doctor_specialitiy = doctor_specialitiy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
