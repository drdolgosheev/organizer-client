package com.hse.organazer_client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.hse.organazer_client.R;
import com.hse.organazer_client.services.Scaner;
import com.hse.organazer_client.services.SimpleScannerActivity;

import static com.loopj.android.http.AsyncHttpClient.log;

public class activity_add_drug extends AppCompatActivity {
    ImageView scaner;
    EditText barcode;
    String TAG = "ADD_DRUG";
    String URL_GET_DRUG = "http://20.84.66.14:1335/api/v1/drug/getDrugByBarcode/";
    String URL_ADD_DRUG = "http://20.84.66.14:1335/api/v1/drug/addDrugToMedKit";
    String URL_VALIDATE_DRUG = "http://20.84.66.14:1335/api/v1/drug/validate/";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Transparent);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_drug);

        scaner = findViewById(R.id.photo_but_scaner);
        barcode = findViewById(R.id.editTextBarCode);

        scaner.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Scaner.class);
            startActivity(intent);
        });

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String code = arguments.getString("code");
            String format = arguments.getString("format");
            log.e(TAG, "Bar code scanned successfully: " + code);
            barcode.setText(code);
            getDrugData();
        }


    }

    public void validateDrug(String barcode){

    }

    public void getDrugData(){

    }
}