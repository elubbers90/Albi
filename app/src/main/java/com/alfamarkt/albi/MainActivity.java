
package com.alfamarkt.albi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.alfamarkt.albi.Utilities.AlarmReceiver;
import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.Shelf;
import com.alfamarkt.albi.classes.StorePlanogram;
import com.opencsv.CSVReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import jxl.*;
//import jxl.read.biff.BiffException;


public class MainActivity extends Activity{
    private StorePlanogram store;
    //private HashMap<Integer,Integer> inventory;
    private String storeString;
    private boolean loadingStore = false;
    private boolean storeError= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingStore=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadStore();
            }
        }).start();
        //scheduleNotification(getNotification("Test Description"), 30000);
    }

    public StorePlanogram combineStores(StorePlanogram store, StorePlanogram newStore){
        if(store==null){
            return newStore;
        } else if(newStore==null){
            return store;
        } else {
            for(int i=0;i<newStore.getRacks().size();i++){
                Rack rack = newStore.getRacks().get(i);
                for(int j=0;j<rack.getShelves().size();j++){
                    Shelf shelf = rack.getShelves().get(j);
                    for(int k=0;k<shelf.getItems().size();k++){
                        Item item = shelf.getItems().get(k);
                        Item oldItem = store.findItem(item.getSku());
                        if(oldItem!=null) {
                            item.setOutOfStockSince(oldItem.getOutOfStockSince());
                            item.setOutOfStock(oldItem.getOutOfStock());
                            item.setRestocked(oldItem.getRestocked());
                            item.setLastCheckedDate(oldItem.getLastCheckedDate());
                        }
                    }
                }
            }
            return newStore;
        }
    }

    /*
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
    }*/

    public StorePlanogram parseStoreWithInventory(List<String[]> csv){
        StorePlanogram store = new StorePlanogram(0);
        store.setChecked(false);
        List<Rack> racks = new ArrayList<Rack>();
        List<Shelf> shelves = new ArrayList<Shelf>();
        List<Item> items = new ArrayList<Item>();
        // skip row 1, does not contain item
        for(int i=1;i<csv.size();i++){
            String[] row = csv.get(i);
            if(row.length>=4) {
                String infoKey = row[3];
                if (!infoKey.equals("") && !infoKey.replace(" ", "").equals("")) {
                    Item item = new Item(items.size());
                    String[] splittedSku = row[0].split("\\.");
                    item.setSku(Integer.parseInt(splittedSku[0].replace(" ", "")));
                    item.setDescription(row[1]);
                    if(row[2].replace(" ", "").startsWith(".")){
                        item.setInventory(0);
                    } else {
                        String[] splittedInventory = row[2].split("\\.");
                        item.setInventory(Integer.parseInt(splittedInventory[0].replace(" ", "")));
                    }
                    String[] split = infoKey.split("-");
                    if (split.length > 1) {
                        // rack-shelf-holenumber-T/F-Position-Sequence-width-depth-height-minimum display
                        // 018 -01   -01        -T  -A       -01      -02   -04   -01    -002
                        int racknumber = Integer.parseInt(split[0].replace(" ", ""));
                        Rack rack = getRack(racks, racknumber);
                        if (rack == null) {
                            rack = new Rack(racks.size());
                            rack.setNumber(racknumber);
                            rack.setChecked(false);
                            rack.setShelves(new ArrayList<Shelf>());
                            racks.add(rack);
                        }
                        int shelfnumber = Integer.parseInt(split[1].replace(" ", ""));
                        Shelf shelf = getShelf(rack.getShelves(), shelfnumber);
                        if (shelf == null) {
                            shelf = new Shelf(shelves.size());
                            shelf.setNumber(shelfnumber);
                            rack.addShelf(shelf);
                            shelves.add(shelf);
                        }
                        item.setNoUrut(Integer.parseInt(split[5].replace(" ", "")));
                        shelf.addItem(item);
                        items.add(item);
                    }
                }
            }
        }
        Collections.sort(racks);
        for(int i=0;i<racks.size();i++){
            List<Shelf> s = racks.get(i).getShelves();
            for(int j=0;j<s.size();j++){
                List<Item> itemList = s.get(j).getItems();
                Collections.sort(itemList);
            }
            Collections.sort(s);

        }
        store.setRacks(racks);
        return store;
    }

    public Rack getRack(List<Rack> racks, int number){
        for(int i=0;i<racks.size();i++){
            if(racks.get(i).getNumber()==number){
                return racks.get(i);
            }
        }
        return null;
    }

    public Shelf getShelf(List<Shelf> shelves, int number){
        for(int i=0;i<shelves.size();i++){
            if(shelves.get(i).getNumber()==number){
                return shelves.get(i);
            }
        }
        return null;
    }

    /*
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
                alertDialog.setMessage("The planogram excel was not found in the Downloads folder.");
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
    }*/

    public List<String[]> loadCSV(String inputFile) throws IOException {
        List<String[]> result = new ArrayList<String[]>();
        File sdcard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(sdcard,inputFile);
        if(file.exists()){
            try {
                InputStream csvStream = new FileInputStream(file);
                InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
                CSVReader csvReader = new CSVReader(csvStreamReader);
                String[] line;

                // throw away the header
                csvReader.readNext();

                while ((line = csvReader.readNext()) != null) {
                    if(!line[0].equals("---")){
                        result.add(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Planogram not loaded");
            alertDialog.setMessage("The planogram file was not found in the Downloads folder.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        return result;
    }
/*
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
                alertDialog.setMessage("The inventory file was not found in the Downloads folder.");
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
*/
    public void openUsers(View view){
        if(storeString!=null) {
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",storeString);
            editor.apply();
            Intent intent = new Intent(this, ManageUsersActivity.class);
            startActivity(intent);
        }
    }

    public void playGame(View view){
        while(loadingStore){
        }
        if(store!=null && !loadingStore && !storeError) {
            Random generator = new Random();
            int rackIndex = generator.nextInt(store.getRacks().size());
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("rackIndex", rackIndex);
            startActivity(intent);
        }
    }

    public void selectRack(View view){
        while(loadingStore){
        }
        if(store!=null && !loadingStore && !storeError) {
            Intent intent = new Intent(this, GameSelectRack.class);
            startActivity(intent);
        }
    }

    public void loadStore(){
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        storeString = sharedPref.getString("com.alfamarkt.albi.storeString", "");
        JSONObject json = null;
        StorePlanogram oldStore = null;
        if(!storeString.equals("")) {
            try {
                json = new JSONObject(storeString);
                oldStore = StorePlanogram.jsonToStore(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            //List<String> inventoryList = loadInventory("inventory.txt");
            //inventory = parseInventory(inventoryList);
            //List<List<String>> result = loadXML("planogram.xls");
            //StorePlanogram newStore = parseXML(result);
            List<String[]> result = loadCSV("planogram.csv");
            StorePlanogram newStore = parseStoreWithInventory(result);
            store = combineStores(oldStore,newStore);
            storeString = store.toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",storeString);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingStore=false;
        if(store==null){
            storeError = true;
        }
    }

    public void createReport(View view){
       loadStore();
        if(store!=null) {
            BufferedWriter output = null;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "report" +new SimpleDateFormat("dd-MM-yyyy").format(new Date())+ ".csv");
                if(file.exists()){
                    file.delete();
                    file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), "report" +new SimpleDateFormat("dd-MM-yyyy").format(new Date())+ ".csv");
                }
                output = new BufferedWriter(new FileWriter(file, true));
                output.write("SKU;Desc;Last Check Date;#Days OOS");
                output.newLine();
                int items = 0;
                int oos = 0;
                List<Item> oositems = new ArrayList<>();
                try {
                    for (int i = 0; i < store.getRacks().size(); i++) {
                        Rack rack = store.getRacks().get(i);
                        for (int j = 0; j < rack.getShelves().size(); j++) {
                            Shelf shelf = rack.getShelves().get(j);
                            for (int k = 0; k < shelf.getItems().size(); k++) {
                                Item item = shelf.getItems().get(k);
                                items++;
                                if (item.getOutOfStock()) {
                                    oositems.add(item);
                                    long diff = new Date().getTime() - item.getOutOfStockSince().getTime();
                                    output.write(item.getSku() + ";" + item.getDescription() + ";" + new SimpleDateFormat("dd-MM-yyyy").format(item.getLastCheckedDate()) + ";" + (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1));
                                    output.newLine();
                                    oos++;
                                }
                            }
                        }
                    }
                } finally {
                    output.close();
                }
                MediaScannerConnection.scanFile(this,
                        new String[]{file.toString()}, null, null);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Weekly Report Created");
                alertDialog.setMessage("Report saved in the Downloads Directory.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 30);
       // alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  pendingIntent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("The title");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.albilogo);
        return builder.build();
    }
}