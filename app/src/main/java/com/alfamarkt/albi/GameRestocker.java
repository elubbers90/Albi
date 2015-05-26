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


public class GameRestocker extends Activity {
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
        setContentView(R.layout.activity_game_restocker);
        populateTable();
    }

    private void populateTable(){
        if(store!=null){
            TableLayout tbl = (TableLayout)findViewById(R.id.fixItemsTable);
            for(int i = tbl.getChildCount(); i>1; i++) {
                tbl.removeViewAt(i);
            }
            final Rack rack = store.getRacks().get(rackIndex);
            List<Shelf> shelves = rack.getShelves();
            for(int i=0;i<shelves.size();i++){
                List<Item> items = shelves.get(i).getItems();
                for(int j=0;j<items.size();j++) {
                    Item item = items.get(j);
                    if (item.getChecked()) {
                        if (!item.getOnDisplay()) {
                            if(item.getInventory()>0) {
                                TableRow newRow = new TableRow(this);
                                TextView name = new TextView(this);
                                name.setText(item.getDescription());
                                TextView inventory = new TextView(this);
                                inventory.setText(String.valueOf(item.getInventory()));
                                newRow.addView(name);
                                newRow.addView(inventory);
                                Random generator = new Random();
                                int id = generator.nextInt(1000000) + j + i;
                                Button btn = new Button(this);
                                btn.setText("Yes");
                                btn.setId(id + 1);
                                btn.setTag(i + "-" + j);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Button) v).setTextColor(Color.GREEN);
                                        String tag[] = v.getTag().toString().split("-");
                                        Item item = rack.getShelves().get(Integer.parseInt(tag[0])).getItems().get(Integer.parseInt(tag[1]));
                                        item.setRestocked(true);
                                        item.setOnDisplay(true);
                                        Button view = (Button) findViewById(v.getId() - 1);
                                        view.setTextColor(Color.BLACK);
                                        if (checkRackFinished()) {
                                            Button endButton = (Button) findViewById(R.id.btnFixDisplay);
                                            endButton.setEnabled(true);
                                        }
                                    }
                                });
                                newRow.addView(btn);
                                Button btnNo = new Button(this);
                                btnNo.setText("No");
                                btnNo.setId(id);
                                btnNo.setTag(i + "-" + j);
                                btnNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Button) v).setTextColor(Color.RED);
                                        String tag[] = v.getTag().toString().split("-");
                                        Item item = rack.getShelves().get(Integer.parseInt(tag[0])).getItems().get(Integer.parseInt(tag[1]));
                                        item.setRestocked(true);
                                        item.setOnDisplay(false);
                                        Button view = (Button) findViewById(v.getId() + 1);
                                        view.setTextColor(Color.BLACK);
                                        if (checkRackFinished()) {
                                            Button endButton = (Button) findViewById(R.id.btnFixDisplay);
                                            endButton.setEnabled(true);
                                        }
                                    }

                                });
                                newRow.addView(btnNo);
                                tbl.addView(newRow);
                            }
                        }
                    }
                }
            }
        }
    }

    public void stopGame(){
        store.setChecked(true);
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("com.alfamarkt.albi.storeString",store.toString());
        editor.apply();
        Intent intent = new Intent(this, GameSummary.class);
        intent.putExtra("rackIndex", rackIndex);
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

    public Boolean checkRackFinished(){
        List<Shelf> shelves = rack.getShelves();
        for(int i=0;i<shelves.size();i++) {
            List<Item> items = shelves.get(i).getItems();
            for (int j = 0; j < items.size(); j++) {
                Item item = items.get(j);
                if(!item.getRestocked() && !item.getOnDisplay() && item.getInventory()>0){
                    return false;
                }
            }
        }
        return true;
    }

    public void fixDisplay(View view){
        if(checkRackFinished()){
            stopGame();
        }
    }
}
