
package com.alfamarkt.albi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.Shelf;
import com.alfamarkt.albi.classes.StorePlanogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jxl.*;
import jxl.biff.IntegerHelper;
import jxl.read.biff.BiffException;


public class MainActivity extends Activity{
    private StorePlanogram store;
    private HashMap<Integer,Integer> inventory;
    private String storeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            List<String> inventoryList = loadInventory("inventory.txt");
            inventory = parseInventory(inventoryList);
            List<List<String>> result = loadXML("planogram.xls");
            store = parseXML(result);
            storeString = store.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StorePlanogram parseXML(List<List<String>> xml){
        StorePlanogram store = new StorePlanogram(0);
        store.setChecked(false);
        List<Rack> racks = new ArrayList<Rack>();
        List<Shelf> shelves = new ArrayList<Shelf>();
        List<Item> items = new ArrayList<Item>();
        for(int i=0;i<xml.size();i++){
            List<String> row = xml.get(i);
            String firstCell = row.get(0);
            String secondCell = row.get(1);
            if(firstCell.equals("PLANOGRAM")) {
                store.setLocation(parseStoreLocation(row));
            } else if(firstCell.contains("Class Store")){
                store.setClassStore(parseClassStore(firstCell));
            } else if(firstCell.contains("Nomor Rak")){
                racks.add(parseRack(firstCell, racks.size()));
            } else if(!firstCell.equals("") && !firstCell.replace(" ","").equals("") && !firstCell.equals("Shv")){
                Shelf shelf = parseShelf(firstCell, shelves.size());
                Item item = parseItem(row, items.size());
                if(!item.getDescription().replace(" ","").equals("-") && !item.getDescription().replace(" ","").equals("")) {
                    items.add(item);
                    shelf.addItem(item);
                }
                shelves.add(shelf);
                racks.get(racks.size()-1).addShelf(shelf);
            } else if(!secondCell.equals("") && !secondCell.replace(" ","").equals("") && !secondCell.equals("Hole")){
                Shelf shelf = shelves.get(shelves.size()-1);
                Item item = parseItem(row, items.size());
                if(!item.getDescription().replace(" ","").equals("-") && !item.getDescription().replace(" ","").equals("")) {
                    items.add(item);
                    shelf.addItem(item);
                }
            }
        }
        store.setRacks(racks);
        return store;
    }

    private Item parseItem(List<String> row, int id) {
        Item item = new Item(id);
        item.setHole(Integer.parseInt(row.get(1).replace(" ", "")));
        item.setSubDept(Integer.parseInt(row.get(3).replace(" ", "")));
        item.setPosition(row.get(4));
        item.setNoUrut(Integer.parseInt(row.get(5).replace(" ", "")));
        int sku = Integer.parseInt(row.get(6).replace(" ", ""));
        item.setSku(sku);
        item.setDescription(row.get(7));
        item.setTierKK(Integer.parseInt(row.get(8).replace(" ","")));
        item.setTierDB(Integer.parseInt(row.get(9).replace(" ","")));
        item.setTierAB(Integer.parseInt(row.get(10).replace(" ","")));
        item.setCapacity(Integer.parseInt(row.get(11).replace(" ","")));
        item.setMinDisplay(Integer.parseInt(row.get(12).replace(" ","")));
        item.setStock(Integer.parseInt(row.get(13).replace(" ","")));
        item.setTag(row.get(14));
        item.setCls(row.get(15));
        if(inventory.containsKey(Integer.valueOf(sku))){
            item.setInventory(inventory.get(Integer.valueOf(sku)));
        } else {
            item.setInventory(0);
        }
        return item;
    }

    private String parseStoreLocation(List<String> row) {
        for(int i=0;i<row.size();i++){
            String cell = row.get(i);
            if(cell!=null && !cell.equals("PLANOGRAM") && !cell.equals("")){
                return cell;
            }
        }
        return "";
    }

    private String parseClassStore(String firstCell) {
        String[] splitted = firstCell.split(":");
        if(splitted.length==2){
            return splitted[1].substring(1,splitted [1].length());
        }
        return "";
    }

    private Rack parseRack(String firstCell, int id) {
        Rack rack = new Rack(id);
        rack.setChecked(false);
        String[] split1 = firstCell.split(":");
        if(split1.length==2) {
            String[] split2 = split1[1].split("-");
            if (split2.length == 2) {
                rack.setNumber(Integer.parseInt(split2[0].replace(" ","")));
                rack.setType(split2[1].substring(1,split2[1].length()));
            }
        }
        return rack;
    }

    private Shelf parseShelf(String firstCell, int id){
        Shelf shelf = new Shelf(id);
        shelf.setNumber(Integer.parseInt(firstCell.replace(" ", "")));
        return shelf;
    }

    public List<List<String>> loadXML(String inputFile) throws IOException {
        List<List<String>> result = new ArrayList<List<String>>();
        File sdcard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(sdcard,inputFile);
        Workbook w;
        try {
            if(file.exists()){
                w = Workbook.getWorkbook(file);
            } else {
                InputStream in = getAssets().open(inputFile);
                w = Workbook.getWorkbook(in);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Planogram not loaded");
                alertDialog.setMessage("The planogram excel has not be found in the Downloads folder.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over column and lines
            for (int j = 0; j < sheet.getRows(); j++) {
                List<String> row = new ArrayList<String>();
                for (int i = 0; i < sheet.getColumns(); i++) {
                    Cell cel = sheet.getCell(i, j);
                    row.add(cel.getContents());
                }
                result.add(row);
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> loadInventory(String inputFile) throws IOException{
        List<String> result = new ArrayList<String>();
        File sdcard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(sdcard,inputFile);
        try {
            BufferedReader br;
            if(file.exists()){
                br = new BufferedReader(new FileReader(file));
            } else {
                InputStream in = getAssets().open(inputFile);
                br = new BufferedReader(new InputStreamReader(in));
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Inventory not loaded");
                alertDialog.setMessage("The inventory file has not be found in the Downloads folder.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public HashMap<Integer,Integer> parseInventory(List<String> input){
        HashMap<Integer,Integer> result = new HashMap<Integer,Integer>();
        for(int i=0;i<input.size();i++) {
            String[] splitted = input.get(i).split("\t");
            if (splitted.length == 4 || splitted.length==3) {
                int index=0;
                if (splitted.length == 4) {
                    index=1;
                }
                Integer sku = Integer.valueOf(splitted[index].split("\\.")[0]);
                Integer inventory = Integer.valueOf(splitted[index+2].split("\\.")[0]);
                result.put(sku,inventory);
            }
        }
        return result;
    }

    public void playGame(View view){
        if(storeString!=null) {
            Random generator = new Random();
            int rackIndex = generator.nextInt(store.getRacks().size());
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",storeString);
            editor.apply();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("rackIndex", rackIndex);
            startActivity(intent);
        }
    }
}
