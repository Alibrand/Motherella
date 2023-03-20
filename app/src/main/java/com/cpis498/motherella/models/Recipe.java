package com.cpis498.motherella.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Recipe  {
    String id;
    String title;
    String link;
    String trimester;
//    String image_url;
//    ArrayList<String> ingredients;
//    ArrayList<String> directions;
//    String size;
    List<String> favourites;

    public Recipe(Map<String,Object> map)
    {
        this.id= (String) map.get("id");
        this.title= (String) map.get("title");
//        this.image_url= (String) map.get("image_url");
//        this.ingredients= (ArrayList<String>) map.get("ingredients");
//        this.directions= (ArrayList<String>) map.get("directions");
//        this.size= (String) map.get("meal_size");
        this.link=(String) map.get("link");
        this.trimester=(String) map.get("trimester");
        this.favourites=new ArrayList<String>();
        if(map.get("favourites")!=null)
            this.favourites= (List<String>) map.get("favourites");
    }

    public Recipe(){}

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }
}
