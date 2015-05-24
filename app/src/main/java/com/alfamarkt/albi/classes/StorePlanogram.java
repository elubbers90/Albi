package com.alfamarkt.albi.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */

public class StorePlanogram {
    public int id;
    public String location;
    public String classStore;
    public List<Rack> racks;
    public Boolean checked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getClassStore() {
        return classStore;
    }

    public void setClassStore(String classStore) {
        this.classStore = classStore;
    }

    public List<Rack> getRacks() {
        return racks;
    }

    public void setRacks(List<Rack> racks) {
        this.racks = racks;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public StorePlanogram() {

    }

    public StorePlanogram(int id, String location, String classStore, List<Rack> racks, Boolean checked) {

        this.id = id;
        this.location = location;
        this.classStore = classStore;
        this.racks = racks;
        this.checked=checked;
    }

    public StorePlanogram(int id) {

        this.id = id;
    }

    public String toString(){
        String result = "{\"id\":";
        result+=id;
        result+=",\"location\":\"";
        result+=location;
        result+="\",\"classStore\":\"";
        result+=classStore;
        result+="\",\"checked\":";
        result+=checked;
        result+=",\"racks\":[";
        for(int i=0;i<racks.size();i++){
            if(i!=0){
                result+=",";
            }
            result+=racks.get(i).toString();
        }
        result+="]}";
        return result;
    }

    public static StorePlanogram jsonToStore(JSONObject json){
        StorePlanogram storePlanogram = new StorePlanogram();
        try {
            storePlanogram.setId(json.getInt("id"));
            storePlanogram.setLocation(json.getString("location"));
            storePlanogram.setClassStore(json.getString("classStore"));
            storePlanogram.setRacks(Rack.jsonToRack(json.getJSONArray("racks")));
            storePlanogram.setChecked(json.getBoolean("checked"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return storePlanogram;
    }
}
