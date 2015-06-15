package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfamarkt.albi.androidOverriders.PieProgressDrawable;
import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.StorePlanogram;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class GameItemPicker extends Activity {
    private StorePlanogram store;
    private Rack rack;
    private Boolean incorrectItems = false;
    private int shelfIndex = 0;
    private int itemIndex = 0;
    private int rackIndex = -1;
    private Boolean gameFinished = false;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ProgressBar progressBar;
    private ImageView progressPie;
    private File path;
    private ImageView view;
    private int imageWidth;

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

        // variables for images
        //path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        view = (ImageView) findViewById(R.id.imageitem);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageWidth = (35*size.y)/100;

        // indexes to keep track of which item is currently checked
        shelfIndex = sharedPref.getInt("com.alfamarkt.albi.shelfIndex", 0);
        itemIndex = sharedPref.getInt("com.alfamarkt.albi.itemIndex",0);

        // for the 2 progress bars
        progressBar = (ProgressBar) findViewById(R.id.itemsProgressBar);
        progressBar.setMax(rack.getShelves().get(shelfIndex).getItems().size());
        progressBar.setProgress(itemIndex);
        progressPie = (ImageView) findViewById(R.id.shelvesProgressPie);
        PieProgressDrawable myDrawObj = new PieProgressDrawable();
        myDrawObj.setColor(Color.parseColor("#bebebe"));
        myDrawObj.setLevel(0);
        progressPie.setImageDrawable(myDrawObj);
        progressPie.invalidate();

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
            editor.putInt("com.alfamarkt.albi.itemIndex", 0);
            editor.putInt("com.alfamarkt.albi.shelfIndex", 0);
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
        updateProgressBar();
        if(shelfIndex<rack.getShelves().size() && itemIndex<rack.getShelves().get(shelfIndex).getItems().size()) {
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setChecked(true);
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOnDisplay(true);
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOutOfStock(false);
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
        updateProgressBar();
        if(shelfIndex<rack.getShelves().size() && itemIndex<rack.getShelves().get(shelfIndex).getItems().size()) {
            if(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getInventory()>0) {
                incorrectItems = true;
            }
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setChecked(true);
            rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOnDisplay(false);
            if(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getInventory()>0){
                rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOutOfStock(false);
            } else {
                if(!rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getOutOfStock()) {
                    rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOutOfStock(true);
                    rack.getShelves().get(shelfIndex).getItems().get(itemIndex).setOutOfStockSince(new Date());
                }
            }
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

    private void updateProgressBar(){
        if(shelfIndex < rack.getShelves().size() && itemIndex<rack.getShelves().get(shelfIndex).getItems().size()) {
            progressBar.setMax(rack.getShelves().get(shelfIndex).getItems().size());
            progressBar.setProgress(itemIndex+1);
        }
        float shelfProgress = 0f;
        if (shelfIndex >= rack.getShelves().size() || (shelfIndex == rack.getShelves().size()-1 && itemIndex >= rack.getShelves().get(shelfIndex).getItems().size()-1)) {
            shelfProgress=100;
        } else if(shelfIndex < rack.getShelves().size() && itemIndex+1==rack.getShelves().get(shelfIndex).getItems().size()){
            shelfProgress  =(float) Math.floor(((shelfIndex+1) * 100) / (rack.getShelves().size()));
        }
        if(shelfProgress!=0f) {
            PieProgressDrawable myDrawObj = (PieProgressDrawable) progressPie.getDrawable();
            myDrawObj.setLevel((int) shelfProgress);
            progressPie.invalidate();
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
                SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("com.alfamarkt.albi.itemIndex", itemIndex);
                editor.putInt("com.alfamarkt.albi.shelfIndex", shelfIndex);
                editor.putString("com.alfamarkt.albi.storeString", store.toString());
                editor.apply();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = String.valueOf(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getSku())+".jpg";
        return new File(path, imageFileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            tryLoadImage(String.valueOf(rack.getShelves().get(shelfIndex).getItems().get(itemIndex).getSku())+".jpg");
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            shelfIndex = sharedPref.getInt("com.alfamarkt.albi.shelfIndex",0);
            itemIndex = sharedPref.getInt("com.alfamarkt.albi.itemIndex",0);
            setNextItem();
        }
    }

    private void tryLoadImage(String imageFileName){
        if (path != null) {
            File file = new File(path, imageFileName);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(file.exists()){
                view.setImageBitmap( decodeSampledBitmapFromResource(file.getAbsolutePath(), imageWidth/2, imageWidth/2));
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

    public static Bitmap decodeSampledBitmapFromResource(String absolutePath,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absolutePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(absolutePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
