package com.cpis498.motherella.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Exercise {
    String id;
    String title;
    String video_url;
    String image_url;
    List<String> favourites;
    String trimester;

    public Exercise(Map<String,Object> map){
        this.id= (String) map.get("id");
        this.title= (String) map.get("title");
        this.video_url= (String) map.get("video_url");
        this.image_url= (String) map.get("image_url");
        this.favourites=new ArrayList<String>();
        if(map.get("favourites")!=null)
            this.favourites= (List<String>) map.get("favourites");
    }

    public  Exercise(){}

    public String getTrimester() {
        return trimester;
    }

    public void setTrimester(String trimester) {
        this.trimester = trimester;
    }

    public  void removeFavourite(String uid)
    {
        this.favourites.remove(uid);
    }

    public List<String> getFavourites() {
        return favourites;
    }



    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }

    public void addFavourite(String uid) {
        this.favourites.add(uid);
    }

    public boolean isFavourite(String uid){
        return this.favourites.indexOf(uid)>-1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
