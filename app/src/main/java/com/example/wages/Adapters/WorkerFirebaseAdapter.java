package com.example.wages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wages.Database;
import com.example.wages.R;
import com.example.wages.CustomerFragments.WorkerRecycleViewDetailActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkerFirebaseAdapter extends FirebaseRecyclerAdapter<Database, WorkerFirebaseAdapter.myViewHolder>{

    public WorkerFirebaseAdapter(@NonNull FirebaseRecyclerOptions<Database> options) {
        super(options);
    }

//    ON BIND VIEW HOLDER FUNCTION
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Database model) {

        holder.FullName.setText(model.getFullName());
        holder.Profession.setText(model.getProfession());
        Glide.with(holder.Profile.getContext()).load(model.getImage()).into(holder.Profile);

        holder.Profile.setOnClickListener(view -> {

            Context context = view.getContext();
            Intent intent = new Intent(context, WorkerRecycleViewDetailActivity.class);
            intent.putExtra("fullName", model.getFullName());
            intent.putExtra("userName", model.getUserName());
            intent.putExtra("email", model.getEmail());
            intent.putExtra("userId",model.getUserId());
            intent.putExtra("phoneNo", model.getPhoneNo());
            intent.putExtra("city", model.getCity());
            intent.putExtra("profession", model.getProfession());
            intent.putExtra("imageUrl", model.getImage());
            intent.putExtra("latitude", model.getLatitude().toString());
            intent.putExtra("longitude", model.getLongitude().toString());

            context.startActivity(intent);

        });

    }

//    ON CREATE VIEW HOLDER FUNCTION
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_worker_model_,parent,false);
        return new myViewHolder(view);
    }

//    my View HOLDER FUNCTION
    public static class myViewHolder extends RecyclerView.ViewHolder{

    ImageView Profile;
    TextView FullName, Profession;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            Profile = itemView.findViewById(R.id.profile);
            FullName = itemView.findViewById(R.id.full_name_textView);
            Profession = itemView.findViewById(R.id.profession_textView);
        }
    }
}
