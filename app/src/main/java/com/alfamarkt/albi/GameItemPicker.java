package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.StorePlanogram;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class GameItemPicker extends Activity {
    private StorePlanogram store;
    private Rack rack;
    private Boolean incorrectItems = false;
    private int shelfIndex = 0;
    private int itemIndex = 0;
    private int rackIndex = -1;
    private Boolean gameFinished = false;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        String storeString = sharedPref.getString("com.alfamarkt.albi.storeString", "");
        JSONObject json;
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
        if(rackIndex!=-1) {
            rack = store.getRacks().get(rackIndex);
        }
        setContentView(R.layout.activity_game_item_picker);
        setNextItem();
    }

    private void setNextItem() {
        Item item = rack.getShelves().get(shelfIndex).getItems().get(itemIndex);
        TextView itemSKU = (TextView)findViewById(R.id.itemSKU);
        itemSKU.setText(String.valueOf(item.getSku()));
        TextView itemText = (TextView)findViewById(R.id.itemText);
        itemText.setText(item.getDescription());
        tryLoadImage(String.valueOf(item.getSku()) + ".jpg");
    }

    public void stopGame(){
        if(!gameFinished) {
            gameFinished = true;
            rack.setChecked(true);
            store.setChecked(incorrectItems);
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString", store.toString());
            editor.apply();
            if (store != null && rackIndex != -1 && store.getChecked()) {
                Intent intent = new Intent(this, GameRestocker.class);
                intent.putExtra("rackIndex", rackIndex);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, GameSummary.class);
                intent.putExtra("rackIndex", rackIndex);
                startActivity(intent);
            }
            finish();
        }
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

    public void inStock(){
        if(shelfIndex<rack.getShelves().size() && itemIndex<rack.getShelves().get(shelfIndex).getItems().size()) {
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setChecked(true);
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOnDisplay(true);
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

    public void notInStock(){
        if(shelfIndex<rack.getShelves().size() && itemIndex<rack.getShelves().get(shelfIndex).getItems().size()) {
            if(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getInventory()>0) {
                incorrectItems = true;
            }
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setChecked(true);
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOnDisplay(false);
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

    public void addPhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = String.valueOf(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getSku())+".jpg";
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(path, imageFileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            tryLoadImage(String.valueOf(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getSku())+".jpg");
        }
    }

    private void tryLoadImage(String imageFileName){
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (path != null) {
            File file = new File(path, imageFileName);
            ImageView view = (ImageView) findViewById(R.id.imageitem);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(file.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                view.setImageBitmap(myBitmap);
                view.setPadding(10, 10, 10, 10);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            } else {
               view.setImageResource(R.drawable.camera);
                view.setPadding(0, 10, 10, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }
            view.setLayoutParams(lp);
        }

    }
}
