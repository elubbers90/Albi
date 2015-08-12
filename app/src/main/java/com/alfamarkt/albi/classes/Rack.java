package com.alfamarkt.albi.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */
public class Rack implements Comparable<Rack>{
    public int id;
    public int number;
    public String type;
    public List<Shelf> shelves;
    public Boolean checked;

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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Rack() {

    }

    public Rack(int id, int number, String type, List<Shelf> shelves, Boolean checked) {

        this.id = id;
        this.number = number;
        this.type = type;
        this.shelves = shelves;
        this.checked=checked;
    }

    public Rack(int id) {

        this.id = id;
    }

    public void addShelf(Shelf shelf) {
        if(this.shelves==null){
            this.shelves=new ArrayList<Shelf>();
        }
        this.shelves.add(shelf);
    }

    public String toString(){
        String result = "{\"id\":";
        result+=id;
        result+=",\"number\":";
        result+=number;
        result+=",\"type\":\"";
        result+=type;
        result+="\",\"checked\":";
        result+=checked;
        result+=",\"shelves\":[";
        for(int i=0;i<shelves.size();i++){
            if(i!=0){
                result+=",";
            }
            result+=shelves.get(i).toString();
        }
        result+="]}";
        return result;
    }

    public static List<Rack> jsonToRack(JSONArray json){
        List<Rack> racks = new ArrayList<Rack>();
        for(int i=0;i<json.length();i++) {
            Rack rack = new Rack();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                rack.setId(jsonObject.getInt("id"));
                rack.setNumber(jsonObject.getInt("number"));
                rack.setType(jsonObject.getString("type"));
                rack.setShelves(Shelf.jsonToShelves(jsonObject.getJSONArray("shelves")));
                rack.setChecked(jsonObject.getBoolean("checked"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            racks.add(rack);
        }
        return racks;
    }

    public Item findItem(int sku){
        for(int i=0;i<shelves.size();i++){
            Item item = shelves.get(i).findItem(sku);
            if(item!=null){
                return item;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Rack rack) {
        return number > rack.getNumber() ? +1 : number < rack.getNumber() ? -1 : 0;
    }
}
