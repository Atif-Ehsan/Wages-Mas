package com.example.wages;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class CommonFunction {

    private static final int ServiceErrorCode = 1001;

    public CommonFunction() {
    }

    public Boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
    }


    public Boolean isServiceAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (googleApiAvailability.isUserResolvableError(resultCode)) {
            Dialog dialog = googleApiAvailability.getErrorDialog((Activity) context, resultCode, ServiceErrorCode,
                    task -> Toast.makeText(context, "Dialog is cancelled bu user", Toast.LENGTH_SHORT).show());
            assert dialog != null;
            dialog.show();
        }
        else {
            Toast.makeText(context, "Play services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



}
