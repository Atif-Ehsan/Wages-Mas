package com.example.wages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wages.Database;
import com.example.wages.MessageActivity;
import com.example.wages.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context myContext;
    private List<Database> users;

    public UserAdapter(Context context , List<Database> users){
        this.myContext = context;
        this.users = users;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(myContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Database database = users.get(position);
        holder.user_name.setText(database.getFullName());
        Glide.with(myContext).load(database.getImage()).into(holder.imageView);
        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myContext, MessageActivity.class);
                intent.putExtra("UserName",database.getUserName());
                intent.putExtra("Image", database.getImage());
                intent.putExtra("userId",database.getUserId());
                myContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView user_name;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.profile_img);
        }
    }
}
