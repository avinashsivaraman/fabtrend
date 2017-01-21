package com.content.avinash.fabtrend;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,NavigationView.OnNavigationItemSelectedListener{
    private static final int PLACE_PICKER_REQUEST = 1;
    private static LatLngBounds BOUNDS_MOUNTAIN_VIEW;
    public GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public static LatLngBounds getBoundsMountainView(){
        return BOUNDS_MOUNTAIN_VIEW;
    }
    public FloatingActionButton fab;
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        buildGoogleAPIClient();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    if(BOUNDS_MOUNTAIN_VIEW!=null)
                        intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    else {
                        Snackbar.make(findViewById(R.id.fab),"Trying to Find the location",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                        onConnected(Bundle.EMPTY);
                    }

                    Intent intent = intentBuilder.build(MainActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private synchronized void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
        fab.performClick();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK ){
            final Place place = PlacePicker.getPlace(data,this);
            Snackbar.make(fab,place.getName(),Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    fab.performClick();
                }
            }).show();
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }

//        return super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            setBoundsMountainView(new LatLngBounds(new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude()),new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude())));
        }else{
            Snackbar.make(findViewById(R.id.fab),"No Network Found",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(findViewById(R.id.fab),"Network is suspended",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Snackbar.make(findViewById(R.id.fab),"Network connection failed",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
    }

    public void setBoundsMountainView(LatLngBounds boundsMountainView) {
        BOUNDS_MOUNTAIN_VIEW = boundsMountainView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
