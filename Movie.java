package com.example.webserverapp;

import java.io.Serializable;

public class Movie implements Serializable {

    private String name, description, score, actors, image;
    private int id;

    Movie( int id, String name, String description, String score, String actors, String image) {
        setName(name);
        setDescription(description);
        setScore(score);
        setActors(actors);
        setId(id);
        setImage(image);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
