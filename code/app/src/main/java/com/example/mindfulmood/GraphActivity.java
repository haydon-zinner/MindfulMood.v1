package com.example.mindfulmood;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {

    //Objects
    TextView textViewDate;
    LineChart lineChart;


    SQLiteDatabase mDatabase;
    ArrayList<Entry> entries = new ArrayList<>();
    String todayDate; // dd-MMM-yy format
    String selectedDate;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //Link Objects
        textViewDate = findViewById(R.id.textViewDate);
        lineChart = findViewById(R.id.lineChart);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set Todays Date
        calendar = Calendar.getInstance();
        todayDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        selectedDate = todayDate;
        textViewDate.setText(selectedDate);

        //Call LineData method
        fillLineData();

    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDatabase.close(); //Close database
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fillLineData(){
        final String[] time = new String[]{"12AM", "1AM", "2AM", "3AM", "4AM", "5AM", "6AM", "7AM","8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM", "6PM", "7PM", "8PM", "9PM", "10PM", "11PM", "12AM"};

        //Populate the data array to be shown
        selectDataFromDB();

        //Create the data set
        LineDataSet lineDataSet = new LineDataSet(entries, "Mood");
        lineDataSet.setLineWidth(2);
        lineDataSet.setDrawFilled(true); //Add the fill under the line
        lineDataSet.setCircleRadius(10);
        lineDataSet.setCircleColor(getResources().getColor(R.color.purple_500));
        lineDataSet.setDrawValues(false); //removes the data point label
        lineDataSet.setHighlightEnabled(true); //highlights the data point when clicked

        //Chart Options
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setMaxHighlightDistance(10); //Sets how close to a datapoint before snapping to it
        lineChart.getLegend().setEnabled(false); //Hide Description
        lineChart.setDescription(null); //Hide Legend
        lineChart.zoom(1,0,0,0);

        // X Axis Option
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(time){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return time[(int) value];
            }

            public int getDecimalDigits() {
                return 0;
            }
        };
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        xAxis.setTextSize(12f);
        //xAxis.setAxisMinimum(10); //Sets starting time viewable on the axis
        //xAxis.setAxisMaximum(12); //Sets finishing time viewable on the axis
        xAxis.setLabelRotationAngle(-65); //Rotates the x axis labels
        xAxis.setDrawAxisLine(false); //Hide the axis line
        xAxis.setDrawGridLines(true); //Hide the grid lines
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setCenterAxisLabels(false); //Centers the x axis labels
        xAxis.setAvoidFirstLastClipping(true); //Avoids clipping of the x axis labels

        // Y Axis Left SideOption
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(12f);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(10);
        yAxis.setLabelCount(10, false);
        yAxis.setDrawGridLines(true);
        yAxis.setDrawAxisLine(false); //Hide the axis line

        // Y Axis Right Side Option
        YAxis YAxisRight = lineChart.getAxisRight();
        YAxisRight.setEnabled(false);

        //Add data set to line data
        LineData lineData = new LineData(lineDataSet);

        //Set line data to line chart
        lineChart.setData(lineData);
        lineChart.setOnChartValueSelectedListener(myFunction);
        lineChart.getAxisLeft().setDrawLimitLinesBehindData(true);
        lineChart.invalidate();
    }

    //Update to include date variable for selection
    private void selectDataFromDB(){

        //Clear any entries
        entries.clear();

        //opening the database
        mDatabase = openOrCreateDatabase(MoodActivity.DATABASE_NAME, MODE_PRIVATE, null);

        //Create cursor to hold the query results
        Cursor cursorHistory = mDatabase.rawQuery(
                "SELECT time, mood, description, id, date FROM mood WHERE date=? ORDER BY date, time ASC", new String[]{selectedDate});

        //Iterate through the cursor and add the data to the entries array
        if (cursorHistory.moveToFirst()) {
            do {
                String timeString = cursorHistory.getString(0);
                float mood = Float.parseFloat(String.valueOf(cursorHistory.getInt(1)));
                String description = cursorHistory.getString(2);
                int id = cursorHistory.getInt(3);
                String date = cursorHistory.getString(4);

                //Convert time to float format while still a string
                String timeHH = timeString.split(":")[0];
                int timeMM = Integer.parseInt(timeString.split(":")[1]);
                timeMM = timeMM * 100 / 60 ;
                float timeFloat = Float.parseFloat(timeHH + "." + timeMM);

                //Add detail entry to entries array
                //entries.add(new detailedEntry(timeFloat, mood, cursorHistory.getString(0), cursorHistory.getString(2), cursorHistory.getInt(3)));
                entries.add(new detailedEntry(timeFloat, mood,timeString, description, id));

            } while (cursorHistory.moveToNext());
        }
        cursorHistory.close();
    }

    OnChartValueSelectedListener myFunction = new OnChartValueSelectedListener() {

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            detailedEntry e1 = (detailedEntry) e;


            AlertDialog.Builder builder = new AlertDialog.Builder(GraphActivity.this);
            builder
                    .setTitle(e1.getTime() + " - Mood " + e1.getMood())
                    .setMessage(e1.getDescription().isEmpty() ? "No Description Saved" : e1.getDescription())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            })
                    .create()
                    .show();
        }

        @Override
        public void onNothingSelected() {

        }
    };

    public void goBackOneDay(View view) {
        calendar.add(Calendar.DATE, -1);
        selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        textViewDate.setText(selectedDate);

        fillLineData();
    }

    public void goForwardOneDay(View view) {
        calendar.add(Calendar.DATE, +1);
        selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
        textViewDate.setText(selectedDate);

        fillLineData();
    }

    private static class detailedEntry extends Entry {
        private String mDescription;
        private int mId;
        private String mTime;

        public detailedEntry(float x, float y, String time, String description, int id) {
            super(x, y);
            mDescription = description;
            mId = id;
            mTime = time;
        }
        public String getDescription() {
            return mDescription;
        }
        public int getId() {
            return mId;
        }
        public Integer getMood() {
            return (int) getY();
        }
        public String getTime() {
            return mTime;
        }
    }

    public void openDatePicker(View view) {

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                TextView textViewDate = findViewById(R.id.textViewDate);
                calendar.set(year, month, day);
                selectedDate = new SimpleDateFormat("dd-MMM-yy").format(calendar.getTime());
                textViewDate.setText(selectedDate);

                //Recall the fillLineData method to update the graph
                fillLineData();
            }
        };

        @SuppressLint("ResourceType") DatePickerDialog datePickerDialog = new DatePickerDialog(this, 6,onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }
}