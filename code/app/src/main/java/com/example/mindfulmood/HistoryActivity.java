package com.example.mindfulmood;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<Mood> moodList;
    SQLiteDatabase mDatabase;
    ListView listViewHistory;
    HistoryAdapter historyAdapter;
    Spinner spinnerSortBy;
    TextView textViewDate;

    Calendar calendar;
    String currentDate;
    String selectedDate;
    String orderBy;

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Find Objects
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        listViewHistory = findViewById(R.id.listViewHistory);
        textViewDate = findViewById(R.id.textViewDate);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        moodList = new ArrayList<>();
        mDatabase = openOrCreateDatabase(MoodActivity.DATABASE_NAME, MODE_PRIVATE, null);
        calendar = Calendar.getInstance();
        currentDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        selectedDate = currentDate;
        textViewDate.setText(currentDate);

        spinnerSortBy.setOnItemSelectedListener(this);


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showHistoryFromDatabase(String date, String orderBy) {
        //Make sures its empty before repopulating
        moodList.clear();
        Cursor cursorHistory = mDatabase.query("mood", null, "date=?", new String[] {date}, null, null, "time " + orderBy, null);
        if (cursorHistory.moveToFirst()){
            do {
                moodList.add(new Mood(
                        cursorHistory.getString(4),
                        cursorHistory.getInt(3),
                        cursorHistory.getString(2),
                        cursorHistory.getString(1),
                        cursorHistory.getInt(0)
                ));
            } while (cursorHistory.moveToNext());
        }
        cursorHistory.close();

        //creating the adapter object
        historyAdapter = new HistoryAdapter(this, R.layout.list_layout_history, moodList,mDatabase, selectedDate, orderBy);
        listViewHistory.setAdapter(historyAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
        textViewDate.setText(currentDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goBackOneDay(View view) {
        calendar.add(Calendar.DATE, -1);
        selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        textViewDate.setText(selectedDate);

        showHistoryFromDatabase(selectedDate, orderBy);
    }

    public void openDatePicker(View view) {

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                calendar.set(year, month, day);
                selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
                textViewDate.setText(selectedDate);

                showHistoryFromDatabase(selectedDate, orderBy);
            }
        };

        @SuppressLint("ResourceType") DatePickerDialog datePickerDialog = new DatePickerDialog(this, 6,onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goForwardOneDay(View view) {
        calendar.add(Calendar.DATE, 1);
        selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        textViewDate.setText(selectedDate);

        showHistoryFromDatabase(selectedDate, orderBy);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String sortBySelection = adapterView.getItemAtPosition(i).toString();
        if (sortBySelection.equals("Latest")){
            orderBy = "DESC";
            showHistoryFromDatabase(selectedDate, orderBy);
        } else if (sortBySelection.equals("Oldest")){
            orderBy = "ASC";
            showHistoryFromDatabase(selectedDate, orderBy);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}