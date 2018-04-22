package com.example.kavin.fitness_game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayFriends extends AppCompatActivity {
    public static final String LOG_TAG = "Friends";

    public List<String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friends);
        ListView mListView = (ListView) findViewById(R.id.myListView);

        // Initializing a new String Array
        final String[] friends = new String[] {
                "test871956352@gmail.com",
                "zhz@db.com",
                "friends_bob@gmail.com",
                "mingming741@ming.com",
                "friends_alice@gmail.com"
        };
        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_expandable_list_item_1, friends){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text size 10 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
                tv.setPadding(5,0,0,0);
                // Return the view
                return view;
            }
        };
        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Click at: " + position);

                String friends_name = friends[position];

                Log.d(LOG_TAG, "Friends: " + friends_name);
                fetchingData(friends_name);
            }
        });
    }

    void fetchingData(final String friends_name){
        Log.i(LOG_TAG, "Access Web Server: Fetching data - Start");
        final TextView User_Name = (TextView)findViewById(R.id.textView_friends_id);
        final TextView Step_Data = (TextView)findViewById(R.id.textView_step_data);
        final TextView Cal_data = (TextView)findViewById(R.id.textView_cal_data);
        final TextView Dis_Data = (TextView)findViewById(R.id.textView_dis_data);

        String myURL = "http://18.221.211.134/fyp/returnUserData.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(myURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(LOG_TAG, "Request responsed.");
                final String[] user_name = new String[response.length()];
                final String[] data_type = new String[response.length()];
                final String[] time = new String[response.length()];
                final String[] user_val = new String[response.length()];
                String step_value = "No Data";
                String cal_value = "No Data";
                String dis_value = "No Data";
                for (int i =0; i < response.length(); i++){
                    try {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        user_name[i] = jsonObject.getString("userName");
                        data_type[i] = jsonObject.getString("dataType");
                        time[i] = jsonObject.getString("time");
                        user_val[i] = jsonObject.getString("value");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (int i =0; i < response.length(); i++) {
                    Log.i(LOG_TAG, "Responded Data: ");
                    Log.i(LOG_TAG, "User: " + user_name[i]);
                    Log.i(LOG_TAG, "Data Type: " + data_type[i]);
                    Log.i(LOG_TAG, "Time: " + time[i]);
                    Log.i(LOG_TAG, "Value: " + user_val[i]);

                    if(user_name[i].equals(friends_name) && data_type[i].equals("Step"))
                        step_value = user_val[i];
                    else if(user_name[i].equals(friends_name) && data_type[i].equals("Calorie"))
                        cal_value = user_val[i];
                    else  if (user_name[i].equals(friends_name) && data_type[i].equals("Distance"))
                        dis_value = user_val[i];
                }
                User_Name.setText(friends_name);
                Step_Data.setText(step_value);
                Cal_data.setText(cal_value);
                Dis_Data.setText(dis_value);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, "Volley error.");
            }
        });
        com.example.kavin.fitness_game.AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }
}
