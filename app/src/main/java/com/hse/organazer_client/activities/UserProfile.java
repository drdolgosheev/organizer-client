package com.hse.organazer_client.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.hse.organazer_client.R;
import com.hse.organazer_client.entities.dto.BooleanDto;
import com.hse.organazer_client.entities.dto.ChangePasswordDto;
import com.hse.organazer_client.entities.dto.ShortUserDto;
import com.hse.organazer_client.entities.dto.addDrugToMedKitDto;
import com.hse.organazer_client.entities.dto.drugSimpleDto;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

public class UserProfile extends AppCompatActivity {

    // View
    TextView upload, fullName, emailTag, usernameTag;
    Button updatePassword;
    CircleImageView userPhoto;
    ImageView backButton;
    EditText oldPassword, newPassword, newPasswordConfirmation;

    // Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // Http and json tools
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    String TAG = "UserProfile";
    String URL_GET_USER = "http://20.84.66.14:1335/api/v1/user/getUser/"; // + username
    String URL_EQUALS_PASS = "http://20.84.66.14:1335/api/v1/user/checkPasswordEquals";
    String URL_CHANGE_PASS = "http://20.84.66.14:1335/api/v1/user/changePassword";

    // Local data
    public static final String myPrefs = "myprefs";
    public static final String nameKeyUsername = "username";
    public static final String nameKeyToken = "token";
    SharedPreferences mySharedPreferences;

    // Some local data
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        upload = findViewById(R.id.profile_upload_photo);
        fullName = findViewById(R.id.profile_first_name_tag);
        emailTag = findViewById(R.id.profile_email_tag);
        usernameTag = findViewById(R.id.profile_username_tag);
        updatePassword = findViewById(R.id.profile_update_button);
        userPhoto = findViewById(R.id.user_photo);
        backButton = findViewById(R.id.profile_back_imageView);
        oldPassword = findViewById(R.id.profile_old_password);
        newPassword = findViewById(R.id.profile_new_password);
        newPasswordConfirmation = findViewById(R.id.profile_new_password_confirmation);

        String username = null, token = null;
        mySharedPreferences = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        if (mySharedPreferences.contains(nameKeyUsername)) {  // arguments != null
            username = mySharedPreferences.getString(nameKeyUsername, ""); // arguments.getString("username");
            token = mySharedPreferences.getString(nameKeyToken, "");
            getContent(token, username);
        }

        backButton.setOnClickListener(v -> {
            finish();
        });

        String finalUsername = username;
        String finalToken = token;
        updatePassword.setOnClickListener(v -> {
            if (!(oldPassword.getText().toString().isEmpty() ||
                    newPassword.getText().toString().isEmpty() ||
                    newPasswordConfirmation.getText().toString().isEmpty())){
                if(newPasswordConfirmation.getText().toString().equals(newPassword.getText().toString())){
                    ChangePasswordDto dtoEquals = new ChangePasswordDto();
                    dtoEquals.setPassword(oldPassword.getText().toString());
                    dtoEquals.setUsername(finalUsername);
                    assertEqualsOldPassword(dtoEquals, finalToken, newPassword.getText().toString(),
                            finalUsername);
                }else {
                    Toast.makeText(UserProfile.this, "Password confirmation is invalid",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(UserProfile.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getContent(String token, String username){
        String url = URL_GET_USER + username;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(UserProfile.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    UserProfile.this.runOnUiThread(() -> {
                        Log.e(TAG, "onResponse: User data uploaded");
                        ShortUserDto userDto = gson.fromJson(data, ShortUserDto.class);

                        fullName.setText("Full name: " + userDto.getFirstName() + " " + userDto.getLastName());
                        usernameTag.setText("Username: " + userDto.getUsername());
                        emailTag.setText("Email: " + userDto.getEmail());
                        setPhoto(username);
                    });
                } else {
                    UserProfile.this.runOnUiThread(() -> {
                        Log.e(TAG, "onResponse: server code: " + response.code() );
                    });
                }
            }
        });
    }

    /**
     * Set user profile photo
     *
     * @param username username
     */
    public void setPhoto(String username) {
        StorageReference riversRef = storageRef.child("users/" + username + ".jpeg");

        riversRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext())
                .load(uri)
                .into(userPhoto));
    }

    public void assertEqualsOldPassword(ChangePasswordDto dtoToServer, String token, String newPassword,
                                        String username){
        String json = gson.toJson(dtoToServer);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(URL_EQUALS_PASS)
                .addHeader("Authorization", "Bearer_" + token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(UserProfile.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    UserProfile.this.runOnUiThread(() -> {
                        BooleanDto booleanDto = gson.fromJson(data, BooleanDto.class);
                        if(!booleanDto.getFlag()){
                            Toast.makeText(UserProfile.this, "Invalid old password", Toast.LENGTH_SHORT).show();
                        }
                        // Если страй пароль совпал
                        if(booleanDto.getFlag()) {
                            ChangePasswordDto dtoChangePass = new ChangePasswordDto();
                            dtoChangePass.setUsername(username);
                            dtoChangePass.setPassword(newPassword);
                            changePassword(dtoChangePass, token);
                        }else {
                            Toast.makeText(UserProfile.this, "Old password is invalid",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    UserProfile.this.runOnUiThread(() -> {
                        log.e(TAG, "Msg: " + response.message() + " Code:" +response.code());
                    });
                }
            }
        });
    }

    public void changePassword(ChangePasswordDto dtoToServer, String token) {
        String json = gson.toJson(dtoToServer);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(URL_CHANGE_PASS)
                .addHeader("Authorization", "Bearer_" + token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(UserProfile.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (response.code() == 200) {
                    UserProfile.this.runOnUiThread(() -> {
                        BooleanDto booleanDto = gson.fromJson(resp, BooleanDto.class);
                        if(booleanDto.getFlag()) {
                            Toast.makeText(UserProfile.this, "Password updated", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UserProfile.this, "Can not update password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    UserProfile.this.runOnUiThread(() -> {
                        log.e(TAG, "Msg: " + response.message() + " Code:" +response.code());
                    });
                }
            }
        });
    }
}