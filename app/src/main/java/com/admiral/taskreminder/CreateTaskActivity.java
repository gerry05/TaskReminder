package com.admiral.taskreminder;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.admiral.taskreminder.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateTaskActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText notes_eTxt;
    Button save_btn, delete_btn, setDateTime_btn,setDateTime2_btn,setLocation_btn;
    Calendar calendar,calendar2;
    boolean hasDate,hasTime = false;
    boolean hasDate2,hasTime2 = false;
    Bundle bundle;
    double extra_locationLat,extra_locationLon = 0.0;
    String extra_action,extra_notes;
    int extra_id;
    long dateInMs,dateInMs2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);


        calendar = Calendar.getInstance();
        calendar2 = Calendar.getInstance();
        notes_eTxt = findViewById(R.id.notes_eTxt);
        save_btn = findViewById(R.id.save_btn);
        delete_btn = findViewById(R.id.delete_btn);
        setDateTime_btn = findViewById(R.id.setDateAndTime_btn);
        setDateTime2_btn = findViewById(R.id.setDateAndTime2_btn);
        setLocation_btn = findViewById(R.id.setLocationBtn);

        myDb = new DatabaseHelper(this);

        Log.e("dateee","::"+calendar.getTimeInMillis());

        Intent getExtraIntent = getIntent();
        bundle = getExtraIntent.getExtras();
        extra_action = bundle.getString("action");
        extra_locationLat = bundle.getDouble("lat");
        extra_locationLon = bundle.getDouble("lon");
        extra_notes = bundle.getString("notes");
        extra_id = bundle.getInt("id");
        dateInMs = bundle.getLong("date_in_ms");
        dateInMs2 = bundle.getLong("end_date");
        Log.e("wwaaa","::"+extra_id+","+extra_notes);
        if(extra_action.equals("create")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create");
            save_btn.setText("SAVE");
            delete_btn.setVisibility(View.GONE);
            notes_eTxt.setText(extra_notes);
        }else if(extra_action.equals("update")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Task");
            Log.e("extraID",""+extra_id);
            notes_eTxt.setText(extra_notes);
            notes_eTxt.setSelection(notes_eTxt.getText().length());
            save_btn.setText("UPDATE");
            delete_btn.setVisibility(View.VISIBLE);
            if(dateInMs != 0){
                setDateTime_btn.setText(getDate(dateInMs, "EEE, MMM dd, yyyy") +"\n"+getDate(dateInMs,"hh:mm aaa"));
                setDateTime_btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }
            if(dateInMs2 != 0){
                setDateTime2_btn.setText(getDate(dateInMs2, "EEE, MMM dd, yyyy") +"\n"+getDate(dateInMs2,"hh:mm aaa"));
                setDateTime2_btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }
        }else if(extra_action.equals("view")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Task ID: "+extra_id);
            MPlayer.stopMediaPlayer();
            notes_eTxt.setText(extra_notes);
            notes_eTxt.setFocusable(false);
            notes_eTxt.setSelection(notes_eTxt.getText().length());
            save_btn.setVisibility(View.GONE);
            delete_btn.setVisibility(View.GONE);
            if(dateInMs != 0){
                setDateTime_btn.setText(getDate(dateInMs, "EEE, MMM dd, yyyy") +"\n"+getDate(dateInMs,"hh:mm aaa"));
            }else{
                setDateTime_btn.setText("NO START DATE");
            }
            setDateTime_btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            setDateTime_btn.setEnabled(false);
            setDateTime_btn.setBackgroundColor(0xFFd8d8d8);
            setDateTime_btn.setTextColor(Color.WHITE);

            if(dateInMs2 != 0 ){
                setDateTime2_btn.setText(getDate(dateInMs2, "EEE, MMM dd, yyyy") +"\n"+getDate(dateInMs2,"hh:mm aaa"));
            }else{
                setDateTime2_btn.setText("NO END DATE");
            }
            setDateTime2_btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            setDateTime2_btn.setEnabled(false);
            setDateTime2_btn.setBackgroundColor(0xFFd8d8d8);
            setDateTime2_btn.setTextColor(Color.WHITE);


        }


        if(extra_locationLat != 0.0){
            setLocation_btn.setText("Latitude: "+ extra_locationLat + "\n" +"Longitude: " + extra_locationLon);
        }else{
            if(extra_action.equals("view")){
                setLocation_btn.setEnabled(false);
                setLocation_btn.setBackgroundColor(0xFFd8d8d8);
                setLocation_btn.setTextColor(Color.WHITE);
            }
        }


        setDateTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTime();
            }
        });
        setDateTime2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTime2();
            }
        });

        setLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGPSEnabled(getBaseContext())){
                    Intent intent = new Intent(CreateTaskActivity.this,LocationActivity.class);
                    intent.putExtra("action",extra_action);
                    if(extra_locationLat != 0.0){
                        intent.putExtra("lat",extra_locationLat);
                        intent.putExtra("lon",extra_locationLon);
                    }
                    startActivityForResult(intent,1);
                }else{
                    Toast.makeText(CreateTaskActivity.this, "Please enable the GPS", Toast.LENGTH_LONG).show();
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(notes_eTxt.getText())) {
                    createTask(dateInMs);
                } else {
                    Toast.makeText(CreateTaskActivity.this, "Notes must not be empty..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean isDeleted = myDb.deleteRecord(bundle.getInt("id"));
               if(isDeleted){
                   Intent intent = new Intent(getBaseContext(),TaskListActivity.class);
                   startActivity(intent);
                   Toast.makeText(CreateTaskActivity.this, "Task Deleted!", Toast.LENGTH_SHORT).show();
               }else{
                   Toast.makeText(CreateTaskActivity.this, "Delete Failed!", Toast.LENGTH_SHORT).show();
               }
                
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

            return;
        }
    }

    private void setDateTime() {
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

       DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int mYear,final int mMonth,final int mDayOfMonth) {
                System.out.println(mMonth + 1 + ":" + mDayOfMonth + ":" + mYear);
                hasDate = true;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                hasTime = true;
                                calendar.set(
                                        mYear,
                                        mMonth,
                                        mDayOfMonth,
                                        hourOfDay,
                                        minute,
                                        0
                                );
                                dateInMs = calendar.getTimeInMillis();
                                Log.e("fulldate:",getDate(dateInMs, "EEE, MMM dd, yyyy hh:mm aaa"));
                                Log.e("date in long",""+dateInMs);
                                setDateTime_btn.setText(getDate(dateInMs, "EEE, MMM dd, yyyy") +"\n"+getDate(dateInMs,"hh:mm aaa"));
                                setDateTime_btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year,month,day);
        datePickerDialog.show();

    }

    private void setDateTime2() {
        final int year = calendar2.get(Calendar.YEAR);
        final int month = calendar2.get(Calendar.MONTH);
        final int day = calendar2.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar2.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar2.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int mYear,final int mMonth,final int mDayOfMonth) {
                System.out.println(mMonth + 1 + ":" + mDayOfMonth + ":" + mYear);
                hasDate2 = true;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                hasTime2 = true;
                                calendar2.set(
                                        mYear,
                                        mMonth,
                                        mDayOfMonth,
                                        hourOfDay,
                                        minute,
                                        0
                                );
                                dateInMs2 = calendar2.getTimeInMillis();
                                Log.e("fulldate:",getDate(dateInMs2, "EEE, MMM dd, yyyy hh:mm aaa"));
                                Log.e("date in long",""+dateInMs2);
                                setDateTime2_btn.setText(getDate(dateInMs2, "EEE, MMM dd, yyyy") +"\n"+getDate(dateInMs2,"hh:mm aaa"));
                                setDateTime2_btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year,month,day);
        datePickerDialog.show();

    }


    public void createTask(long timeInMillis) {
        String notes = notes_eTxt.getText().toString();
        Cursor cursor = myDb.getLastRecord();
        Log.e("timeInMillis: ","" + timeInMillis);
        int timestamp = (int) timeInMillis;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,Alarm.class);
        if(hasDate && hasTime){
            if (cursor.getCount() != 0) {
                if (cursor.moveToLast()) {
                    System.out.println(cursor.getInt(0));
                    Log.e("Last id: ", "" + cursor.getInt(0));

                    intent.putExtra("notificationId", cursor.getInt(0));
                    if(extra_action.equals("create")){
                        intent.putExtra("id",cursor.getInt(0)+1);
                    }else{
                        intent.putExtra("id", bundle.getInt("id"));
                    }
                    intent.putExtra("todo", notes);
                    intent.putExtra("lat",extra_locationLat);
                    intent.putExtra("lon",extra_locationLon);
                    intent.putExtra("start_date",dateInMs);
                    intent.putExtra("end_date",dateInMs2);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,cursor.getInt(0) + 2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);
                    if(timeInMillis - 600000 > new Date().getTime()){
                        Log.e("asdasdasd","asdasdasd");
                        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,cursor.getInt(0) + 3,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeInMillis - 600000,pendingIntent1);
                    }
                    Toast.makeText(this, "You have created an event", Toast.LENGTH_SHORT).show();
                }
            }else{
                intent.putExtra("notificationId", 0);
                intent.putExtra("id",0);
                intent.putExtra("todo", notes);
                intent.putExtra("lat",extra_locationLat);
                intent.putExtra("lon",extra_locationLon);
                intent.putExtra("start_date",dateInMs);
                intent.putExtra("end_date",dateInMs2);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);
                if(timeInMillis - 600000 > new Date().getTime()){
                    PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeInMillis - 600000,pendingIntent1);
                }

                if(extra_action.equals("create")){
                    Toast.makeText(this, "You have created an event", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();
                }
            }
            Log.e("timestamp: ",""+ getDate(timeInMillis, "EEE, MMM dd, yyyy") +"\n"+getDate(timeInMillis,"hh:mm aaa"));
        }


        Intent intentTaskListActivity = new Intent(this, TaskListActivity.class);
        if(extra_action.equals("create")){
            boolean isInserted = myDb.insertData(notes,extra_locationLat,extra_locationLon,  dateInMs,dateInMs2,timestamp);
            if (isInserted) {
                hasDate = false;
                hasDate2 = false;
                startActivity(intentTaskListActivity);
            } else {
                Toast.makeText(CreateTaskActivity.this, "Insert failed!", Toast.LENGTH_SHORT).show();
            }
        }else{
            boolean isUpdated = myDb.updateRecord(String.valueOf(bundle.getInt("id")),notes,extra_locationLat,extra_locationLon,dateInMs,dateInMs2,timestamp);
            Log.e("idnotes",""+bundle.getInt("id") + ":::" + bundle.getString("notes"));
            if(isUpdated){
                hasDate = false;
                hasDate2 = false;
                startActivity(intentTaskListActivity);
            }else {
                Toast.makeText(CreateTaskActivity.this, "Update error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                extra_locationLat = data.getDoubleExtra("lat",0.0);
                extra_locationLon = data.getDoubleExtra("lon",0.0);
                extra_action = data.getStringExtra("action");
                if(extra_locationLat != 0.0){
                    setLocation_btn.setText("Latitude: "+ extra_locationLat + "\n" +"Longitude: " + extra_locationLon);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("notes",notes_eTxt.getText().toString());
        bundle.putLong("date_in_ms",dateInMs);
    }

    public boolean isGPSEnabled(Context mContext)
    {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(CreateTaskActivity.this, TaskListActivity.class);
        startActivity(intent);
        return super.onSupportNavigateUp();
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
