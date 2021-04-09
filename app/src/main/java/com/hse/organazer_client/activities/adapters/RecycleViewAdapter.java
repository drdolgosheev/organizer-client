package com.hse.organazer_client.activities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hse.organazer_client.R;
import com.hse.organazer_client.entities.Drug;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder >{
    private static final String TAG = "RecycleViewAdapter";;

    private List<Drug> drugs = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mGroup = new ArrayList<>();
    private ArrayList<String> mNextTakeTime = new ArrayList<>();
    private ArrayList<Date> mStartTakeTime = new ArrayList<>();
    private ArrayList<Date> mStopTakeTime = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapter(List<Drug> drugs, ArrayList<String> mName, ArrayList<String> mGroup,
                              ArrayList<String> mNextTakeTime, ArrayList<Date> mStartTakeTime,
                              ArrayList<Date> mStopTakeTime, Context mContext) {
        this.drugs = drugs;
        this.mName = mName;
        this.mGroup = mGroup;
        this.mNextTakeTime = mNextTakeTime;
        this.mStartTakeTime = mStartTakeTime;
        this.mStopTakeTime = mStopTakeTime;
        this.mContext = mContext;
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

        int real_pos = position + 1;
        Log.e("POS", position + " ");
//        StorageReference riversRef = storageRef.child("images/" + fb_user.getEmail() + "/" + real_pos+".jpeg");
//        riversRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Glide.with(mContext)
//                        .load(uri)
//                        .into(holder.image);
//            }
//        });

        holder.drug_name.setText(mName.get(position));

        if(mGroup.get(position).equals("Я")) {
            holder.user_group.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_corner_slim_green));

        }

        holder.user_group.setText(mGroup.get(position));

        holder.start_take_time.setText("Start take date: " + ((Integer)mStartTakeTime.get(position)
                .getDate()).toString() + "." + ((Integer)mStartTakeTime.get(position)
                .getMonth()).toString() + "." + ((Integer)mStartTakeTime.get(position)
                .getYear()).toString());

        holder.stop_take_time.setText("Stop take date: " + ((Integer)mStopTakeTime.get(position)
                .getDate()).toString() + "." + ((Integer)mStopTakeTime.get(position)
                .getMonth()).toString() + "." + ((Integer)mStopTakeTime.get(position)
                .getYear()).toString());

        holder.layout.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, problemCard.class);
//                intent.putExtra("position", position);
//                intent.putExtra("user", true);
//                mContext.startActivity(intent);
//                Toast.makeText(mContext,"Статус: " + Problem.getStatus(mStatus.get(position)), Toast.LENGTH_LONG).show();
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
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.drug_image);
            drug_name = itemView.findViewById(R.id.drug_name);
            user_group = itemView.findViewById(R.id.user_group);
            start_take_time = itemView.findViewById(R.id.start_take_date);
            stop_take_time = itemView.findViewById(R.id.stop_take_date);
            layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
