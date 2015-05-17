package com.alfamarkt.albi.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */

public class Shelf {
    public int id;
    public int number;
    public List<Item> items;

    public Shelf(int id, int number, List<Item> items) {
        this.id = id;
        this.number = number;
        this.items = items;
    }

    public Shelf(int id) {

        this.id = id;
    }

    public Shelf() {
    }

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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item){
        if(this.items==null){
            this.items=new ArrayList<>();
        }
        this.items.add(item);
    }
}
