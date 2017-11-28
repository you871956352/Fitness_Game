package com.example.kavin.fitness_game;

/**
 * Created by Kavin on 2017/11/26.
 */

public class Plan_item {
    private String title;
    private int step;

    Plan_item(){
        this.title = "";
        this.step = 0;
    }

    Plan_item(String title,int step){
        this.title = title;
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String write(){
        String out = "";
        return out + title + "," + step;
    }
}
