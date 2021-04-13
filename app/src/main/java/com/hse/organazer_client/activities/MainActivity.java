package com.hse.organazer_client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.hse.organazer_client.R;
import com.hse.organazer_client.activities.adapters.RecycleViewAdapter;
import com.hse.organazer_client.entities.Drug;
import com.hse.organazer_client.entities.dto.AuthDtoFromServer;
import com.hse.organazer_client.entities.dto.ListDrugDto;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addDrugButton;
    List<Drug> drugs = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mGroup = new ArrayList<>();
    private ArrayList<String> mNextTakeTime = new ArrayList<>();
    private ArrayList<Date> mStartTakeTime = new ArrayList<>();
    private ArrayList<Date> mStopTakeTime = new ArrayList<>();
    private ArrayList<Integer> pillPerDay = new ArrayList<>();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private String GETMEDKIT_URL = "http://20.84.66.14:1335/api/v1/user/get/medKit/";
    String token = "";
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.ResycleView);
        addDrugButton = (FloatingActionButton) findViewById(R.id.floatingActionButton_addDrug);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            username = arguments.getString("username");
            token = arguments.getString("token");
            GETMEDKIT_URL = GETMEDKIT_URL + username;
            get(GETMEDKIT_URL, token);
        }

        addDrugButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, activity_add_drug.class);
            intent.putExtra("token", token);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

//    private void InitBitMap(String username, String token){
//        db.collection("users").document(mail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Log.e("TAG", mail);
//                cur_user = documentSnapshot.toObject(User.class);
//                if (cur_user.getProblemCount() != 0) {
//                    problemList = cur_user.getProblems();
//
//                    for (counter = 1; counter <= cur_user.getProblemCount(); counter++) {
//                        Log.e("COUNTER1", " " + counter);
//                        Log.e("PROB1", " " + cur_user.getProblemCount());
//                        mImagesName.add(problemList.get(counter - 1).getName());
//                        Log.e("mName: ", mImagesName.toString());
//                        mDescr.add(problemList.get(counter - 1).getDescription());
//                        // Тут возможно что то не так
//                        mStatus.add(problemList.get(counter - 1).getStatus());
//                    }
//
//
//                }
//            }
//        });
//    }

    private void initRecyclerView(){
        RecycleViewAdapter adapter = new RecycleViewAdapter(drugs, mName,mGroup,mNextTakeTime,mStartTakeTime,mStopTakeTime, this, pillPerDay);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void get(String url, String token){
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer_" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MainActivity.this, "Connection to the server is refused", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                if (response.code() == 200) {
                    MainActivity.this.runOnUiThread(() -> {
                        Drug[] drugs_ans = gson.fromJson(data, Drug[].class);

                        for (Drug drug : drugs_ans) {
                            mName.add(drug.getName());
                            mGroup.add("Я");
                            mNextTakeTime.add("Next take time:" + "12.00");
                            mStartTakeTime.add(drug.getStartTakePillsTime());
                            mStopTakeTime.add(drug.getStartTakePillsTime()); // Доделать
                            pillPerDay.add(drug.getNumOfPillsPerDay());
                            drugs.add(drug);
                        }
                        initRecyclerView();
                    });
                } else {
                    MainActivity.this.runOnUiThread(() -> {
                        System.out.println("Invalid token");
                        Toast.makeText(MainActivity.this, "Invalid token", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}