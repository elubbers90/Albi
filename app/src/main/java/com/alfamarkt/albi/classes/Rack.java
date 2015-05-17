package com.alfamarkt.albi.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */
public class Rack {
    public int id;
    public int number;
    public String type;
    public List<Shelf> shelves;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Shelf> getShelves() {
        return shelves;
    }

    public void setShelves(List<Shelf> shelves) {
        this.shelves = shelves;
    }

    public Rack() {

    }

    public Rack(int id, int number, String type, List<Shelf> shelves) {

        this.id = id;
        this.number = number;
        this.type = type;
        this.shelves = shelves;
    }

    public Rack(int id) {

        this.id = id;
    }

    public void addShelf(Shelf shelf) {
        if(this.shelves==null){
            this.shelves=new ArrayList<>();
        }
        this.shelves.add(shelf);
    }
}
