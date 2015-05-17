package com.alfamarkt.albi.classes;

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

    public Item() {
    }

    public Item(int id) {

        this.id = id;
    }

    public Item(int id, int hole, int subDept, String position, int noUrut, int sku, String description, int tierKK, int tierDB, int tierAB, int capacity, int minDisplay, int stock, String tag, String cls) {
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

    public int tierAB;
    public int capacity;
    public int minDisplay;
    public int stock;
    public String tag;
    public String cls;
}
