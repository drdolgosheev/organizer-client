package com.hse.organazer_client.services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.zxing.Result;
import com.hse.organazer_client.activities.MainActivity;
import com.hse.organazer_client.activities.activity_add_drug;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scaner extends Activity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "SCANER: ";
    String token = "";
    String username = "";
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);// Set the scanner view as the content view

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            token = arguments.getString("token");
            username = arguments.getString("username");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        String code = rawResult.getText();
        String format =  rawResult.getBarcodeFormat().toString();
        // If you would like to resume scanning, call this method below:

        Intent intent = new Intent(getApplicationContext(), activity_add_drug.class);
        intent.putExtra("code", code);
        intent.putExtra("format", format);
        intent.putExtra("token", token);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
