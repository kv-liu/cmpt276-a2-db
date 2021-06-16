package com.example;

public class Input {
    private int id;
    private String name;
    private int width;
    private int height;
    private int radius;
    private String color;
    //getters
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRadius() {
        return this.radius;
    }

    public String getColor() {
        return this.color;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public void setRadius(int r) {
        this.radius = r;
    }

    public void setColor(String c) {
        this.color = c;
    }
}
