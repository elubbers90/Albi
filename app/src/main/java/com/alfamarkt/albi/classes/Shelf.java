package com.alfamarkt.albi.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */

public class Shelf implements Comparable<Shelf>  {
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
            this.items=new ArrayList<Item>();
        }
        this.items.add(item);
    }
    public String toString(){
        String result = "{\"id\":";
        result+=id;
        result+=",\"number\":";
        result+=number;
        if(items!=null) {
            result += ",\"items\":[";
            for (int i = 0; i < items.size(); i++) {
                if (i != 0) {
                    result += ",";
                }
                result += items.get(i).toString();
            }
            result += "]";
        }
        result+="}";
        return result;
    }

    public static List<Shelf> jsonToShelves(JSONArray json){
        List<Shelf> shelves = new ArrayList<Shelf>();
        for(int i=0;i<json.length();i++) {
            Shelf shelf = new Shelf();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                shelf.setId(jsonObject.getInt("id"));
                shelf.setNumber(jsonObject.getInt("number"));
                shelf.setItems(Item.jsonToItems(jsonObject.getJSONArray("items")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            shelves.add(shelf);
        }
        return shelves;
    }

    public Item findItem(int sku){
        for(int i=0;i<items.size();i++){
            Item item = items.get(i);
            if(item.getSku() == sku){
                return item;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Shelf shelf) {
        return number > shelf.getNumber() ? +1 : number < shelf.getNumber() ? -1 : 0;
    }
}
