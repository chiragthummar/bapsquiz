package com.bittechnologies.bapsquiz.model;

/**
 * Created by chira on 1/29/2016.
 */
public class Chapter {

    private int id;
    private String name;
    private  String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
