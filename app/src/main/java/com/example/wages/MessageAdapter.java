package com.example.wages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context myContext;
    private List<Chat> mychats;
    private String imageUrl;
    FirebaseUser user;
    public MessageAdapter(Context myContext,List<Chat> mychats,String imageUrl){
        this.mychats = mychats;
        this.myContext = myContext;
        this.imageUrl = imageUrl;
    }
    @NonNull
    @Override
    public MessageAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(myContext).inflate(R.layout.background_sent_message,parent,false);
            return new MessageAdapter.viewHolder(view);
        }
        else{
            View view = LayoutInflater.from(myContext).inflate(R.layout.background_received_message,parent,false);
            return new MessageAdapter.viewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.viewHolder holder, int position) {
        Chat mchat = mychats.get(position);
        holder.show_Message.setText(mchat.getMessage());
        Glide.with(myContext).load(imageUrl).into(holder.profile_img);
    }

    @Override
    public int getItemCount() {
        return mychats.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView show_Message;
        public ImageView profile_img;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            show_Message = itemView.findViewById(R.id.textMessage);
            profile_img = itemView.findViewById(R.id.profile_img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(mychats.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
