package com.hse.organazer_client.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hse.organazer_client.R;
import com.hse.organazer_client.entities.dto.AuthDtoFromServer;
import com.hse.organazer_client.entities.dto.AuthDtoToServer;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String AUTH_URL = "http://20.84.66.14:1335/api/v1/auth/login";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private Button auth_but;
    private EditText pass, login;
    private TextView registerRedirect;
    public static final String myPrefs = "myprefs";
    public static final String nameKeyUsername = "username";
    public static final String nameKeyToken = "token";
    SharedPreferences mySharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth_but = (Button) findViewById(R.id.auth_button);
        login = (EditText) findViewById(R.id.editTextTextUsername);
        pass = (EditText) findViewById(R.id.editTextPassword);
        registerRedirect = (TextView) findViewById(R.id.registerTextActivity);

        mySharedPreferences = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        if (mySharedPreferences.contains(nameKeyUsername)) {
            // если есть, то ставим значение этого ключа в EditText
        }
        registerRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
        auth_but.setOnClickListener(v -> {
            if (pass.getText().toString().isEmpty() || login.getText().toString().isEmpty()) {
                System.out.println(pass.toString());
                Toast.makeText(LoginActivity.this, "Пустой логин или пароль",
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    signIn(login.getText().toString(), pass.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        createNotificationChanel();
    }

    public void signIn(String email, String password) throws IOException {
        AuthDtoToServer authDtoToServer = new AuthDtoToServer(email, password);
        post(AUTH_URL, gson.toJson(authDtoToServer));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChanel(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "some_channel_id";
        CharSequence channelName = "Some Channel";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);
    }


    public void post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(LoginActivity.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String response_loc = response.body().string();
                if (response.code() == 200) {
                    LoginActivity.this.runOnUiThread(() -> {
                        System.out.println(response_loc);
                        //todo: send data to next activity using AuthDtoFromServer and gson
                        AuthDtoFromServer respJson = gson.fromJson(response_loc, AuthDtoFromServer.class);
                        saveText(respJson.getToken(), respJson.getUsername());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("token",respJson.getToken());
                        intent.putExtra("username",respJson.getUsername());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    LoginActivity.this.runOnUiThread(() -> {
                        System.out.println("Invalid login or password");
                        Toast.makeText(LoginActivity.this, "Invalid login or password", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    public void saveText(String token, String username) {
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        if(mySharedPreferences.contains(nameKeyUsername)){
            editor.remove(nameKeyUsername);
            editor.remove(nameKeyToken);
            editor.apply();
        }

        editor.putString(nameKeyToken, token);
        editor.putString(nameKeyUsername, username);
        editor.apply();
    }
}