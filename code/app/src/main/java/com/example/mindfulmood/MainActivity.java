package com.example.mindfulmood;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView textViewLastEntry;
    SQLiteDatabase mDatabase;
    Calendar calendar;
    String currentDate;
    public static final String DATABASE_NAME = "mood_db";


    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLastEntry = findViewById(R.id.textViewLastEntry);
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        calendar = Calendar.getInstance();
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());

        textViewLastEntry.setText("Last Entry: " + getLastEntry());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        textViewLastEntry.setText("Last Entry: " + getLastEntry());
    }


    public void openMoodActivity(View view) {
        Intent intent = new Intent(this, MoodActivity.class);
        startActivity(intent);
    }

    public void viewHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void openGraphActivity(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public String getLastEntry(){
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM mood ORDER BY date, time DESC", null);
        if(cursor.moveToFirst()){
            String lastDate = cursor.getString(2);
            String lastTime = cursor.getString(1);
            return lastDate + " at " + lastTime;
        } else {
            return "No entries yet";
        }
    }
}