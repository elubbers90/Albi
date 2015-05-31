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


public class GameSummary extends Activity {
    private StorePlanogram store;
    private Rack rack;
    private int shelfIndex = 0;
    private int itemIndex = 0;
    private int rackIndex = -1;

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
        rackIndex = intent.getIntExtra("rackIndex", -1);
        if(rackIndex!=-1) {
            rack = store.getRacks().get(rackIndex);
        }
        setContentView(R.layout.activity_game_summary);
        populateTable();
    }

    private void populateTable(){
        if(store!=null){
            TableLayout tbl1 = (TableLayout)findViewById(R.id.inventoryControlTable);
            for(int i = tbl1.getChildCount(); i>1; i++) {
                tbl1.removeViewAt(i);
            }
            TableLayout tbl2 = (TableLayout)findViewById(R.id.onDisplayTable);
            for(int i = tbl2.getChildCount(); i>1; i++) {
                tbl2.removeViewAt(i);
            }
            TableLayout tbl3 = (TableLayout)findViewById(R.id.oosTable);
            for(int i = tbl3.getChildCount(); i>1; i++) {
                tbl3.removeViewAt(i);
            }
            final Rack rack = store.getRacks().get(rackIndex);
            List<Shelf> shelves = rack.getShelves();
            for(int i=0;i<shelves.size();i++){
                List<Item> items = shelves.get(i).getItems();
                for(int j=0;j<items.size();j++) {
                    Item item = items.get(j);
                    if (item.getChecked()) {
                        TableRow newRow = new TableRow(this);
                        newRow.setWeightSum(1f);
                        newRow.setPadding(0,30,0,30);
                        newRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                        TextView name = new TextView(this);
                        name.setText(item.getDescription());
                        name.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.55f));
                        newRow.addView(name);
                        TextView onDisplay = new TextView(this);
                        if(item.onDisplay){
                            onDisplay.setText("Yes");
                        } else {
                            onDisplay.setText("No");
                        }
                        onDisplay.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f));
                        newRow.addView(onDisplay);
                        TextView inventory = new TextView(this);
                        inventory.setText(String.valueOf(item.getInventory()));
                        inventory.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.20f));
                        newRow.addView(inventory);
                        if(!item.getOnDisplay() && item.getRestocked()){
                            if(tbl1.getChildCount() % 2 == 0){
                                newRow.setBackgroundColor(Color.WHITE);
                            } else {
                                newRow.setBackgroundColor(Color.parseColor("#F0F2F1"));
                            }
                            tbl1.addView(newRow);
                        } else if(item.getOnDisplay()){
                            if(tbl2.getChildCount() % 2 == 0){
                                newRow.setBackgroundColor(Color.WHITE);
                            } else {
                                newRow.setBackgroundColor(Color.parseColor("#F0F2F1"));
                            }
                            tbl2.addView(newRow);
                        } else {
                            tbl3.addView(newRow);
                            if(tbl3.getChildCount() % 2 == 0){
                                newRow.setBackgroundColor(Color.WHITE);
                            } else {
                                newRow.setBackgroundColor(Color.parseColor("#F0F2F1"));
                            }
                        }
                    }
                }
            }
            Button but1 = (Button) findViewById(R.id.btnInventoryControl);
            but1.setText(but1.getText() + " - " + (tbl1.getChildCount()-1));
            Button but2 = (Button) findViewById(R.id.btnOnDisplay);
            but2.setText(but2.getText() + " - " + (tbl2.getChildCount()-1));
            Button but3 = (Button) findViewById(R.id.btnOOS);
            but3.setText(but3.getText() + " - " + (tbl3.getChildCount()-1));
            View lay1 = findViewById(R.id.layoutInvcontrol);
            View lay2 = findViewById(R.id.layoutOnDisplay);
            View lay3 = findViewById(R.id.layoutOOS);
            lay1.setVisibility(View.GONE);
            lay2.setVisibility(View.GONE);
            lay3.setVisibility(View.GONE);
        }
    }

    public void stopGame(){
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("com.alfamarkt.albi.storeString",store.toString());
        editor.apply();
        Intent intent = new Intent(this, GameComplete.class);
        startActivity(intent);
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

    public void completeRack(View view){
        stopGame();
    }

    public void toggleInvControl(View view){
        View tbl1 = findViewById(R.id.layoutInvcontrol);
       // View tbl2 = findViewById(R.id.layoutOnDisplay);
        //View tbl3 = findViewById(R.id.layoutOOS);
        if(tbl1.getVisibility() == View.GONE) {
            tbl1.setVisibility(View.VISIBLE);
        } else {
            tbl1.setVisibility(View.GONE);
        }
        //tbl2.setVisibility(View.GONE);
        //tbl3.setVisibility(View.GONE);
    }

    public void toggleOnDisplay(View view){
       // View tbl1 = findViewById(R.id.layoutInvcontrol);
        View tbl2 = findViewById(R.id.layoutOnDisplay);
        //View tbl3 = findViewById(R.id.layoutOOS);
        if(tbl2.getVisibility() == View.GONE) {
            tbl2.setVisibility(View.VISIBLE);
        } else {
            tbl2.setVisibility(View.GONE);
        }
       // tbl1.setVisibility(View.GONE);
       // tbl3.setVisibility(View.GONE);
    }

    public void toggleOOS(View view){
       // View tbl1 = findViewById(R.id.layoutInvcontrol);
       // View tbl2 = findViewById(R.id.layoutOnDisplay);
        View tbl3 = findViewById(R.id.layoutOOS);
        if(tbl3.getVisibility() == View.GONE) {
            tbl3.setVisibility(View.VISIBLE);
        } else {
            tbl3.setVisibility(View.GONE);
        }
       // tbl1.setVisibility(View.GONE);
        //tbl2.setVisibility(View.GONE);
    }
}
