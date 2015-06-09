package com.alfamarkt.albi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.alfamarkt.albi.androidOverriders.SwipeDismissTouchListener;
import com.alfamarkt.albi.classes.Item;
import com.alfamarkt.albi.classes.StorePlanogram;
import com.alfamarkt.albi.classes.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ManageUsersActivity extends Activity {
    private LinearLayout mainLinearLayout;
    private ScrollView scrollView;
    private List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollViewUsers);

        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        String usersString = sharedPref.getString("com.alfamarkt.albi.users", "");
        JSONArray json = null;
        users = new ArrayList<User>();
        if(usersString!=null && !usersString.equals("")) {
            try {
                json = new JSONArray(usersString);
                users = User.jsonToUsers(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(users.size()==0){
            users.add(new User(0,"Enter name"));
        }

        displayUsers();
    }

    @Override
    protected void onStop(){
        super.onStop();
        saveUsers();
    }

    private void saveUsers() {
        String result = "[";
        for (int i = 0; i < users.size(); i++) {
            if (i != 0) {
                result += ",";
            }
            result += users.get(i).toString();
        }
        result += "]";
        SharedPreferences sharedPref = this.getSharedPreferences("com.alfamarkt.albi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("com.alfamarkt.albi.users", result);
        editor.apply();
    }

    public void displayUsers(){
        mainLinearLayout.removeAllViews();
        for(int i=0;i<users.size();i++) {
            final RelativeLayout rel = new RelativeLayout(this);
            rel.setId(users.get(i).getId());
            rel.setBackgroundResource(R.drawable.bordersmall);
            int height = getResources().getDisplayMetrics().heightPixels;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.height = height / 10;
            params.setMargins(0, 0, 0, height / 100);
            rel.setLayoutParams(params);
            rel.setOnTouchListener(new SwipeDismissTouchListener(
                    rel,
                    null,
                    new SwipeDismissTouchListener.DismissCallbacks() {
                        @Override
                        public boolean canDismiss(Object token) {
                            return true;
                        }

                        @Override
                        public void onDismiss(View view, Object token) {
                            scrollView.removeView(rel);
                        }
                    }));

            LinearLayout lin = new LinearLayout(this);
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setWeightSum(1);

            ImageView imageView = new ImageView(this);
            if (i % 4 == 0) {
                imageView.setImageResource(R.drawable.user1);
            } else if (i % 4 == 1) {
                imageView.setImageResource(R.drawable.user2);
            } else if (i % 4 == 2) {
                imageView.setImageResource(R.drawable.user4);
            } else {
                imageView.setImageResource(R.drawable.user3);
            }
            LinearLayout.LayoutParams linparams1 = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.2f

            );
            imageView.setLayoutParams(linparams1);
            lin.addView(imageView);

            EditText editText = new EditText(this);
            editText.setText(users.get(i).getName());
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setSingleLine(true);
            editText.setTag(i);
            editText.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams linparams2 = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.6f

            );
            linparams2.gravity= Gravity.CENTER_VERTICAL;
            editText.setLayoutParams(linparams2);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        users.get(Integer.parseInt(v.getTag().toString())).setName(v.getText().toString());
                        saveUsers();
                    }
                    return false;
                }
            });
            editText.setOnKeyListener(new EditText.OnKeyListener() {
                @Override
                public boolean onKey(View v, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.KEYCODE_BACK || keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        if (i == KeyEvent.KEYCODE_BACK) {
                            users.get(Integer.parseInt(v.getTag().toString())).setName(((TextView) v).getText().toString());
                            saveUsers();
                        }
                    }
                    return false;
                }
            });
            lin.addView(editText);


            RelativeLayout rel2 = new RelativeLayout(this);
            rel2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.2f));
            LinearLayout lin2 = new LinearLayout(this);
            lin2.setWeightSum(1f);
            lin2.setOrientation(LinearLayout.VERTICAL);
            final Button btnNo = new Button(this);
            btnNo.setBackgroundResource(R.drawable.xred);
            btnNo.setTag(i);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = v.getTag().toString();
                    users.remove(Integer.parseInt(tag));
                    for(int j=0;j<users.size();j++){
                        users.get(j).setId(j);
                    }
                    displayUsers();
                }

            });
            rel2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnNo.performClick();
                }
            });
            rel2.addView(btnNo);
            lin.addView(rel2);


            rel.addView(lin);
            mainLinearLayout.addView(rel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_users, menu);
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

    public void createUser(View view){
        users.add(new User(users.size(),"Enter name"));
        saveUsers();
        displayUsers();
        scrollView.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
