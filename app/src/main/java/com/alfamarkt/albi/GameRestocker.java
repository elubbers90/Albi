package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.StorePlanogram;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GameRestocker extends Activity {
    private StorePlanogram store;
    private Rack rack;
    private int shelfIndex = 0;
    private int itemIndex = 0;

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
        Intent intent = getIntent();
        int rackIndex = intent.getIntExtra("rackIndex", -1);
        if(rackIndex!=-1) {
            rack = store.getRacks().get(rackIndex);
        }
        setContentView(R.layout.activity_game_restocker);
        setNextItem();
    }

    private void setNextItem() {
        TextView itemText = (TextView)findViewById(R.id.itemTextRestocked);
        Item item = rack.getShelves().get(shelfIndex).getItems().get(itemIndex);
        if(item.getCorrect()){
            itemIndex++;
            if(itemIndex>=rack.getShelves().get(shelfIndex).getItems().size()){
                itemIndex=0;
                shelfIndex++;
                if(shelfIndex>=rack.getShelves().size()){
                    stopGame();
                } else {
                    setNextItem();
                }
            } else {
                setNextItem();
            }
        } else {
            itemText.setText(item.getSku() + " - " + item.getDescription());
        }
    }

    public void stopGame(){
        rack.setChecked(true);
        store.setChecked(false);
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("com.alfamarkt.albi.storeString",store.toString());
        editor.apply();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_item_picker, menu);
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

    public void restocked(View view){
        if(shelfIndex<rack.getShelves().size() && itemIndex<rack.getShelves().get(shelfIndex).getItems().size()) {
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setChecked(true);
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setCorrect(true);
            itemIndex++;
            if(itemIndex>=rack.getShelves().get(shelfIndex).getItems().size()){
                itemIndex=0;
                shelfIndex++;
                if(shelfIndex>=rack.getShelves().size()){
                    stopGame();
                } else {
                    setNextItem();
                }
            } else {
                setNextItem();
            }
        } else {
            stopGame();
        }
    }
}
