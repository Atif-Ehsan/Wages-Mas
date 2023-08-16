package com.example.wages.CustomerFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wages.Adapters.UserAdapter;
import com.example.wages.Chat;
import com.example.wages.Database;
import com.example.wages.Notification.Token;
import com.example.wages.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Database> workers;
    private List<String> users;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chatUserRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        users = new ArrayList<>();
        readUsers();

        return view;
    }

    private void readUsers() {
        FirebaseUser curentUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customers").child(curentUser.getUid());
//        String UserName = databaseReference.child("userName").toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot sp : snapshot.getChildren()){
                    Chat db = sp.getValue(Chat.class);
                    if(db.getSender().equals(curentUser.getUid())) {
                        users.add(db.getReceiver());
                    }
                    if(db.getReceiver().equals(curentUser.getUid())){
                        users.add(db.getSender());
                    }
                }
//                for(String s : users){
//                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
//                }
                readChats();
//                userAdapter = new UserAdapter(getContext(),workers);
//                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        workers = new ArrayList<Database>();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Workers");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                workers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Database db1 = dataSnapshot.getValue(Database.class);
                    for(String id : users){
                        if(db1.getUserId().equals(id)){
                            if(workers.size()!=0){
                                for(Database db2 : workers){
                                    if(!db1.getUserName().equals(db2.getUserName())){
                                        workers.add(db1);
                                    }
                                }
                            }
                            else{
                                workers.add(db1);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(),workers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}