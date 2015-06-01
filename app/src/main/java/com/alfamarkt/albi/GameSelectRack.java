package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;

import com.alfamarkt.albi.classes.Rack;
import com.alfamarkt.albi.classes.StorePlanogram;
import com.tekle.oss.android.animation.AnimationFactory;

import org.json.JSONException;
import org.json.JSONObject;


public class GameSelectRack extends Activity {
    public StorePlanogram store;
    public int rackIndex=-1;
    private int min_distance = 100;
    private float downX, downY, upX, upY;
    private ViewAnimator viewAnimator;


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
        setContentView(R.layout.activity_game_select_rack);
        populateFlipper();
    }

    private void populateFlipper(){
        if(store!=null){
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int margin = (width*15)/100;

            viewAnimator = (ViewAnimator)this.findViewById(R.id.viewFlipper);
            for(int i=0;i<store.getRacks().size();i++){
                Rack rack = store.getRacks().get(i);
                TextView view = new TextView(this);
                view.setText(String.valueOf(rack.getNumber()));
                ViewFlipper.LayoutParams lp = new ViewFlipper.LayoutParams(
                        ViewFlipper.LayoutParams.MATCH_PARENT,
                        ViewFlipper.LayoutParams.MATCH_PARENT);
                lp.setMargins(margin,margin,margin,margin);
                view.setLayoutParams(lp);
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.flipperText));
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.border);
                view.setTag(String.valueOf(i));
                viewAnimator.addView(view);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(viewAnimator!=null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    //VERTICAL SCROLL
                    if (Math.abs(deltaX) <= Math.abs(deltaY)) {
                        if (Math.abs(deltaY) > min_distance) {
                            // top or down
                            if (deltaY < 0) {
                                AnimationFactory.flipTransitionInverse(viewAnimator, AnimationFactory.FlipDirection.BOTTOM_TOP, 300);
                                return true;
                            }
                            if (deltaY > 0) {
                                AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.TOP_BOTTOM,300);
                                return true;
                            }
                        } else {
                            //not long enough swipe...
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
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
        if(store!=null) {
            View activeview = viewAnimator.getCurrentView();
            rackIndex = Integer.parseInt(activeview.getTag().toString());
            SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.alfamarkt.albi.storeString",store.toString());
            editor.putInt("com.alfamarkt.albi.itemIndex", 0);
            editor.putInt("com.alfamarkt.albi.shelfIndex", 0);
            editor.apply();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("rackIndex", rackIndex);
            startActivity(intent);
            finish();
        }
    }
}
