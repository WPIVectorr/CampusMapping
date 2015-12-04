package com.vectorr.vectorrmapping;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

public class GUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setInitialImage();
// Start spinner fill code
        //Create Spinners
        Spinner start = (Spinner) findViewById(R.id.start_spinner);
        Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
        Spinner end = (Spinner) findViewById(R.id.end_spinner);
        Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

        //Populate ArrayLists for Spinners
        ArrayList<String> countries = new ArrayList<String>();
        countries.add("Australia");
        countries.add("Canada");
        countries.add("China");
        countries.add("India");
        countries.add("Sri Lanka");
        countries.add("United States");

        //Turn the ArrayLists to Adapters
        ArrayAdapter startAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter startPointAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        startPointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter endAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter endPointAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
        endPointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Put Adapters into Spinners
        start.setAdapter(startAdapter);
        startPoint.setAdapter(startPointAdapter);
        end.setAdapter(endAdapter);
        endPoint.setAdapter(endPointAdapter);
// End spinner fill code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gui, menu);
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

    public void generatePath(View view) {
        Intent intent = new Intent(this, displayRoute.class);

        //Create Spinners
        Spinner start = (Spinner) findViewById(R.id.start_spinner);
        Spinner startPoint = (Spinner) findViewById(R.id.point1_spinner);
        Spinner end = (Spinner) findViewById(R.id.end_spinner);
        Spinner endPoint = (Spinner) findViewById(R.id.point2_spinner);

        //Get strings from Spinners
        String sMap = start.getSelectedItem().toString();
        String sPoint = startPoint.getSelectedItem().toString();
        String eMap = end.getSelectedItem().toString();
        String ePoint = endPoint.getSelectedItem().toString();

        //Store the Strings as extra data
        intent.putExtra("startMap", sMap);
        intent.putExtra("startPoint", sPoint);
        intent.putExtra("endMap", eMap);
        intent.putExtra("endPoint", ePoint);
        //Start the activity
        startActivity(intent);
    }

    private void setInitialImage() {
        setCurrentImage();
    }

    private void setCurrentImage() {

        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        imageView.setImageResource(R.drawable.vectorrlogo);

    }
}
