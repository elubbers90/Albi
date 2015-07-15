package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
                                newRow.setWeightSum(1f);
                                newRow.setPadding(0,30,0,30);
                                newRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                                if(tbl.getChildCount() % 2 == 0){
                                    newRow.setBackgroundColor(Color.WHITE);
                                } else {
                                    newRow.setBackgroundColor(Color.parseColor("#F0F2F1"));
                                }


                                TextView name = new TextView(this);
                                name.setText(item.getDescription());
                                name.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.70f));
                                name.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                        getResources().getDimension(R.dimen.tableText));
                                newRow.addView(name);


                                TextView inventory = new TextView(this);
                                inventory.setText(String.valueOf(item.getInventory()));
                                inventory.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.08f));
                                inventory.setPadding(1, 0, 0, 0);
                                inventory.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                        getResources().getDimension(R.dimen.tableText));
                                newRow.addView(inventory);


                                Random generator = new Random();
                                int id = generator.nextInt(1000000) + j + i;


                                RelativeLayout rel1 = new RelativeLayout(this);
                                rel1.setLayoutParams(new TableRow.LayoutParams(0, name.getLineHeight()*2, 0.09f));
                                LinearLayout lin1 = new LinearLayout(this);
                                lin1.setWeightSum(1f);
                                lin1.setOrientation(LinearLayout.VERTICAL);
                                final Button btn = new Button(this);
                                btn.setBackgroundResource(R.drawable.checkmarkgrey);
                                btn.setId(id + 1);
                                btn.setTag(i + "-" + j);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Button) v).setBackgroundResource(R.drawable.checkmarkgreen);
                                        String tag[] = v.getTag().toString().split("-");
                                        Item item = rack.getShelves().get(Integer.parseInt(tag[0])).getItems().get(Integer.parseInt(tag[1]));
                                        item.setRestocked(true);
                                        item.setOnDisplay(true);
                                        Button view = (Button) findViewById(v.getId() - 1);
                                        view.setBackgroundResource(R.drawable.xgrey);
                                        if (checkRackFinished()) {
                                            Button endButton = (Button) findViewById(R.id.btnFixDisplay);
                                            endButton.setEnabled(true);
                                        }
                                    }
                                });
                                rel1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btn.performClick();
                                    }
                                });
                                btn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 0, 1f));
                                lin1.addView(btn);
                                rel1.addView(lin1);
                                newRow.addView(rel1);


                                RelativeLayout relempty = new RelativeLayout(this);
                                relempty.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.03f));
                                newRow.addView(relempty);


                                RelativeLayout rel2 = new RelativeLayout(this);
                                rel2.setLayoutParams(new TableRow.LayoutParams(0, name.getLineHeight()*2, 0.09f));
                                LinearLayout lin2 = new LinearLayout(this);
                                lin2.setWeightSum(1f);
                                lin2.setOrientation(LinearLayout.VERTICAL);
                                final Button btnNo = new Button(this);
                                btnNo.setBackgroundResource(R.drawable.xgrey);
                                btnNo.setId(id);
                                btnNo.setTag(i + "-" + j);
                                btnNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((Button) v).setBackgroundResource(R.drawable.xred);
                                        String tag[] = v.getTag().toString().split("-");
                                        Item item = rack.getShelves().get(Integer.parseInt(tag[0])).getItems().get(Integer.parseInt(tag[1]));
                                        item.setRestocked(true);
                                        item.setOnDisplay(false);
                                        Button view = (Button) findViewById(v.getId() + 1);
                                        view.setBackgroundResource(R.drawable.checkmarkgrey);
                                        if (checkRackFinished()) {
                                            Button endButton = (Button) findViewById(R.id.btnFixDisplay);
                                            endButton.setEnabled(true);
                                        }
                                    }

                                });
                                rel2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btnNo.performClick();
                                    }
                                });
                                btnNo.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 0, 1f));
                                lin2.addView(btnNo);
                                rel2.addView(lin2);
                                newRow.addView(rel2);


                                RelativeLayout relempty2 = new RelativeLayout(this);
                                relempty2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.01f));
                                newRow.addView(relempty2);


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
