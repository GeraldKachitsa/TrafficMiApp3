package com.example.trafficmi.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trafficmi.HelpCenter;
import com.example.trafficmi.LogIn;
import com.example.trafficmi.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DriverOffence extends AppCompatActivity {

    //Progress bar
    //    ProgressBar progressBar;
    //    int count = 0;
    //    Timer timer;
    private ProgressBar progressBar;
    private int i = 0;
    private Handler hdlr = new Handler();
    private TextView txtView;


    private TextInputEditText fullNameOfDriver;

    EditText driverOffenceDescription, scannedLicenseNum, offenceLocation;
    private Button updateDriverRecordsBtn;
    RadioGroup offenceRadioGroup;
    RadioButton radioSexButton;
    String longitude, latitude, address, licenceNumber;


    //toolBar
    Toolbar driverOffenceToolBar;

    //firebase database

    FirebaseDatabase root;
    DatabaseReference referenci;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int GET_BARCODE_rESULTS = 50;

    TextView getLicenceNumber;


    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_offence);
        fullNameOfDriver = (TextInputEditText) findViewById(R.id.driverName);
        driverOffenceDescription = (EditText) findViewById(R.id.otherOffenceDetails);
        scannedLicenseNum = (EditText) findViewById(R.id.scannedLicenseNum);
        offenceLocation = (EditText) findViewById(R.id.offenceLocation);
        driverOffenceToolBar = (Toolbar) findViewById(R.id.driverOffenceToolBar);
        getLicenceNumber = findViewById(R.id.licenseNum);

        //ProgressBar

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtView = (TextView) findViewById(R.id.tView);


//        textView = findViewById(R.id.textView1);
        offenceRadioGroup = findViewById(R.id.offenceRadioGroup);
        // fused location initialization
        fusedLocationProviderClient = new FusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude = location.getLatitude() + "";
                longitude = location.getLongitude() + "";
            }
        });

        setSupportActionBar(driverOffenceToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getLicenceNumber.setOnClickListener(v -> {
            Intent i = new Intent(this, BarcodeScanner.class);
            startActivityForResult(i, GET_BARCODE_rESULTS);
        });

        updateDriverRecordsBtn = (Button) findViewById(R.id.goToNextOffenceBtn);

        updateDriverRecordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverOffence.this, "Lat :" + latitude + "\n Lon: " + longitude, Toast.LENGTH_SHORT).show();
                driverOffenceRecords();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vehicle_theft_menu, menu);
        return true;
    }
// Handling menu items events

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
                startActivity(new Intent(this, LogIn.class));
                return true;
            case R.id.help:
                startActivity(new Intent(this, HelpCenter.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if yes,Exit
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if No, cancel and continue
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void driverOffenceRecords() {
        int selectedId = offenceRadioGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);


        root = FirebaseDatabase.getInstance();
        referenci = root.getReference();
        referenci = root.getReference("DriverOffences");

        String fullNameDriver = fullNameOfDriver.getText().toString();
        String offenceDescription = driverOffenceDescription.getText().toString().trim();
        String offencePlace = offenceLocation.getText().toString().trim();

        if (fullNameDriver.isEmpty() || offenceDescription.isEmpty() || offencePlace.isEmpty()) {
            Toast.makeText(this, "Input validation errors", Toast.LENGTH_SHORT).show();
        } else {
            com.example.trafficmi.DriverOffenceRecords driverOffenceRecords;
            if (licenceNumber != null) {
                if (latitude == null && longitude == null) {
                    latitude = "";
                    longitude = "";
                }
                driverOffenceRecords = new com.example.trafficmi.DriverOffenceRecords(
                        fullNameDriver,
                        licenceNumber,
                        offencePlace,
                        offenceDescription,
                        radioSexButton.getText().toString(),
                        latitude,
                        longitude);

            } else {
                if (latitude == null && longitude == null) {
                    latitude = "";
                    longitude = "";
                }

                driverOffenceRecords = new com.example.trafficmi.DriverOffenceRecords(
                        fullNameDriver,
                        offencePlace,
                        offenceDescription,
                        radioSexButton.getText().toString(),
                        latitude,
                        longitude);
            }
            referenci.child(String.valueOf(System.currentTimeMillis())).setValue(driverOffenceRecords);

            progressBar.setVisibility(View.VISIBLE);

            i = progressBar.getProgress();
            new Thread(new Runnable() {
                public void run() {
                    while (i < 100) {
                        i += 1;
                        // Update the progress bar and display the current value in text view
                        hdlr.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(i);
                                txtView.setText(i + "/" + progressBar.getMax());

                            }
                        });
                        try {
                            // Sleep for 100 milliseconds to show the progress slowly.
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }).start();


            Toast.makeText(this,
                    "Records Successfully updated",
                    Toast.LENGTH_LONG)
                    .show();

            fullNameOfDriver.setText("");
            offenceLocation.setText("");

            scannedLicenseNum.setText("");

            driverOffenceDescription.setText("");
            //startActivity( new Intent(this, DriverOffenseDetail.class));

        }


        //Writing to database


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    latitude = location.getLatitude() + "";
                    longitude = location.getLongitude() + "";
                }
            });
        }

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_BARCODE_rESULTS && resultCode == RESULT_OK) {
            licenceNumber = data.getStringExtra("data");
            scannedLicenseNum.setText(licenceNumber);
            // Toast.makeText(this, licenceNumber, Toast.LENGTH_SHORT).show();
        }

    }
}