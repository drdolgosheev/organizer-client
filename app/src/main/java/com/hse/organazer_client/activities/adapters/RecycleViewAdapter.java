package com.hse.organazer_client.activities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hse.organazer_client.R;
import com.hse.organazer_client.activities.DrugInfoCard;
import com.hse.organazer_client.entities.Drug;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.loopj.android.http.AsyncHttpClient.log;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private static final String TAG = "RecycleViewAdapter";;
    private List<Drug> drugs = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mGroup = new ArrayList<>();
    private ArrayList<String> mNextTakeTime = new ArrayList<>();
    private ArrayList<Integer> mPillsPerDay = new ArrayList<>();
    private ArrayList<Date> mStartTakeTime = new ArrayList<>();
    private ArrayList<Date> mStopTakeTime = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapter(List<Drug> drugs, ArrayList<String> mName, ArrayList<String> mGroup,
                              ArrayList<String> mNextTakeTime, ArrayList<Date> mStartTakeTime,
                              ArrayList<Date> mStopTakeTime, Context mContext, ArrayList<Integer> mPillsPerDay) {
        this.drugs = drugs;
        this.mName = mName;
        this.mGroup = mGroup;
        this.mNextTakeTime = mNextTakeTime;
        this.mStartTakeTime = mStartTakeTime;
        this.mStopTakeTime = mStopTakeTime;
        this.mContext = mContext;
        this.mPillsPerDay = mPillsPerDay;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG,"Is called.");
        int hoursInADay = 22-8;
        StorageReference riversRef = storageRef.child("test/" + mName.get(position) + ".jpeg");
        log.e(TAG, mName.get(position));
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(mContext)
                .load(uri)
                .into(holder.image));

        holder.drug_name.setText(mName.get(position));

//        if(mGroup.get(position).equals("Я")) {
//            holder.user_group.setBackgroundColor(Color.RED);
//        }

        holder.user_group.setText(mGroup.get(position));

        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        df2.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        df1.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        DateFormat df3 = new SimpleDateFormat("HH:mm");
        df2.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        List<Date> takePillsTimeList = new ArrayList<>();
        for (int i = 0; i < mPillsPerDay.get(position); i++) {
            Date takeTimeDate = new Date();
            int step = hoursInADay/mPillsPerDay.get(position);
            takeTimeDate.setHours(0);
            takeTimeDate.setHours(8+step*(i+1));
            takeTimeDate.setMinutes(0);
            takeTimeDate.setSeconds(0);
            Log.e(TAG, df2.format(takeTimeDate));
            takePillsTimeList.add(takeTimeDate);
        }


        for (int i = 0; i < mPillsPerDay.get(position); i++) {
            Date curDate = new Date();
            Log.e(TAG,"cur_date: "+df2.format(curDate));
            if(takePillsTimeList.get(i).after(curDate)){
                holder.next_take_time.setText("Nearest time today at: " + df3.format(takePillsTimeList.get(i)));
                break;
            }else {
                holder.next_take_time.setText("Next take time is tomorrow: " + df3.format(takePillsTimeList.get(0)));
            }
        }

        holder.stop_take_time.setText("Expire time date: " + df1.format(mStopTakeTime.get(position)));

        holder.start_take_time.setText("Start take date: " + df1.format(mStartTakeTime.get(position)));

        holder.layout.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DrugInfoCard.class);
                intent.putExtra("name", mName.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return drugs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView drug_name;
        TextView user_group;
        TextView start_take_time;
        TextView stop_take_time;
        TextView next_take_time;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.user_photo);
            drug_name = itemView.findViewById(R.id.drug_name);
            user_group = itemView.findViewById(R.id.user_group);
            start_take_time = itemView.findViewById(R.id.start_take_date);
            stop_take_time = itemView.findViewById(R.id.stop_take_date);
            next_take_time = itemView.findViewById(R.id.next_take_time);
            layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
