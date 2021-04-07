package com.hse.organazer_client.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hse.organazer_client.R;
import com.hse.organazer_client.entities.dto.RegisterDtoUserToServer;
import com.hse.organazer_client.services.EmailValidator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private String REG_URL = "http://20.84.66.14:1335/api/v1/auth/register";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.editTextTextUsernameRegister);
        firstName = (EditText) findViewById(R.id.editTextPersonNameRegister);
        lastName = (EditText) findViewById(R.id.editTextSurnameRegister);
        email = (EditText) findViewById(R.id.editTextTextEmailAddressRegister);
        password = (EditText) findViewById(R.id.editTextPasswordRegister);
        regButton = (Button) findViewById(R.id.registerButton);

        regButton.setOnClickListener(v -> {
            if (username.getText().toString().isEmpty() ||
                    firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                    email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Не все поля заполненны",
                        Toast.LENGTH_SHORT).show();
            } else {
                RegisterDtoUserToServer user = new RegisterDtoUserToServer(username.getText().toString(),
                        firstName.getText().toString(), lastName.getText().toString(),
                        email.getText().toString(), password.getText().toString());
                register(user);
            }
        });
    }

    public Boolean register(RegisterDtoUserToServer user) {
        if (!EmailValidator.isValidEmailAddress(user.getEmail())) {
            Toast.makeText(RegisterActivity.this, "Невалидная почта",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        post(REG_URL, gson.toJson(user));
        return true;
    }

    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(RegisterActivity.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String response_loc = response.body().string();
                if (response.code() == 200) {
                    RegisterActivity.this.runOnUiThread(() -> {
                        System.out.println(response_loc);
                        Toast.makeText(RegisterActivity.this, response_loc, Toast.LENGTH_SHORT).show();
                        //todo: send data to next activity using AuthDtoFromServer and gson
//                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
//                                Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
                    });
                } else {
                    RegisterActivity.this.runOnUiThread(() -> {
                        System.out.println("Server response code" + response.code());
                        Toast.makeText(RegisterActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
