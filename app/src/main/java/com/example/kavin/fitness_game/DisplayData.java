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
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

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
        Log.d(LOG_TAG,"Start");
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        updateData(dataReadResponse);
                        Log.d(LOG_TAG, "onSuccess()");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "onFailure()", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(LOG_TAG, "onComplete()");
                    }
                });
        Log.d(LOG_TAG,"End");
    }

    public void updateData(DataReadResponse dataReadResult) {

        Log.i(LOG_TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
        Bucket bucket = dataReadResult.getBuckets().get(0);
        DataSet dataSet = bucket.getDataSets().get(0);

        Log.i(LOG_TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        DataPoint dp = dataSet.getDataPoints().get(0);
        Log.i(LOG_TAG, "Data point:");
        Log.i(LOG_TAG, "\tType: " + dp.getDataType().getName());
        Log.i(LOG_TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        Log.i(LOG_TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
        Field field = dp.getDataType().getFields().get(0);
        Log.i(LOG_TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));

        if(dp.getDataType().getName().equals("com.google.step_count.delta")){
            userState.setStep(Integer.parseInt("" + dp.getValue(field)));
            updateUI();
        }else if(dp.getDataType().getName().equals("com.google.calories.expended")){
            userState.setCalo(Float.parseFloat("" + dp.getValue(field)));
            updateUI();
        }

        Log.i(LOG_TAG, "UpdateData finished.");

    }

    /*public static void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(
                    LOG_TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(LOG_TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }
    */

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(LOG_TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(LOG_TAG, "Data point:");
            Log.i(LOG_TAG, "\tType: " + dp.getDataType().getName());
            Log.i(LOG_TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(LOG_TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(LOG_TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
        }
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
}
