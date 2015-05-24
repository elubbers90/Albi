package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.Shelf;
import com.alfamarkt.albi.classes.StorePlanogram;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;


public class GameActivity extends Activity {
    public StorePlanogram store;
    public int rackIndex=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        String storeString = sharedPref.getString("com.alfamarkt.albi.storeString", "");
        JSONObject json = null;
        if(!storeString.equals("")) {
            try {
                json = new JSONObject(storeString);
                store = StorePlanogram.jsonToStore(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.activity_game);
        populateTable();
    }

    private void populateTable(){
        if(store!=null){
            TableLayout tbl = (TableLayout)findViewById(R.id.itemsTable);
            for(int i = tbl.getChildCount(); i>1; i++) {
                tbl.removeViewAt(i);
            }
            if(!store.getChecked()) {
                Random generator = new Random();
                rackIndex = generator.nextInt(store.getRacks().size());
            }
            Rack rack = store.getRacks().get(rackIndex);
            List<Shelf> shelves = rack.getShelves();
            for(int i=0;i<shelves.size();i++){
                List<Item> items = shelves.get(i).getItems();
                for(int j=0;j<items.size();j++) {
                    Item item = items.get(j);
                    TableRow newRow = new TableRow(this);
                    TextView sku = new TextView(this);
                    sku.setText(String.valueOf(item.getSku()));
                    TextView name = new TextView(this);
                    name.setText(item.getDescription());
                    newRow.addView(sku);
                    newRow.addView(name);
                    if (item.getChecked()) {
                        if (item.getCorrect()) {
                            // the item is correct, do nothing
                        } else {
                            // item is not on display. show red color.
                            sku.setTextColor(Color.RED);
                            name.setTextColor(Color.RED);
                            tbl.addView(newRow);
                        }
                    } else {
                        // item has not been checked yet, game needs to be played, show item
                        tbl.addView(newRow);
                    }

                }
            }
            if(store.getChecked()){
                Button playGame = (Button) findViewById(R.id.btnNextStep);
                playGame.setVisibility(View.INVISIBLE);
                Button fixDisplay = (Button) findViewById(R.id.btnFixDisplay);
                fixDisplay.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void playGame(View view){
        if(store!=null && rackIndex!=-1) {
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",store.toString());
            editor.apply();
            Intent intent = new Intent(this, GameItemPicker.class);
            intent.putExtra("rackIndex", rackIndex);
            startActivity(intent);
        }
    }

    public void fixDisplay(View view){
        if(store!=null && rackIndex!=-1 && store.getChecked()) {
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",store.toString());
            editor.apply();
            Intent intent = new Intent(this, GameRestocker.class);
            intent.putExtra("rackIndex", rackIndex);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        String storeString = sharedPref.getString("com.alfamarkt.albi.storeString", "");
        JSONObject json = null;
        if (!storeString.equals("")) {
            try {
                json = new JSONObject(storeString);
                store = StorePlanogram.jsonToStore(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.activity_game);
        populateTable();
    }
}
