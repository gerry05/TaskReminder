package com.admiral.taskreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.admiral.taskreminder.R;
import com.admiral.taskreminder.model.Tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TasksAdapter extends ArrayAdapter<Tasks> implements View.OnClickListener {

    private ArrayList<Tasks> tasksList;
    private int resourceLayout;
    private Context mContext;


    public TasksAdapter(Context context, int resource, List<Tasks> objects) {
        super(context, resource, objects);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }
        Tasks tasks = getItem(position);


        if(tasks != null){
            TextView notes_txt = (TextView) v.findViewById(R.id.notes_txt);
            TextView start_date_txt = v.findViewById(R.id.start_date_txt);
            TextView end_date_txt = v.findViewById(R.id.end_date_txt);
            if(notes_txt != null){
                notes_txt.setText(tasks.getNotes());
            }
            if(start_date_txt != null){
                if(tasks.getAlarm() != 0){
                    start_date_txt.setText("Event start: "+getDate(tasks.getAlarm(), "EEE, MMM dd, yyyy") +" "+getDate(tasks.getAlarm(),"hh:mm aaa"));
                }else{
                    start_date_txt.setText("Event start: n/a");
                }
            }
            if(end_date_txt != null){
                if(tasks.getEnd_date() != 0){
                    end_date_txt.setText("Event end: "+getDate(tasks.getEnd_date(), "EEE, MMM dd, yyyy") +" "+getDate(tasks.getEnd_date(),"hh:mm aaa"));
                }else{
                    end_date_txt.setText("Event end: n/a");
                }
            }

        }

        return v;
    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onClick(View v) {

    }
}
