package com.example.wages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wages.Notification.APIService;
import com.example.wages.Notification.Client;
import com.example.wages.Notification.Data;
import com.example.wages.Notification.MyResponse;
import com.example.wages.Notification.Sender;
import com.example.wages.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    ImageButton send;
    EditText textToSend;
    FirebaseUser user;
    MessageAdapter adapter;
    List<Chat> myChats;
    RecyclerView recyclerView;
    DatabaseReference reference;
    String immage;
    String receiver1,workerId;
    APIService apiService;
    boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        receiver1 = intent.getStringExtra("UserName");
        workerId = intent.getStringExtra("userId");
        String ImageUrl = intent.getStringExtra("Image");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        recyclerView = findViewById(R.id.chat_recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        send = findViewById(R.id.send_btn);
        textToSend = findViewById(R.id.textSend);
        user = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/fcm/send/").create(APIService.class);


        send.setOnClickListener(v -> {
            notify = true;
            String message = textToSend.getText().toString();
            if(!message.equals("")){
                    sendMessage(user.getUid(),workerId,message);
            }
            textToSend.setText("");
//            notification();
        });

        readMessage(user.getUid(),workerId,ImageUrl);
    }
    private void sendMessage(String sender,String receiver,String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);
        databaseReference.child("Chat").push().setValue(map);


        final String msg = message;

    }
    private void sendNotification(String receiver ,String userName ,String message){
        Toast.makeText(this, "In send notification", Toast.LENGTH_SHORT).show();
        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(MessageActivity.this, "In event of token", Toast.LENGTH_SHORT).show();
                for(DataSnapshot sp : snapshot.getChildren()){
                    Toast.makeText(MessageActivity.this, "something", Toast.LENGTH_SHORT).show();
                    Token token = sp.getValue(Token.class);
                    assert token != null;
                    Toast.makeText(MessageActivity.this, token.getToken(), Toast.LENGTH_SHORT).show();
                    Data data = new Data(user.getUid(),R.drawable.ic_launcher_background,userName + ": "+message,"New Message"
                    ,receiver1);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                    Toast.makeText(MessageActivity.this,"Hello world", Toast.LENGTH_SHORT).show();
                                    if(response.code()==200){
                                        assert response.body() != null;
                                        if(response.body().success!=1){
                                            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                    Toast.makeText(MessageActivity.this, "Failedd", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void readMessage(String myId,String userid,String imageUrl){
        myChats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myChats.clear();
                for(DataSnapshot s : snapshot.getChildren()){
                    Chat c = s.getValue(Chat.class);
                    assert c != null;
                    if(c.getReceiver().equals(myId)&&c.getSender().equals(userid) ||
                            c.getReceiver().equals(userid) && c.getSender().equals(myId)){
                        myChats.add(c);
                    }
                    adapter = new MessageAdapter(MessageActivity.this,myChats,imageUrl);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}