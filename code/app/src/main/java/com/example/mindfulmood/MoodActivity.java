package com.example.mindfulmood;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MoodActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "mood_db";

    Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    SeekBar slider;
    EditText description;
    SQLiteDatabase mDatabase;

    String currentTime;
    String currentDate;
    String selectedTime;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Find Objects
        TextView textView_currentTime = findViewById(R.id.textView_currentTime);
        TextView textView_currentDate = findViewById(R.id.textView_currentDate);
        slider = findViewById(R.id.slider);
        description = findViewById(R.id.description);

        //Set Time
        currentTime = timeFormat.format(calendar.getTime());
        selectedTime = currentTime;
        textView_currentTime.setText(currentTime);

        //Set Date
        currentDate = dateFormat.format(calendar.getTime());
        selectedDate = currentDate;
        textView_currentDate.setText(currentDate);

        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createMoodTable();
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createMoodTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS mood (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    time TEXT NOT NULL,\n" +
                        "    date TEXT NOT NULL,\n" +
                        "    mood INTEGER NOT NULL,\n" +
                        "    description TEXT NOT NULL\n" +
                        ");"
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view) {
        String sliderValue = String.valueOf(slider.getProgress());
        String descriptionValue = description.getText().toString();

        //Add to DB
        mDatabase.execSQL(
                "INSERT INTO mood (time, date, mood, description) VALUES ('" + selectedTime + "', '" + selectedDate + "', '" + sliderValue + "', '" + descriptionValue + "');"
        );

        Toast.makeText(this, "Mood Added", Toast.LENGTH_SHORT).show();

        finish();
    }

    public void openTimePicker(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                TextView textView_currentTime = findViewById(R.id.textView_currentTime);
                selectedTime = (String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                textView_currentTime.setText(selectedTime);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, 3,onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void openDatePicker(View view) {

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                TextView textView_currentDate = findViewById(R.id.textView_currentDate);
                calendar.set(year, month, day);
                selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
                textView_currentDate.setText(selectedDate);
            }
        };

        @SuppressLint("ResourceType") DatePickerDialog datePickerDialog = new DatePickerDialog(this, 6,onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    public void goBackOneDay(View view) {
        calendar.add(Calendar.DATE, -1);
        selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        TextView textView_currentDate = findViewById(R.id.textView_currentDate);
        textView_currentDate.setText(selectedDate);
    }

    public void goForwardOneDay(View view) {
        calendar.add(Calendar.DATE, 1);
        selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        TextView textView_currentDate = findViewById(R.id.textView_currentDate);
        textView_currentDate.setText(selectedDate);
    }
}