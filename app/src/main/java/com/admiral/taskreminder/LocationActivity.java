package com.admiral.taskreminder;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.admiral.taskreminder.direction_helpers.FetchURL;
import com.admiral.taskreminder.direction_helpers.TaskLoadedCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    GoogleMap map;
    Button getDirection_btn;
    ImageView back_btn;
    Polyline currentPolyline;
    Boolean isPolylineVisible = false;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker destinationMarker;
    LatLng destinationLocation;
    private PlaceAutocompleteFragment placeAutocompleteFragment;

    Bundle bundle;
    String action;

    boolean firstLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();
        getDirection_btn = findViewById(R.id.getDirection_btn);
        back_btn = findViewById(R.id.back_btn);

        Intent getExtraIntent = getIntent();
        bundle = getExtraIntent.getExtras();
        action = bundle.getString("action");
        if(bundle.getDouble("lat") != 0.0){
            destinationLocation = new LatLng(bundle.getDouble("lat"),bundle.getDouble("lon"));
        }




        placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationpermission();
        }


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);


        getDirection_btn.setBackgroundColor(0xFFc0c0c0);
            getDirection_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isPolylineVisible == true){
                        //SAVE BUTTON IS VISIBLE
                        if(!action.equals("view")){
                            onBackPressed();
                        }
                    }else{
                        if(destinationLocation != null){
                            String url = getUrl(new LatLng(Globals.myLocation.getLatitude(),Globals.myLocation.getLongitude()),destinationLocation,"walking");
                            new FetchURL(LocationActivity.this).execute(url,"walking");
                           if(!action.equals("view")){
                               getDirection_btn.setText("SAVE");
                           }
                        }else{
                            Toast.makeText(LocationActivity.this, "Please set your destination!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
       

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               // Log.e("Location: ", location.toString());
                Log.e("location latlon", location.getLatitude() + ":" + location.getLongitude());
                Globals.myLocation = location;

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

        placeSelected();
    }

    private void placeSelected() {
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                final LatLng latLngLoc = place.getLatLng();
                destinationLocation =  latLngLoc;
                if(destinationMarker!=null){
                    destinationMarker.remove();
                }
                if(currentPolyline != null){
                    currentPolyline.remove();
                    isPolylineVisible = false;
                    getDirection_btn.setText("GET DIRECTION");
                }
                getDirection_btn.setBackgroundColor(0xFF4fc3f7);
                destinationMarker = map.addMarker(new MarkerOptions().position(latLngLoc).title(place.getName().toString()));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLngLoc));
                map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(LocationActivity.this, ""+status.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToMyLocation(LatLng latLng){
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(13),2000,null);
    }
    private void openNavigation(LatLng latLng){
        String uri = "waze://?ll="+String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude)+"&navigate=yes";
        try{
            startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(uri)));
        }catch (Exception e){
            Toast.makeText(this, "You need to download the WAZE app.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(destinationLocation != null){
            getDirection_btn.setBackgroundColor(0xFF4fc3f7);
            destinationMarker = map.addMarker(new MarkerOptions().position(destinationLocation).title("Destination"));
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if(firstLocation == false){
                        firstLocation = true;
                        Toast.makeText(LocationActivity.this, "Tap to set desination", Toast.LENGTH_LONG).show();
                        goToMyLocation(new LatLng(location.getLatitude(),location.getLongitude()));
                    }
                    Globals.myLocation = location;
                }
            });
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
              if(!action.equals("view")){
                  if(destinationMarker!=null){
                      destinationMarker.remove();
                  }

                  getDirection_btn.setBackgroundColor(0xFF4fc3f7);
                  destinationLocation = latLng;
                  if(currentPolyline != null){
                      currentPolyline.remove();
                      isPolylineVisible = false;
                      getDirection_btn.setText("GET DIRECTION");
                  }
                  destinationMarker = map.addMarker(new MarkerOptions().position(latLng).title("destination"));
              }

            }
        });
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    public boolean checkUserLocationpermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                map.setMyLocationEnabled(true);
           //     Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                Globals.myLocation = lastKnownLocation;
//                if(Globals.myLocation != null){
//                    goToMyLocation(new LatLng(Globals.myLocation.getLatitude(),Globals.myLocation.getLongitude()));
//                }
//
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if(firstLocation == false){
                            firstLocation = true;
                            Toast.makeText(LocationActivity.this, "Tap to set desination", Toast.LENGTH_LONG).show();
                            goToMyLocation(new LatLng(location.getLatitude(),location.getLongitude()));
                        }
                        Globals.myLocation = location;
                    }
                });
            }
        }else{
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
        return;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("backkkk");
        Intent intent = new Intent();
        if(destinationLocation != null){
            intent.putExtra("lat", Double.parseDouble(String.valueOf(destinationLocation.latitude)));
            intent.putExtra("lon",Double.parseDouble(String.valueOf(destinationLocation.longitude)));
        }
        intent.putExtra("action",action);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
        isPolylineVisible = true;
        map.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);
    }
}
