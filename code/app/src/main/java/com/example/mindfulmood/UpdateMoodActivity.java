package com.example.mindfulmood;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateMoodActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "mood_db";
    Bundle moodEntry;
    SQLiteDatabase mDatabase;
    Calendar calendar;
    String currentDate;
    String selectedDate;
    String currentTime;
    String selectedTime;
    String currentDescription;
    int currentMood;
    int id;

    TextView textView_currentDate;
    TextView textView_currentTime;
    SeekBar slider;
    EditText description;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mood);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Get Objects
        textView_currentDate = findViewById(R.id.textView_currentDate);
        textView_currentTime = findViewById(R.id.textView_currentTime);
        slider = findViewById(R.id.slider);
        description = findViewById(R.id.description);

        //Get Bundle
        moodEntry = getIntent().getExtras();
        calendar = Calendar.getInstance();
        currentDate = moodEntry.get("date").toString();
        selectedDate = currentDate;
        currentTime = moodEntry.get("time").toString();
        selectedTime = currentTime;
        currentDescription = moodEntry.get("description").toString();
        currentMood =  moodEntry.get("value").toString().equals("") ? 0 : Integer.parseInt(moodEntry.get("value").toString());
        id = moodEntry.get("id").toString().equals("") ? 0 : Integer.parseInt(moodEntry.get("id").toString());


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        try {
            Date date = dateFormat.parse(currentDate);
            assert date != null;
            calendar.setTime(date);
        } catch (ParseException ignored) {

        }


        //Set Objects to bundle data
        slider.setProgress(currentMood);
        description.setText(currentDescription);
        textView_currentDate.setText(currentDate);
        textView_currentTime.setText(currentTime);
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

    public void onUpdate(View view) {
        currentMood = slider.getProgress();
        currentDescription = description.getText().toString();

        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        mDatabase.execSQL("UPDATE mood SET time=?, date=?, mood=?, description=? WHERE id=?",new String[]{selectedTime,selectedDate, String.valueOf(currentMood),currentDescription, String.valueOf(id)});
        mDatabase.close();
        Toast.makeText(this, "Entry Updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}