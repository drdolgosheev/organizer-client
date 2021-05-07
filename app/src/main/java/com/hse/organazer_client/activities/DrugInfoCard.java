package com.hse.organazer_client.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.hse.organazer_client.R;
import com.hse.organazer_client.entities.dto.AuthDtoFromServer;
import com.hse.organazer_client.entities.dto.DatesDto;
import com.hse.organazer_client.entities.dto.DeleteFromMedKitDto;
import com.hse.organazer_client.entities.dto.DrugFullDto;
import com.hse.organazer_client.entities.dto.StringDto;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DrugInfoCard extends AppCompatActivity {
    // Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // Http and json tools
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();

    // Urls and tag
    String TAG = "GET_DRUG_CARD";
    String URL_GET_DRUG = "http://20.84.66.14:1335/api/v1/drug/getDrugByBarcode/";
    String URL_GET_TIME = "http://20.84.66.14:1335/api/v1/drug/getDrugsTakeTime/"; // + barcode
    String URL_GET_BARCODE = "http://20.84.66.14:1335/api/v1/drug/getBarcodeByName/"; // + name
    String URL_REMOVE_DRUG = "http://20.84.66.14:1335/api/v1/user/deleteDrugFromMedKit";

    // View
    CircleImageView image;
    TextView takePillsTime, userGroup, drugName, drugDescription,
            pillsLeft, startTakeDate, expireDate;
    ImageView closeButton;
    Button removeDrug;

    // Local data
    public static final String myPrefs = "myprefs";
    public static final String nameKeyUsername = "username";
    public static final String nameKeyToken = "token";
    SharedPreferences mySharedPreferences;
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_drug_info_card);

        image = findViewById(R.id.drug_image);
        takePillsTime = findViewById(R.id.drug_info_user_take_time_text_view);
        userGroup = findViewById(R.id.drug_info_user_group_text_view);
        drugName = findViewById(R.id.drug_info_name_textView);
        drugDescription = findViewById(R.id.drug_info_descr);
        pillsLeft = findViewById(R.id.drug_info_user_pills_num_text_view);
        startTakeDate = findViewById(R.id.drug_info_start_take_date);
        expireDate = findViewById(R.id.drug_info_user_exp_date_text_view);
        closeButton = findViewById(R.id.close_button);
        removeDrug = findViewById(R.id.drug_info_delete_button);

        String username = null, token = null, drugName = null;

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            drugName = arguments.getString("name");
            setPhoto(drugName);
        }

        mySharedPreferences = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        if (mySharedPreferences.contains(nameKeyUsername)) {  // arguments != null
            username = mySharedPreferences.getString(nameKeyUsername, ""); // arguments.getString("username");
            token = mySharedPreferences.getString(nameKeyToken, "");
            setText(drugName, username, token);
        }

        closeButton.setOnClickListener(v -> {
            finish();
        });

        String finalUsername = username;
        String finalToken = token;
        String finalDrugName = drugName;
        removeDrug.setOnClickListener(v -> {
            flag = true;
            getDrugBarcode(finalUsername, finalToken, finalDrugName);
        });
    }

    /**
     * Set photo
     *
     * @param drugName drug name
     */
    public void setPhoto(String drugName) {
        StorageReference riversRef = storageRef.child("test/" + drugName + ".jpeg");

        riversRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext())
                .load(uri)
                .into(image));
    }

    public void setText(String drugName, String username, String token) {
        getDrugBarcode(username, token, drugName);
    }

    /**
     * Get barcode by drug name
     * @param username username
     * @param token user token
     * @param drugName drug name
     */
    public void getDrugBarcode(String username, String token, String drugName) {
        String url = URL_GET_BARCODE + drugName;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(DrugInfoCard.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        StringDto barcode = gson.fromJson(data, StringDto.class);
                        getDrugData(barcode.getData(), token);
                        if(flag){
                            flag = false;
                            removeDrugFromMedKit(username, token, barcode.getData());
                        }
                    });
                } else {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        System.out.println("No drug with such name");
                        Toast.makeText(DrugInfoCard.this, "No drug with such name", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    /**
     * Gets drug data
     * @param barcode drug barcode
     * @param token user token
     */
    public void getDrugData(String barcode, String token) {
        Request request = new Request.Builder()
                .url(URL_GET_DRUG + barcode)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(DrugInfoCard.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        DrugFullDto dto = gson.fromJson(data, DrugFullDto.class);
                        if (dto != null) {
                            DateFormat df3 = new SimpleDateFormat("dd-MM-yyyy");
                            df3.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

                            DateFormat df2 = new SimpleDateFormat("HH:mm");
                            df3.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

                            drugDescription.setText(dto.getDescription());
                            drugName.setText(dto.getName());
                            userGroup.setText(String.format("Family member: %s", dto.getGroup()));
                            pillsLeft.setText(String.format("Pills left: %s", dto.getNumOfPills().toString()));
                            startTakeDate.setText(String.format("Start take date: %s", df3.format(dto.getStartTakePillsTime())));
                            expireDate.setText(String.format("Expire date: %s", df3.format(dto.getExpDate())));
//                            getDrugTakeTime(barcode, token);
                            StringBuilder result = new StringBuilder("Take time daily at: ");

                            for (int i = 0; i < dto.getNumOfPillsPerDay(); i++) {
                                Date takeTimeDate = new Date();
                                int step = (22-8)/dto.getNumOfPillsPerDay();
                                takeTimeDate.setHours(0);
                                takeTimeDate.setHours(8+step*(i+1));
                                takeTimeDate.setMinutes(0);
                                takeTimeDate.setSeconds(0);
                                Log.e(TAG, df2.format(takeTimeDate));
                                result.append(df2.format(takeTimeDate)).append(" ");
                            }
                            takePillsTime.setText(result.toString());
                        }
                    });
                } else {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        System.out.println("Something wrong code: " + response.code());
                    });
                }
            }
        });
    }

    /**
     * Get pills take time
     * @param barcode drug barcode
     * @param token user token
     */
    public void getDrugTakeTime(String barcode, String token) {
        Request request = new Request.Builder()
                .url(URL_GET_TIME + barcode)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(DrugInfoCard.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        DatesDto dates = gson.fromJson(data, DatesDto.class);
                        if (dates != null) {
                            DateFormat df3 = new SimpleDateFormat("HH:mm");
                            df3.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                            StringBuilder result = new StringBuilder("Take time daily at: ");

                            for (int i = 0; i < dates.getDate().size(); i++) {
                                Log.e(TAG, i + "onResponse: "+ df3.format(dates.getDate().get(i)));
                                result.append(df3.format(dates.getDate().get(i))).append("  ");
                            }
                            System.out.println(result.toString());
                            takePillsTime.setText(result.toString());
                        }
                    });
                } else {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        System.out.println("Something wrong code: " + response.code());
                    });
                }
            }
        });
    }

    /**
     * Method removes drug from user's med kit
     * @param username username
     * @param token user token
     * @param barcode drug barcode
     */
    public void removeDrugFromMedKit(String username, String token, String barcode) {
        DeleteFromMedKitDto deleteFromMedKitDto = new DeleteFromMedKitDto(username, barcode);
        RequestBody body = RequestBody.create(gson.toJson(deleteFromMedKitDto), JSON);
        Request request = new Request.Builder()
                .url(URL_REMOVE_DRUG)
                .addHeader("Authorization", "Bearer_" + token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(DrugInfoCard.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        Toast.makeText(DrugInfoCard.this, "Drug deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    DrugInfoCard.this.runOnUiThread(() -> {
                        System.out.println("Something went wrong");
                    });
                }
            }
        });
    }

}