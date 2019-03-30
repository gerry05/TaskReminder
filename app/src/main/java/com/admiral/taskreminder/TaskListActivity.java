package com.admiral.taskreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.admiral.taskreminder.adapter.TasksAdapter;
import com.admiral.taskreminder.database.DatabaseHelper;
import com.admiral.taskreminder.model.Tasks;
import com.admiral.taskreminder.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {
    FloatingActionButton createTask_btn;
    DatabaseHelper myDb;

    ListView tasks_listView;
    ArrayList<Tasks> listItem;
    ArrayAdapter adapter;

    List<Tasks> itemList;
    TasksAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        getSupportActionBar().setTitle("Events");

        createTask_btn = findViewById(R.id.createTask_btn);
        tasks_listView = findViewById(R.id.tasks_listView);

        myDb = new DatabaseHelper(this);

        itemList = new ArrayList<>();

        createTask_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
                intent.putExtra("action", "create");
                startActivity(intent);

            }
        });

        Cursor res = myDb.getAllData();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,Alarm.class);
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Tasks tasks = new Tasks(res.getInt(0), res.getString(1), res.getDouble(2), res.getDouble(3), res.getLong(4),res.getLong(5));
                itemList.add(tasks);
                if(res.getLong(4) > new Date().getTime() ){
                    intent.putExtra("notificationId", res.getInt(0));
                    intent.putExtra("id", res.getInt(0));
                    intent.putExtra("todo", res.getString(1));
                    intent.putExtra("lat",res.getDouble(2));
                    intent.putExtra("lon",res.getDouble(3));
                    intent.putExtra("start_date",res.getLong(4));
                    intent.putExtra("end_date",res.getLong(5));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,res.getInt(0),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,res.getLong(4),pendingIntent);
                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this,res.getInt(0),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,res.getLong(4) - 600000,pendingIntent2);
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


                Intent intent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
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

    public boolean isGPSEnabled(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                if (isGPSEnabled(getBaseContext())) {
                    Intent intent = new Intent(TaskListActivity.this, MapViewAllActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please enable the GPS", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_calendar:
                //Toast.makeText(this, "Calendar!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TaskListActivity.this,CalendarActivity.class);
                startActivity(intent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {

    }
}
