package com.example.gpsmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private LocationManager locationManager;
    private TextView tvProvider, tvLatitude, tvLongitude;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            tvLatitude.setText("Latitude: " + location.getLatitude());
            tvLongitude.setText("Longitude: " + location.getLongitude());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvProvider = findViewById(R.id.tvProvider);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkPermissionAndStartLocation();
            swipeRefreshLayout.setRefreshing(false);
        });

        checkPermissionAndStartLocation();
    }

    private void checkPermissionAndStartLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        String provider = locationManager.getBestProvider(new android.location.Criteria(), true);
        if (provider == null) {
            tvProvider.setText("Best provider: none");
            return;
        }
        tvProvider.setText("Best provider: " + provider);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 2000, 1, locationListener);

            Location lastKnown = locationManager.getLastKnownLocation(provider);
            if (lastKnown != null) {
                tvLatitude.setText("Latitude: " + lastKnown.getLatitude());
                tvLongitude.setText("Longitude: " + lastKnown.getLongitude());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }
}