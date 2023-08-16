package com.example.wages.WorkerFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wages.Database;
import com.example.wages.Orders;
import com.example.wages.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerRequestFragment extends Fragment {
    TextView name;
    ImageView imageView;
    Button accept;
    DatabaseReference reference,reference1;
    LinearLayout linearLayout;
    FirebaseUser user;
    public WorkerRequestFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_worker_request, container, false);
        name = view.findViewById(R.id.nameOfCustomer);
        accept = view.findViewById(R.id.acceptRequest);
        linearLayout = view.findViewById(R.id.linear);
        user = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot sp : snapshot.getChildren()){
                        Orders orders = sp.getValue(Orders.class);
                        assert orders != null;
                        if(orders.getReceiver().equals(user.getUid())) {
                            name.setText(orders.getMyName());
                            linearLayout.setVisibility(View.VISIBLE);
                            if(orders.getStatus().equals("Waiting for Response")) {
                                accept.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onClick(View view) {
                                        reference = FirebaseDatabase.getInstance().getReference("Orders").child(orders.getMyId());
                                        reference.child("status").setValue("Worker hired");
                                        accept.setText("Mark as done");
                                    }
                                });
                            }
                            else if(orders.getStatus().equals("Worker hired")){
                                accept.setText("Mark as done");
                                accept.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onClick(View view) {
                                        reference = FirebaseDatabase.getInstance().getReference("Orders").child(orders.getMyId());
                                        reference.child("status").setValue("Done");
                                        accept.setText("Job Done");
                                    }
                                });
                            }
                            else {
                                accept.setText("Job Done");
                                reference = FirebaseDatabase.getInstance().getReference("Orders").child(orders.getMyId());
                                reference.child("review").setValue("pending");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}