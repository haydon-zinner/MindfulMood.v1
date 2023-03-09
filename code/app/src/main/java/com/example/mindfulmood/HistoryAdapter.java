package com.example.mindfulmood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<Mood> {

    Context mCtx;
    int listLayoutRes;
    List<Mood> moodList;
    SQLiteDatabase mDatabase;
    String date;
    String orderBy;

    public HistoryAdapter(Context mCtx, int listLayoutRes, List<Mood> moodList, SQLiteDatabase mDatabase, String date, String orderBy) {
        super(mCtx, listLayoutRes, moodList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.moodList = moodList;
        this.mDatabase = mDatabase;
        this.date = date;
        this.orderBy = orderBy;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("ViewHolder") View view = inflater.inflate(listLayoutRes, null);

        //getting record of the specified position
        final Mood mood = moodList.get(position);

        //getting views
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView textViewTime = view.findViewById(R.id.textViewTime);
        TextView textViewValue = view.findViewById(R.id.textViewValue);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button editButton = view.findViewById(R.id.editButton);

        //adding data to views
        textViewDate.setText(mood.getDate());
        textViewTime.setText(mood.getTime());
        textViewValue.setText(String.valueOf((mood.getValue())));
        textViewDescription.setText(mood.getDescription());


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Delete Record?")
                        .setMessage("Are you sure you want to delete this record?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.execSQL("DELETE FROM mood WHERE id = '" + mood.getId() + "'");
                                Toast.makeText(mCtx, "Entry deleted", Toast.LENGTH_SHORT).show();
                                reloadDatabase();
                            }
                        })
                        .create()
                        .show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEntry(mood);
            }

            private void updateEntry(Mood mood) {
                Intent intent = new Intent(mCtx, UpdateMoodActivity.class);
                intent.putExtra("id", mood.getId());
                intent.putExtra("date", mood.getDate());
                intent.putExtra("time", mood.getTime());
                intent.putExtra("value", mood.getValue());
                intent.putExtra("description", mood.getDescription());
                mCtx.startActivity(intent);
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void reloadDatabase() {
        Cursor cursorHistory = mDatabase.query("mood", null, "date=?", new String[]{date}, null, null, "time " + orderBy, null);
        moodList.clear();
        if (cursorHistory.moveToFirst()) {
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
        notifyDataSetChanged();
    }


}
