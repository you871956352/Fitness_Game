package com.example.kavin.fitness_game;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayData extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 34;
    public static final String LOG_TAG = "DisplayData";

    private static UserState userState;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static Plan_item plan;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);



        userState = new UserState();
        String[] plan_String = ReadUserData("plan.txt").split(",");

        plan = new Plan_item();
        if(plan_String.length >1){
            plan.setTitle(plan_String[0]);
            plan.setStep(Integer.parseInt(plan_String[1]));
        }


        String userStateData = ReadUserData("state.txt");
        if (!userStateData.equals("")){
            String[] temp = userStateData.split(",");
            if(temp.length >1){
                userState.setUserName(temp[0]);
                userState.setStep(Integer.parseInt(temp[1]));
                userState.setCalo(Float.parseFloat(temp[2]));
            }
            updateUI();
        }

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            }
        }
    }

    public void warning(View view){
        int reward = getReward();
        String totalReward = ReadUserData("reward.txt");
        if(!totalReward.equals("")){
            reward += Integer.parseInt(totalReward);
        }
        final int reward_temp = reward;

        AlertDialog alertDialog = new AlertDialog.Builder(DisplayData.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Sure to earn rewards of "+ getReward() +"now(only once)?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SaveReward(reward_temp,"reward.txt");
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void accessGoogleFit() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(LOG_TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(LOG_TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest_step =
                new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

        sendRequest(readRequest_step,R.id.textView_step_data);

        DataReadRequest readRequest_cal =
                new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

        sendRequest(readRequest_cal,R.id.textView_cal_data);

    }

    //Request function.
    public void sendRequest(DataReadRequest readRequest, final int tid){
        Log.d(LOG_TAG,"Sending to google fit: Start");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        updateData(dataReadResponse);
                        Log.d(LOG_TAG, "Sending to google fit: onSuccess()");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Sending to google fit: onFailure()", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(LOG_TAG, "Sending to google fit: onComplete()");
                    }
                });
        Log.d(LOG_TAG,"Sending to google fit: End");
    }

    public void updateData(DataReadResponse dataReadResult) {
        Log.i(LOG_TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
        Bucket bucket = dataReadResult.getBuckets().get(0);
        DataSet dataSet = bucket.getDataSets().get(0);

        Log.i(LOG_TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        if(!dataSet.getDataPoints().isEmpty()) {
            DataPoint dp = dataSet.getDataPoints().get(0);
            Log.i(LOG_TAG, "Data point:");
            Log.i(LOG_TAG, "\tType: " + dp.getDataType().getName());
            Log.i(LOG_TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(LOG_TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            Field field = dp.getDataType().getFields().get(0);
            Log.i(LOG_TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));

            if (dp.getDataType().getName().equals("com.google.step_count.delta")) {
                userState.setStep(Integer.parseInt("" + dp.getValue(field)));
            } else if (dp.getDataType().getName().equals("com.google.calories.expended")) {
                userState.setCalo(Float.parseFloat("" + dp.getValue(field)));
            }
        }
        updateUI();
        Log.i(LOG_TAG, "UpdateData finished.");

    }

    public void SaveUserState(UserState userState,String filename){
        FileOutputStream outputStream;
        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            outputStream.write(userState.write().getBytes());
            outputStream.close();
            Log.d(LOG_TAG,"Save success.");
        }catch (Exception e){
            e.printStackTrace();
            Log.d(LOG_TAG,"Save fail.");
        }
    }

    public void SaveReward(int reward,String filename){
        FileOutputStream outputStream;

        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(("" + reward).getBytes());
            outputStream.close();
            Log.d(LOG_TAG,"Save success.");
        }catch (Exception e){
            e.printStackTrace();
            Log.d(LOG_TAG,"Save fail.");
        }
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

    public void updateUI(){
        TextView Step_Data = (TextView)findViewById(R.id.textView_step_data);

        String value = "" + userState.getStep();
        Step_Data.setText(value.subSequence(0,value.length()));

        TextView Cal_Data = (TextView)findViewById(R.id.textView_cal_data);

        value = "" + userState.getCalo();
        Cal_Data.setText(value.subSequence(0,value.length()));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(plan.getStep());
        progressBar.setProgress(userState.getStep());

        SaveUserState(userState,"state.txt");
        //Log.i(LOG_TAG, "Access server.");
        //fetchingData();
        Log.i(LOG_TAG, "Post to server : Start.");
        PostData("test871956352@gmail.com", "2","2018-2-2","100");
        Log.i(LOG_TAG, "Post to server : End.");

        Log.i(LOG_TAG, "UpdateUI finished.");
    }

    public int getReward(){
        int target_step = plan.getStep();
        int current_step = userState.getStep();
        int reward = 0;

        if(current_step >= target_step){
            reward = target_step + current_step;

        }else{
            reward = current_step;
        }
        Log.i(LOG_TAG, "Save reward finished.");
        return reward;
    }

    void fetchingData(){
        Log.i(LOG_TAG, "Access Web Server: Fetching data - Start");
        final TextView Step_Data = (TextView)findViewById(R.id.textView_step_data);

        String myURL = "http://18.221.211.134/fyp/returnUserData.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(myURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(LOG_TAG, "Request responsed.");
                final String[] user_name = new String[response.length()];
                final String[] user_step = new String[response.length()];
                final String[] user_cal = new String[response.length()];
                final String[] user_val = new String[response.length()];
                for (int i =0; i < response.length(); i++){
                    try {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        user_name[i] = jsonObject.getString("userName");
                        user_step[i] = jsonObject.getString("dataType");
                        user_cal[i] = jsonObject.getString("time");
                        user_val[i] = jsonObject.getString("value");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Step_Data.setText(user_name[0]);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, "Volley error.");
            }
        });
        com.example.kavin.fitness_game.AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void PostData(final String userName, final String dataType, final String time, final String value) {
        String url = "http://18.221.211.134/fyp/processUserData.php";
        StringRequest sq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(LOG_TAG, "Post Data : onResponse.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, error.toString());
                VolleyLog.d("Volley Log", error);
            }
        }) {
            protected Map<String, String > getParams(){
                Map<String, String> parr = new HashMap<String, String>();
                parr.put("userName", userName);
                parr.put("dataTypeID", dataType);
                parr.put("time", time);
                parr.put("value", value);
                return parr;
            }

        };
        AppController.getInstance().addToRequestQueue(sq);
    }
}
