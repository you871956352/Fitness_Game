package com.example.kavin.fitness_game;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.View;


public class DisplayHealth extends AppCompatActivity {

    public static final String LOG_TAG = "Health";

    public List<String> advice_t;
    public List<String> advice_c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_health);

        advice_t = Arrays.asList(
                "1.Be Consistent ",
                "2.Follow an Effective Exercise Routine",
                "3.Set Realistic Goals",
                "4.Use the Buddy System",
                "5.Make Your Plan Fit Your Life",
                "6.Be Happy",
                "7.Watch the Clock",
                "8.Call In the Pros",
                "9.Get Inspired",
                "10.Be Patient"
        );
        advice_c = Arrays.asList(
                "Chase Squires is the first to admit that he's no fitness expert. But he is a guy who used to weigh 205 pounds, more than was healthy for his 5'4\" frame. \"In my vacation pictures in 2002, I looked like the Stay Puft Marshmallow Man at the beach,\" says the 42-year-old Colorado resident. Squires decided enough was enough, cut out fatty food, and started walking on a treadmill. The pounds came off and soon he was running marathons -- not fast, but in the race. He ran his first 50-mile race in October 2003 and completed his first 100-miler a year later. Since then, he's completed several 100-mile, 50-mile, and 50k races.",

                "The American Council on Exercise (ACE) recently surveyed 1,000 ACE-certified personal trainers about the best techniques to get fit. Their top three suggestions:\n" +
                        "\n" +
                        "Strength training. Even 20 minutes a day twice a week will help tone the entire body.\n" +
                        "Interval training. \"In its most basic form, interval training might involve walking for two minutes, running for two, and alternating this pattern throughout the duration of a workout,\" says Cedric Bryant, PhD, FACSM, chief science officer for ACE. \"It is an extremely time-efficient and productive way to exercise.\"\n" +
                        "Increased cardio/aerobic exercise. Bryant suggests accumulating 60 minutes or more a day of low- to moderate-intensity physical activity, such as walking, running, or dancing.",

                "\"Don't strive for perfection or an improbable goal that can't be met,\" says Kara Thompson, spokesperson for the International Health Racquet and Sportsclub Association (IHRSA). \"Focus instead on increasing healthy behaviors.\"\n" +
                        "\n" +
                        "In other words, don't worry if you can't run a 5K just yet. Make it a habit to walk 15 minutes a day, and add time, distance, and intensity from there.",

                "Find a friend or relative whom you like and trust who also wants to establish a healthier lifestyle, suggests Thompson. \"Encourage one another. Exercise together. Use this as an opportunity to enjoy one another's company and to strengthen the relationship.\"\n" +
                        "\n",

                "Too busy to get to the gym? Tennis star Martina Navratilova, health and fitness ambassador for the AARP, knows a thing or two about being busy and staying fit.\n" +
                        "\n" +
                        "Make your plan fit your life, she advises in an article on the AARP web site. \"You don't need fancy exercise gear and gyms to get fit.\"\n" +
                        "\n" +
                        "If you've got floor space, try simple floor exercises to target areas such as the hips and buttocks, legs and thighs, and chest and arms (like push-ups, squats, and lunges). Aim for 10-12 repetitions of each exercise, adding more reps and intensity as you build strength.",

                "Be sure to pick an activity you actually enjoy doing, suggests Los Angeles celebrity trainer Sebastien Lagree.\n" + "\"If you hate weights, don't go to the gym. You can lose weight and get in shape with any type of training or activity,\" he says.\n" +
                        "\n" +
                        "And choose something that is convenient. Rock climbing may be a great workout, but if you live in a city, it's not something you'll be doing every day.",

                "Your body clock, that is. Try to work out at the time you have the most energy, suggests Jason Theodosakis, MD, exercise physiologist at the University of Arizona College of Medicine. If you're a morning person, schedule your fitness activities early in the day; if you perk up as the day goes along, plan your activities in the afternoon or evening.\n" +
                        "\n" +
                        "\"Working out while you have the most energy will yield the best results,\" Theodosakis says.",

                "Especially if you're first getting started, Theodosakis suggests having a professional assessment to determine what types of exercise you need most.\n" +
                        "\n" +
                        "\"For some people, attention to flexibility or to balance and agility, may be more important than resistance training or aerobics,\" he says. \"By getting a professional assessment, you can determine your weakest links and focus on them. This will improve your overall fitness balance.\"",

                "\"Fitness is a state of mind,\" says fitness professional and life coach Allan Fine of Calgary, Alberta, Canada. One of Fine's tricks to get and stay motivated is to read blogs or web sites that show him how others have been successful. \"Who inspires you?\" he asks.",

                "Finally, remember that even if you follow all these tips, there will be ups and downs, setbacks and victories, advises Navratilova. Just be patient, and don't give up, she says on the AARP web site: \"Hang in there, and you'll see solid results.\""
        );

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        for(int i=0;i < 10;i ++){
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("First Line", advice_t.get(i) );
            data.add(datum);
        }


        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line"},
                new int[] {android.R.id.text1});
        ListView mListView = (ListView) findViewById(R.id.myListView);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d(LOG_TAG, "Click at: " + position);

                String title = advice_t.get(position);
                String content = advice_c.get(position);

                warning(title,content);
            }
        });
    }

    public void warning(String title,String content){
        AlertDialog alertDialog = new AlertDialog.Builder(DisplayHealth.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
