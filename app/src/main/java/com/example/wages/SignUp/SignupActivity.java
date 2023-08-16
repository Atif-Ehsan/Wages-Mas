package com.example.wages.SignUp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.example.wages.CommonFunction;
import com.example.wages.LoginActivity;
import com.example.wages.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-------------------Remove menu bar code----------------
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_signup);



        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frameLayout, new CustomerSignupFragment());
        ft.commit();
//
//        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();
//
        getLocationPermission();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.customer) {
                FragmentManager fmc = getSupportFragmentManager();
                FragmentTransaction ftc = fmc.beginTransaction();
                ftc.replace(R.id.frameLayout, new CustomerSignupFragment());
                ftc.commit();
                return true;
            }
            if (item.getItemId() == R.id.worker){
                FragmentManager fmw = getSupportFragmentManager();
                FragmentTransaction ftw = fmw.beginTransaction();
                ftw.replace(R.id.frameLayout, new WorkerSignupFragment());
                ftw.commit();
                return true;
            }
            return false;
        });

    }

    private void getLocationPermission(){

        CommonFunction commonFunction = new CommonFunction();

        // CHECK IS INTERNET CONNECTION IS AVAILABLE OR NOT
        if(commonFunction.isConnected(getApplicationContext())){

            // CHECK IS GOOGLE SERVICES ARE AVAILABLE OR NOT
            if(commonFunction.isServiceAvailable(getApplicationContext())) {

                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                // CHECK IS GPS IS ON OR OF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    // CHECK THE LOCATION PERMISSION
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    else {

                        // IF LOCATION PERMISSION IS NOT ALLOWED
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                        }

                    }

                }
                else{

                    // IF GPS IS OFF
                    new AlertDialog.Builder(this)
                            .setIcon(R.drawable.p_location)
                            .setTitle("GPS Permission")
                            .setMessage("GPS is required for this app. Please enabled GPS.")

                            .setPositiveButton("yes", (dialogInterface, i) -> startActivityForResult(new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0))

                            .setNegativeButton("No", (dialogInterface, i) -> {
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            })
                            .setCancelable(false).create().show();
                }
            }

        }
        else{

            // IF INTERNET CONNECTION IS NOT AVAILABLE
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NO INTERNET CONNECTION");
            builder.setMessage("Internet connection is not available pleas check your internet connection ")

                    .setPositiveButton("Connect", (dialogInterface, i) -> startActivityForResult(new Intent(
                            Settings.ACTION_WIFI_SETTINGS), 0))

                    .setNegativeButton("No", (dialog, which) -> System.exit(0));

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }


    // FUNCTION USE FOR REQUEST FOR THE LOCATION PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Intent intent=new Intent();
            intent.setClass(this, this.getClass());
            this.startActivity(intent);
            this.finish();

        }
        else{

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Permission Required")
                    .setMessage("Permission is need to get your location because it required for map")
                    .setPositiveButton("Ok", (dialogInterface, i) -> startActivityForResult(new Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null)
                            ), 0))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        finish();
                        System.exit(0);
                        dialogInterface.dismiss();
                    })
                    .setCancelable(false).create().show();

        }
    }



    // FOR RESULT OF THE INTENT ACTIVITY
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            Intent intent=new Intent();
            intent.setClass(this, this.getClass());
            this.startActivity(intent);
            this.finish();

        }

    }


}
