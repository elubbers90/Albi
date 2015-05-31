package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        if(storeString!=null && !storeString.equals("")) {
            try {
                json = new JSONObject(storeString);
                store = StorePlanogram.jsonToStore(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = getIntent();
        rackIndex = intent.getIntExtra("rackIndex", -1);
        setContentView(R.layout.activity_game);
        populateTable();
    }

    private void populateTable(){
        if(store!=null && rackIndex!=-1){
            Rack rack = store.getRacks().get(rackIndex);
            TextView rackNumber = (TextView) findViewById(R.id.rackNumber);
            rackNumber.setText("Rack " + rack.getNumber());
            TextView shelfAmount = (TextView) findViewById(R.id.shelfAmount);
            if(rack.getShelves().size()==1){
                shelfAmount.setText("1 shelf");
            } else {
                shelfAmount.setText(rack.getShelves().size() + " shelves");
            }
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            int personalBest = (int) sharedPref.getFloat("com.alfamarkt.albi.highScoreStore"+store.getClassStore().replace(" ","")+ "Rack" +rack.getNumber(), -1);
            if(personalBest>=0){
                TextView personalBestView = (TextView) findViewById(R.id.previousBest);
                personalBestView.setText("Previous Best:\n" + personalBest + "% on display");
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
            finish();
        }
    }
}
