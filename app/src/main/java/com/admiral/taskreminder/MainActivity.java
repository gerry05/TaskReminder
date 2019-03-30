package com.admiral.taskreminder;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.admiral.taskreminder.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    Button createFirstTask_btn;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        createFirstTask_btn = findViewById(R.id.createFirstTask_btn);

        myDb = new DatabaseHelper(this);


        Cursor res = myDb.getAllData();
        if(res.getCount() != 0){
            Intent intent = new Intent(this,TaskListActivity.class);
            startActivity(intent);
        }
        createFirstTask_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CreateTaskActivity.class);
                intent.putExtra("action","create");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
