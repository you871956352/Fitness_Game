package com.example.kavin.fitness_game;

/**
 * Created by Kavin on 2017/11/28.
 */

public class UserState {
    private String userName;
    private int step;
    private float distance;
    private float calo;

    UserState(){
        this.userName = "";
        this.step = 0;
        this.calo = 0;
    }

    UserState(String userName,int step,float calo){
        this.userName = userName;
        this.step = step;
        this.calo = calo;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getCalo() {
        return calo;
    }

    public void setCalo(float calo) {
        this.calo = calo;
    }

    public float getDistance(){return distance; }

    public void setDistance(float distance){this.distance = distance;}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String write(){
        String out = "";
        return out + userName + "," + step + "," + calo;
    }
}
