package com.example.mindfulmood;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Mood {
    int id;
    String description;
    int value;
    String date;
    String time;

    //Constructor


    public Mood(String description, int value, String date, String time, int id) {
        this.description = description;
        this.value = value;
        this.date = date;
        this.time = time;
        this.id = id;
    }

    //Getters and Setters
    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
