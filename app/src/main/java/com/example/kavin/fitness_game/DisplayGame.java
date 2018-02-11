package com.example.kavin.fitness_game;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.lang.reflect.Field;

public class DisplayGame extends AppCompatActivity {

    public static final String LOG_TAG = "Game";

    public int c;
    public int w;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game);

        String totalReward = ReadUserData("reward.txt");

        TextView reward = (TextView)findViewById(R.id.textView_reward);

        String value = "Current points : " + totalReward;
        reward.setText(value.subSequence(0,value.length()));

        c = 1;
        w = 1;
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

    public void onClick(View view){
        switch(view.getId()) {
            case R.id.c1:
                c = 1;
                break;
            case R.id.c2:
                c = 2;
                break;
            case R.id.c3:
                c = 3;
                break;
            case R.id.w1:
                w = 1;
                break;
            case R.id.w2:
                w = 2;
                break;
        }

        updateChar();
    }
    public void updateChar(){
        ImageView character = findViewById(R.id.imageView);
        String name = "char" + c + w;

        Log.d(LOG_TAG,"Combination " + name);

        int resId = getResId();
        Log.d(LOG_TAG,"Id " + resId);


        character.setImageResource(resId);
    }

    public int getResId() {

        switch(c) {
            case 1:
                switch (w){
                    case 1:
                        return R.drawable.char11;
                    case 2:
                        return R.drawable.char12;
                }
                break;
            case 2:
                switch (w){
                    case 1:
                        return R.drawable.char21;
                    case 2:
                        return R.drawable.char22;
                }
                break;
            case 3:
                switch (w){
                    case 1:
                        return R.drawable.char31;
                    case 2:
                        return R.drawable.char32;
                }
                break;
        }
        return 0;
    }
}
