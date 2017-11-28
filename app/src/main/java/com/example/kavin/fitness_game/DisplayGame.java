package com.example.kavin.fitness_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;

public class DisplayGame extends AppCompatActivity {

    public static final String LOG_TAG = "Game";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game);

        String totalReward = ReadUserData("reward.txt");

        TextView reward = (TextView)findViewById(R.id.textView_reward);

        String value = "Current points : " + totalReward;
        reward.setText(value.subSequence(0,value.length()));

    }


    public String ReadUserData(String filename){
        String string = "";
        try{
            FileInputStream inputStream = this.openFileInput(filename);
            byte[] buffer = new byte[1024];
            int hasRead = 0;
            StringBuilder sb =new StringBuilder();

            while ((hasRead = inputStream.read(buffer)) != -1){
                sb.append(new String(buffer,0,hasRead));
            }

            inputStream.close();

            string = sb.toString();
            Log.d(LOG_TAG,"Read success.");

        }catch (Exception e){
            e.printStackTrace();
            Log.d(LOG_TAG,"Read fail.");
        }
        return string;
    }
}
