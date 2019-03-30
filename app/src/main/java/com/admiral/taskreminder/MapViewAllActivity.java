package com.admiral.taskreminder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.admiral.taskreminder.adapter.TasksAdapter;
import com.admiral.taskreminder.database.DatabaseHelper;
import com.admiral.taskreminder.model.Tasks;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MapViewAllActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    MarkerOptions markerOptions;

    DatabaseHelper myDb;
    List<Tasks> itemList;

    int lastNotificationId = 9999;
    boolean firstLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_all);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Maps");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationpermission();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        myDb = new DatabaseHelper(this);

        itemList = new ArrayList<>();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Globals.myLocation = location;

                Cursor res = myDb.getAllData();
                if (res.getCount() != 0) {
                    while (res.moveToNext()) {
                        if (calculateDistance(res.getDouble(2), res.getDouble(3)) < 300) {
                            if (getDate(new Date().getTime(), "MMM dd, yyyy").equals(getDate(res.getLong(4), "MMM dd, yyyy"))) {
                                // When notification is tapped, call TaskListActivity.
                                Intent mainIntent = new Intent(getApplicationContext(), CreateTaskActivity.class);
                                mainIntent.putExtra("id", res.getInt(0));
                                mainIntent.putExtra("notes", res.getString(1));
                                mainIntent.putExtra("lat", res.getDouble(2));
                                mainIntent.putExtra("lon", res.getDouble(3));
                                mainIntent.putExtra("date_in_ms", res.getLong(4));
                                mainIntent.putExtra("end_date", res.getLong(5));
                                mainIntent.putExtra("action", "view");
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, FLAG_UPDATE_CURRENT);


                                NotificationManager myNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                int notificationId = res.getInt(0) + 1000;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(notificationId), "Reminder", NotificationManager.IMPORTANCE_HIGH);

                                    // Configure the notification channel.
                                    notificationChannel.setDescription(res.getString(1));
                                    notificationChannel.enableLights(true);
                                    notificationChannel.setLightColor(Color.BLUE);
                                    notificationChannel.setShowBadge(true);
                                    notificationChannel.setVibrationPattern(new long[]{100, 250});
                                    notificationChannel.enableVibration(true);
                                    if(notificationChannel != null){
                                        myNotificationManager.createNotificationChannel(notificationChannel);
                                    }
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MapViewAllActivity.this,String.valueOf(notificationId));
                                builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                                        .setContentTitle("Reminders")
                                        .setContentText(res.getString(1))
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true)
                                        .setContentIntent(contentIntent)
                                        .setFullScreenIntent(contentIntent,true)
                                        .setPriority(Notification.PRIORITY_MAX)
                                        .setDefaults(Notification.DEFAULT_VIBRATE);


//                                if (lastNotificationId != res.getInt(0)) {
//                                    // Notify
//                                    lastNotificationId = res.getInt(0);
//                                    myNotificationManager.notify(notificationId, builder.build());
//                                    MPlayer.mediaPlayer(getApplicationContext());
//                                }
                                if(!Globals.notificationIdList.contains(res.getInt(0))){
                                    Globals.notificationIdList.add(res.getInt(0));
                                    myNotificationManager.notify(notificationId, builder.build());
                                    MPlayer.mediaPlayer(getApplicationContext());
                                }
                            }
                        }
                    }

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        markerOptions = new MarkerOptions();
        markerOptions.title("My location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(199.0F));


        Cursor res = myDb.getAllData();
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Tasks tasks = new Tasks(res.getInt(0), res.getString(1), res.getDouble(2), res.getDouble(3), res.getLong(4),res.getLong(5));
                if (getDate(new Date().getTime(), "MMM dd, yyyy").equals(getDate(res.getLong(4), "MMM dd, yyyy"))) {
                    itemList.add(tasks);
                    if (tasks.getLat() != 0.0) {
                        map.addMarker(new MarkerOptions().position(new LatLng(tasks.getLat(), tasks.getLon())).title(tasks.getNotes()));
                    }
                }

            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            map.setMyLocationEnabled(true);

            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (firstLocation == false) {
                        firstLocation = true;
                        goToMyLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                    Globals.myLocation = location;
                }
            });
        }
    }

    private float calculateDistance(double dest_lat, double dest_lon) {
        Location l1 = new Location("One");
        l1.setLatitude(Globals.myLocation.getLatitude());
        l1.setLongitude(Globals.myLocation.getLongitude());

        Location l2 = new Location("Two");
        l2.setLatitude(dest_lat);
        l2.setLongitude(dest_lon);

        float distance_bw_one_and_two = l1.distanceTo(l2);
        return distance_bw_one_and_two;
    }


    private void goToMyLocation(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public boolean checkUserLocationpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                map.setMyLocationEnabled(true);
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if (firstLocation == false) {
                            firstLocation = true;
                            goToMyLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                        Globals.myLocation = location;
                    }
                });
            }
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
        return;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
