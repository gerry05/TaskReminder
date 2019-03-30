package com.admiral.taskreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.admiral.taskreminder.adapter.TasksAdapter;
import com.admiral.taskreminder.database.DatabaseHelper;
import com.admiral.taskreminder.model.Tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FilteredEventsListActivity extends AppCompatActivity {
    DatabaseHelper myDb;

    ListView tasks_listView;
    List<Tasks> itemList;
    TasksAdapter tasksAdapter;
    Bundle bundle;
    Long dateInMs;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_events_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tasks_listView = findViewById(R.id.tasks_listView);
        myDb = new DatabaseHelper(this);

        Intent getExtraIntent = getIntent();
        bundle = getExtraIntent.getExtras();
        dateInMs = bundle.getLong("date");
        type = bundle.getString("type");
        if(type.equals("day")){
            getSupportActionBar().setTitle("Events for "+getDate(dateInMs,"MMMM dd"));
        }else{
            getSupportActionBar().setTitle("Events for "+getDate(dateInMs,"MMMM"));
        }
        itemList = new ArrayList<>();


        Cursor res = myDb.getAllData();
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Tasks tasks = new Tasks(res.getInt(0), res.getString(1), res.getDouble(2), res.getDouble(3), res.getLong(4),res.getLong(5));
                if(getDate(res.getLong(4),"yyyy").equals(getDate(dateInMs,"yyyy"))){
                    if(getDate(res.getLong(4),"MM").equals(getDate(dateInMs,"MM"))){
                        if(type.equals("month")){
                            itemList.add(tasks);
                        }
                        if(getDate(res.getLong(4),"dd").equals(getDate(dateInMs,"dd"))){
                            if(type.equals("day")){
                                itemList.add(tasks);
                            }
                        }
                    }
                }
            }
            tasksAdapter = new TasksAdapter(this, R.layout.row_task_item, itemList);
            tasks_listView.setAdapter(tasksAdapter);
        }


        tasks_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int notesId = tasksAdapter.getItem(position).getId();
                String notes = tasksAdapter.getItem(position).getNotes();
                double lat = tasksAdapter.getItem(position).getLat();
                double lon = tasksAdapter.getItem(position).getLon();
                long alarm = tasksAdapter.getItem(position).getAlarm();
                long end_date = tasksAdapter.getItem(position).getEnd_date();


                Intent intent = new Intent(FilteredEventsListActivity.this, CreateTaskActivity.class);
                intent.putExtra("id", notesId);
                intent.putExtra("notes", notes);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("date_in_ms", alarm);
                intent.putExtra("end_date",end_date);
                intent.putExtra("action", "update");
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
