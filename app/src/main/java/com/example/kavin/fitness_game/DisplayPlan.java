package com.example.kavin.fitness_game;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayPlan extends AppCompatActivity {

    public static final String LOG_TAG = "Plan";

    public List<Plan_item> plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_plan);

        Initial();

        plan = new ArrayList<Plan_item>();
        plan.add(new Plan_item("Casual Plan",2000));
        plan.add(new Plan_item("Normal Plan",5000));
        plan.add(new Plan_item("Hiking Plan",12000));
        plan.add(new Plan_item("Exercise Plan",20000));


        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        for(int i=0;i < plan.size();i ++){
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("First Line", plan.get(i).getTitle() );
            datum.put("Second Line", "Step Target : " + plan.get(i).getStep() );

            data.add(datum);
        }


        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line","Second Line"},
                new int[] {android.R.id.text1, android.R.id.text2});
        ListView mListView = (ListView) findViewById(R.id.myListView_plan);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d(LOG_TAG, "Click at: " + position);

                TextView Step_Data = findViewById(R.id.textView_target_step);

                String value = "" + plan.get(position).write();
                String target_step = plan.get(position).write().split(",")[1];
                Step_Data.setText(value.subSequence(0,value.length()));

                SaveUserPlan(plan.get(position));

            }
        });
    }

    public void Initial(){

        String s = ReadUserPlan();

        if(!s.equals("")){
            TextView Step_Data = findViewById(R.id.textView_target_step);
            Step_Data.setText(s);
        }
    }

    public void SaveUserPlan(Plan_item item){
        String filename = "plan.txt";
        FileOutputStream outputStream;

        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            outputStream.write(item.write().getBytes());
            outputStream.close();
            Log.d(LOG_TAG,"Save success.");
        }catch (Exception e){
            e.printStackTrace();
            Log.d(LOG_TAG,"Save fail.");
        }
    }

    public String ReadUserPlan(){
        String filename = "plan.txt";
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
