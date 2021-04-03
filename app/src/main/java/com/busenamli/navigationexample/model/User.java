package com.busenamli.navigationexample.model;

public class User {

    private String name;
    private String city;
    private String description;
    private String image;

    public User(String name, String city, String description, String image) {
        this.name = name;
        this.city = city;
        this.description = description;
        this.image = image;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
