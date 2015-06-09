package com.alfamarkt.albi.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ansem on 7-6-2015.
 */
public class User {
    private int id;
    private String name;

    public User(int id,String name) {
        this.name = name;
        this.id = id;
    }

    public User() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        String result = "{\"id\":";
        result+=id;
        result+=",\"name\":\"";
        result+=name;
        result+="\"}";
        return result;
    }

    public static List<User> jsonToUsers(JSONArray json){
        List<User> users = new ArrayList<User>();
        for(int i=0;i<json.length();i++) {
            User user = new User();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                user.setId(jsonObject.getInt("id"));
                user.setName(jsonObject.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            users.add(user);
        }
        return users;
    }

}
