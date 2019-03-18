package it.uniba.di.sms.carpooling.Passaggio;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.location.LocationManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.uniba.di.sms.carpooling.R;

public class TrackingActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        Toolbar toolbar = findViewById(R.id.my_toolbar);

        (TrackingActivity.this).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.close_icon);
        toolbar.setTitle("Tracciamento del percorso");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTrackerService();
            }
        });

        int idPassaggio = getIntent().getExtras().getInt("id_passaggio");


//Check whether GPS tracking is enabled//

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

//Check whether this app has access to the location permission//

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //stopTrackerService();
        }
    }


    private void stopTrackerService() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        stopService(serviceIntent);
        onBackPressed();
    }


}

