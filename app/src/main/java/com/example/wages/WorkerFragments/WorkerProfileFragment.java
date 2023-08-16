package com.example.wages.WorkerFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wages.CustomerFragments.CustomerHome;
import com.example.wages.Database;
import com.example.wages.LoginActivity;
import com.example.wages.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerProfileFragment extends Fragment {

    TextView FullName, UserName, Email, Profession, PhoneNo, City;
    ImageView Profile;
    Button SignOut;
    ProgressBar progressBar;

    public WorkerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_worker_profile, container, false);

        FindViewByID(view);

        progressBar.setVisibility(view.VISIBLE);

        showUserProfile(view);

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;

    }

    private void FindViewByID(View view) {

        progressBar = view.findViewById(R.id.progress_bar_profile);
        FullName = view.findViewById(R.id.profile_full_name_w);
        UserName = view.findViewById(R.id.profile_username_w);
        Email = view.findViewById(R.id.profile_email_w);
        Profession = view.findViewById(R.id.profile_profession_w);
        PhoneNo = view.findViewById(R.id.profile_phone_no_w);
        City = view.findViewById(R.id.profile_city_w);
        Profile = view.findViewById(R.id.profile_image_w);
        SignOut = view.findViewById(R.id.sing_out);
    }

    private void showUserProfile(View view) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();



        String UID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Database database = snapshot.getValue(Database.class);

                    if(database != null){

                        String fullName = database.getFullName();
                        String email = firebaseUser.getEmail();
                        String userName = database.getUserName();
                        String profession = database.getProfession();
                        String phoneNo = database.getPhoneNo();
                        String city = database.getCity();
                        String url = database.getImage();

                        Glide.with(getContext()).load(url).into(Profile);
                        FullName.setText(fullName);
                        UserName.setText(userName);
                        Email.setText(email);
                        Profession.setText(profession);
                        PhoneNo.setText(phoneNo);
                        City.setText(city);

                        progressBar.setVisibility(view.GONE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String Error = error.toString();
                Toast.makeText(getContext(), Error, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(view.GONE);
            }
        });
    }

}