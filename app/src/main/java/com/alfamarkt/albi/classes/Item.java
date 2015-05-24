package com.alfamarkt.albi.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erik on 5/7/2015.
 */
public class Item {
    public int id;
    public int hole;
    public int subDept;
    public String position;
    public int noUrut;
    public int sku;
    public String description;
    public int tierKK;
    public int tierDB;
    public int tierAB;
    public int capacity;
    public int minDisplay;
    public int stock;
    public String tag;
    public String cls;
    public Boolean checked=false;
    public Boolean correct=false;

    public Item() {
    }

    public Item(int id) {

        this.id = id;
    }

    public Item(int id, int hole, int subDept, String position, int noUrut, int sku, String description, int tierKK, int tierDB, int tierAB, int capacity, int minDisplay, int stock, String tag, String cls, boolean checked, Boolean correct) {
        this.id = id;
        this.hole = hole;
        this.subDept = subDept;
        this.position = position;
        this.noUrut = noUrut;
        this.sku = sku;
        this.description = description;
        this.tierKK = tierKK;
        this.tierDB = tierDB;
        this.tierAB = tierAB;
        this.capacity = capacity;
        this.minDisplay = minDisplay;
        this.stock = stock;
        this.tag = tag;
        this.cls = cls;
        this.checked = checked;
        this.correct = correct;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHole() {
        return hole;
    }

    public void setHole(int hole) {
        this.hole = hole;
    }

    public int getSubDept() {
        return subDept;
    }

    public void setSubDept(int subDept) {
        this.subDept = subDept;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getNoUrut() {
        return noUrut;
    }

    public void setNoUrut(int noUrut) {
        this.noUrut = noUrut;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTierKK() {
        return tierKK;
    }

    public void setTierKK(int tierKK) {
        this.tierKK = tierKK;
    }

    public int getTierDB() {
        return tierDB;
    }

    public void setTierDB(int tierDB) {
        this.tierDB = tierDB;
    }

    public int getTierAB() {
        return tierAB;
    }

    public void setTierAB(int tierAB) {
        this.tierAB = tierAB;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMinDisplay() {
        return minDisplay;
    }

    public void setMinDisplay(int minDisplay) {
        this.minDisplay = minDisplay;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String toString(){
        String result = "{\"id\":";
        result+=id;
        result+=",\"hole\":";
        result+=hole;
        result+=",\"subDept\":";
        result+=subDept;
        result+=",\"position\":\"";
        result+=position;
        result+="\",\"noUrut\":";
        result+=noUrut;
        result+=",\"sku\":";
        result+=sku;
        result+=",\"description\":\"";
        result+=description.replace("\"","\'");
        result+="\",\"tierKK\":";
        result+=tierKK;
        result+=",\"tierDB\":";
        result+=tierDB;
        result+=",\"tierAB\":";
        result+=tierAB;
        result+=",\"capacity\":";
        result+=capacity;
        result+=",\"minDisplay\":";
        result+=minDisplay;
        result+=",\"stock\":\"";
        result+=stock;
        result+="\",\"tag\":\"";
        result+=tag;
        result+="\",\"cls\":\"";
        result+=cls;
        result+="\",\"checked\":";
        result+=checked;
        result+=",\"correct\":";
        result+=correct;
        result+="}";
        return result;
    }

    public static List<Item> jsonToItems(JSONArray json){
        List<Item> items = new ArrayList<Item>();
        for(int i=0;i<json.length();i++) {
            Item item = new Item();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                item.setId(jsonObject.getInt("id"));
                item.setHole(jsonObject.getInt("hole"));
                item.setSubDept(jsonObject.getInt("subDept"));
                item.setPosition(jsonObject.getString("position"));
                item.setNoUrut(jsonObject.getInt("noUrut"));
                item.setSku(jsonObject.getInt("sku"));
                item.setDescription(jsonObject.getString("description"));
                item.setTierKK(jsonObject.getInt("tierKK"));
                item.setTierDB(jsonObject.getInt("tierDB"));
                item.setTierAB(jsonObject.getInt("tierAB"));
                item.setCapacity(jsonObject.getInt("capacity"));
                item.setMinDisplay(jsonObject.getInt("minDisplay"));
                item.setStock(jsonObject.getInt("stock"));
                item.setTag(jsonObject.getString("tag"));
                item.setCls(jsonObject.getString("cls"));
                item.setChecked(jsonObject.getBoolean("checked"));
                item.setCorrect(jsonObject.getBoolean("correct"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items.add(item);
        }
        return items;
    }
}
