package com.admiral.taskreminder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.admiral.taskreminder.database.DatabaseHelper;
import com.admiral.taskreminder.model.Tasks;
import com.admiral.taskreminder.utils.DrawableUtils;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarActivity extends AppCompatActivity {

    com.applandeo.materialcalendarview.CalendarView calendarView;
    List<EventDay> events;
    List eventDate;

    DatabaseHelper myDb;
    Button viewEventsMonth_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Event Calendar");

        events = new ArrayList<>();
        eventDate = new ArrayList<>();
        myDb = new DatabaseHelper(this);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setHeaderColor(R.color.colorAccent);
        viewEventsMonth_btn = findViewById(R.id.viewEventsMonth_btn);

        Cursor res = myDb.getAllData();
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Log.e("year","::"+getDate(res.getLong(4), "yyyy"));
                Log.e("month","::"+getDate(res.getLong(4), "MM"));
                Log.e("day","::"+getDate(res.getLong(4), "dd"));
                int day = Integer.parseInt(getDate(res.getLong(4), "dd"));
                int month=Integer.parseInt(getDate(res.getLong(4), "MM"));
                int year= Integer.parseInt(getDate(res.getLong(4), "yyyy"));
                int date = Integer.parseInt(getDate(res.getLong(4), "MMddyyyy"));
                if(year >= 2000){
                    eventDate.add(date);
                    int occurences = Collections.frequency(eventDate,date);
                    Log.e("occurencies","::"+occurences);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(year,month-1,day);

                    if(occurences > 1){
                        for (int i = 0; i < events.size(); i++) {
                            EventDay eventDay = (EventDay) events.get(i);
                            if(eventDay.getCalendar().get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)){
                                if(eventDay.getCalendar().get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)){
                                    if(eventDay.getCalendar().get(Calendar.DAY_OF_MONTH) == calendar1.get(Calendar.DAY_OF_MONTH)){
                                        events.remove(i);
                                        events.add(new EventDay(calendar1, DrawableUtils.getCircleDrawableWithText(this, String.valueOf(occurences))));
                                    }
                                }
                            }
                        }
                    }else{
                        events.add(new EventDay(calendar1, DrawableUtils.getCircleDrawableWithText(this, String.valueOf(occurences))));
                    }
                }

            }
        }

        calendarView.setEvents(events);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Intent intent = new Intent(CalendarActivity.this, FilteredEventsListActivity.class);
                intent.putExtra("date", eventDay.getCalendar().getTimeInMillis());
                intent.putExtra("type","day");
                startActivity(intent);
            }
        });

        viewEventsMonth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, FilteredEventsListActivity.class);
                intent.putExtra("date", calendarView.getCurrentPageDate().getTimeInMillis());
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });

    }


    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
