package com.hse.organazer_client.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hse.organazer_client.R;
import com.hse.organazer_client.entities.Drug;
import com.hse.organazer_client.entities.User;
import com.hse.organazer_client.entities.dto.AuthDtoFromServer;
import com.hse.organazer_client.entities.dto.DrugFullDto;
import com.hse.organazer_client.entities.dto.SingleBooleanDto;
import com.hse.organazer_client.entities.dto.addDrugToMedKitDto;
import com.hse.organazer_client.entities.dto.drugSimpleDto;
import com.hse.organazer_client.services.Scaner;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

public class activity_add_drug extends AppCompatActivity {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    ImageView scanner;
    EditText barcode;
    String TAG = "ADD_DRUG";
    String URL_GET_DRUG = "http://20.84.66.14:1335/api/v1/drug/getDrugByBarcode/";
    String URL_ADD_DRUG = "http://20.84.66.14:1335/api/v1/drug/addDrugToMedKit";
    String URL_VALIDATE_DRUG = "http://20.84.66.14:1335/api/v1/drug/validate/";
    EditText drug_name, drug_description, drug_start_take_time, drug_expire_date, num_of_pills;
    EditText num_of_pills_per_day;
    ImageView validate, validate_ok, validate_not_ok;
    String token = "";
    String code = "";
    String username;
    Button add;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Transparent);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_drug);

        add = findViewById(R.id.button_add_drug);
        scanner = findViewById(R.id.photo_but_scaner);
        barcode = findViewById(R.id.editTextBarCode);
        drug_name = findViewById(R.id.editTextDrugNameAddDrug);
        drug_description = findViewById(R.id.editTextDrugDescription);
        drug_start_take_time = findViewById(R.id.editTextStartTakeTime);
        drug_expire_date = findViewById(R.id.editTextExpireDate);
        num_of_pills = findViewById(R.id.editTextNumberInBox);
        num_of_pills_per_day = findViewById(R.id.editTextNumberOfPillsPerDay);
        validate = findViewById(R.id.imageView_validate);
        validate_ok = findViewById(R.id.imageView_original);
        validate_not_ok = findViewById(R.id.imageView_notOriginal);

        validate_ok.setVisibility(View.INVISIBLE);
        validate_not_ok.setVisibility(View.INVISIBLE);

        scanner.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Scaner.class);
            intent.putExtra("token", token);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            try {
                code = arguments.getString("code");
                String format = arguments.getString("format");
                log.e(TAG, "Bar code scanned successfully: " + code);
                barcode.setText(code);
            } catch (Exception e) {
                log.e(TAG, "Waiting for barcode");
            }
            username = arguments.getString("username");
            token = arguments.getString("token");
            log.e(TAG, username);
        }

        validate.setOnClickListener(v -> {
            validateDrug(barcode.getText().toString(), token);
        });

        add.setOnClickListener(v -> {
            log.e(TAG, (String.valueOf(barcode.getText())));
            if (!(barcode.getText().toString().equals("") || drug_description.getText().toString().equals("") ||
                    drug_expire_date.getText().toString().equals("") || drug_start_take_time.getText().toString().equals("") ||
                    drug_name.getText().toString().equals("") || num_of_pills.getText().toString().equals("") ||
                    num_of_pills_per_day.getText().toString().equals(""))) {
                log.e(TAG, (String.valueOf(barcode.getText().equals(null))));
                drugSimpleDto drug = new drugSimpleDto();
                drug.setName(drug_name.getText().toString());
                drug.setBarcode(barcode.getText().toString());
                drug.setDescription(drug_description.getText().toString());
                drug.setNumOfPills(Integer.valueOf(num_of_pills.getText().toString()));
                drug.setNumOfPillsPerDay(Integer.valueOf(num_of_pills_per_day.getText().toString()));
                drug.setTakePillsInterval(Integer.valueOf(num_of_pills_per_day.getText().toString()));

                final String OLD_FORMAT = "dd.MM.yyyy";
                final String NEW_FORMAT = "yyyy-MM-dd";

                SimpleDateFormat fromUser = new SimpleDateFormat(OLD_FORMAT);
                SimpleDateFormat myFormat = new SimpleDateFormat(NEW_FORMAT);
                try {
                    String newDate;

                    newDate = myFormat.format(fromUser.parse(drug_expire_date.getText().toString()));
                    log.e("TAG", newDate);
                    drug.setExpDate(newDate);
                    newDate  = myFormat.format(fromUser.parse(drug_start_take_time.getText().toString()));
                    drug.setStartTakePillsTime(newDate);
                    newDate  = myFormat.format(fromUser.parse(drug_start_take_time.getText().toString()));
                    drug.setProdDate(newDate);
                } catch (Exception e) {
                    Toast.makeText(activity_add_drug.this, "Invalid form of data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                addDataToMedKit(drug, username, token);
            } else {
                Toast.makeText(activity_add_drug.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validateDrug(String barcode, String token) {
        Request request = new Request.Builder()
                .url(URL_VALIDATE_DRUG + barcode)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(activity_add_drug.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    activity_add_drug.this.runOnUiThread(() -> {
                        System.out.println(data);
                        Boolean dto = gson.fromJson(data, Boolean.class);

                        if (dto) {
                            validate_ok.setVisibility(View.VISIBLE);
                            Toast.makeText(activity_add_drug.this, "Drug is original", Toast.LENGTH_SHORT).show();
                            getDrugData(barcode, token);
                        } else {
                            validate_ok.setVisibility(View.INVISIBLE);
                            validate_not_ok.setVisibility(View.VISIBLE);
                            Toast.makeText(activity_add_drug.this, "Drug is NOT original", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    activity_add_drug.this.runOnUiThread(() -> {
                        System.out.println("Invalid token");
                        Toast.makeText(activity_add_drug.this, "There is no drug in our DB", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    public void getDrugData(String barcode, String token) {
        Request request = new Request.Builder()
                .url(URL_GET_DRUG + barcode)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(activity_add_drug.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    activity_add_drug.this.runOnUiThread(() -> {
                        DrugFullDto dto = gson.fromJson(data, DrugFullDto.class);
                        if (dto != null) {
                            drug_name.setText(dto.getName());
                            drug_description.setText(dto.getDescription());
                            num_of_pills.setText(dto.getNumOfPills().toString());
                        }
                    });
                } else {
                    activity_add_drug.this.runOnUiThread(() -> {
                        System.out.println("Invalid token");
                        Toast.makeText(activity_add_drug.this, "Invalid token", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    public void addDataToMedKit(drugSimpleDto drug, String username, String token) {
        addDrugToMedKitDto dtoToServer = new addDrugToMedKitDto(drug, username);
        String json = gson.toJson(dtoToServer);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(URL_ADD_DRUG)
                .addHeader("Authorization", "Bearer_" + token)
                .post(body)
                .build();
        log.e(TAG, json);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(activity_add_drug.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (response.code() == 200) {
                    activity_add_drug.this.runOnUiThread(() -> {
                        Toast.makeText(activity_add_drug.this, "Drug added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("token", token);
                        intent.putExtra("username", username);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    activity_add_drug.this.runOnUiThread(() -> {
                        log.e(TAG, "Msg: " + response.message() + " Code:" +response.code());
                        Toast.makeText(activity_add_drug.this, "Some issues with server", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}