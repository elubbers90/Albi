package com.alfamarkt.albi.classes;

import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */

public class StorePlanogram {
    public int id;
    public String location;
    public String classStore;
    public List<Rack> racks;

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

    public StorePlanogram() {

    }

    public StorePlanogram(int id, String location, String classStore, List<Rack> racks) {

        this.id = id;
        this.location = location;
        this.classStore = classStore;
        this.racks = racks;
    }

    public StorePlanogram(int id) {

        this.id = id;
    }
}
