package com.example.kavin.fitness_game;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 34;
    public static final String LOG_TAG = "Sample";

    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void redirect_health(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayHealth.class);
        startActivity(intent);
    }
    public void redirect_game(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayGame.class);
        startActivity(intent);
    }
    public void redirect_plan(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayPlan.class);
        startActivity(intent);
    }
    public void redirect_data(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayData.class);
        startActivity(intent);
    }
    public void redirect_friends(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayFriends.class);
        startActivity(intent);
    }
}