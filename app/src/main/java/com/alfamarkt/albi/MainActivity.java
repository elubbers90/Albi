package com.alfamarkt.albi;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.*;
import jxl.read.biff.BiffException;


public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            List<String> result = loadXML("planogram25032015.xls");
            if(!result.isEmpty()) {
                Button startButton = (Button) findViewById(R.id.btnStartGame);
                startButton.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> loadXML(String inputFile) throws IOException {
        List<String> resultSet = new ArrayList<>();

        InputStream in = getAssets().open(inputFile);

        if (in!=null){
            Workbook w;
            try {
                w = Workbook.getWorkbook(in);
                // Get the first sheet
                Sheet sheet = w.getSheet(0);
                // Loop over column and lines
                for (int j = 0; j < sheet.getRows(); j++) {
                    for (int i = 0; i < sheet.getColumns(); i++) {
                        Cell cel = sheet.getCell(i, j);
                        resultSet.add(cel.getContents());
                    }
                }
            } catch (BiffException e) {
                e.printStackTrace();
            }
        } else {
            resultSet.add("File not found..!");
        }
        if (resultSet.size() == 0) {
            resultSet.add("Data not found..!");
        }
        return resultSet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
