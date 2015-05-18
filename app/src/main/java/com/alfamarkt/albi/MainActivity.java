
package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.*;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.Shelf;
import com.alfamarkt.albi.classes.StorePlanogram;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.*;
import jxl.read.biff.BiffException;


public class MainActivity extends Activity{
    private StorePlanogram store;
    private String storeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            List<List<String>> result = loadXML("planogram25032015.xls");
            store = parseXML(result);
            storeString = store.toString();
            if(!result.isEmpty()) {
                Button startButton = (Button) findViewById(R.id.btnStartGame);
                startButton.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StorePlanogram parseXML(List<List<String>> xml){
        StorePlanogram store = new StorePlanogram(0);
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
                items.add(item);
                shelf.addItem(item);
                shelves.add(shelf);
                racks.get(racks.size()-1).addShelf(shelf);
            } else if(!secondCell.equals("") && !secondCell.replace(" ","").equals("") && !secondCell.equals("Hole")){
                Shelf shelf = shelves.get(shelves.size()-1);
                Item item = parseItem(row, items.size());
                items.add(item);
                shelf.addItem(item);
            }
        }
        store.setRacks(racks);
        return store;
    }

    private Item parseItem(List<String> row, int id) {
        Item item = new Item(id);
        item.setHole(Integer.parseInt(row.get(1).replace(" ","")));
        item.setSubDept(Integer.parseInt(row.get(3).replace(" ","")));
        item.setPosition(row.get(4));
        item.setNoUrut(Integer.parseInt(row.get(5).replace(" ","")));
        item.setSku(Integer.parseInt(row.get(6).replace(" ","")));
        item.setDescription(row.get(7));
        item.setTierKK(Integer.parseInt(row.get(8).replace(" ","")));
        item.setTierDB(Integer.parseInt(row.get(9).replace(" ","")));
        item.setTierAB(Integer.parseInt(row.get(10).replace(" ","")));
        item.setCapacity(Integer.parseInt(row.get(11).replace(" ","")));
        item.setMinDisplay(Integer.parseInt(row.get(12).replace(" ","")));
        item.setStock(Integer.parseInt(row.get(13).replace(" ","")));
        item.setTag(row.get(14));
        item.setCls(row.get(15));
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
        shelf.setNumber(Integer.parseInt(firstCell.replace(" ","")));
        return shelf;
    }

    public List<List<String>> loadXML(String inputFile) throws IOException {
        List<List<String>> result = new ArrayList<List<String>>();
        InputStream in = getAssets().open(inputFile);

        if (in!=null){
            Workbook w;
            try {
                w = Workbook.getWorkbook(in);
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
        }
        return result;
    }

    public void playGame(View view){
        if(storeString!=null) {
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",storeString);
            editor.commit();
        }
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
