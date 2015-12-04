package com.vectorr.vectorrmapping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class displayRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_route);

        //Get extras
        Intent intent = getIntent();
        Bundle locations = intent.getExtras();

        //Extract data
        String startMap = locations.getString("startMap");
        String startPoint = locations.getString("startPoint");
        String endMap = locations.getString("endMap");
        String endPoint = locations.getString("endPoint");

        //Create string to display
        StringBuilder message = new StringBuilder();
        message.append(startMap + "\n");
        message.append(startPoint + "\n");
        message.append(endMap + "\n");
        message.append(endPoint + "\n");

        //Display string
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        setContentView(textView);
    }
}
