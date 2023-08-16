package com.example.wages.CustomerFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.wages.Database;
import com.example.wages.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerProfileFragment extends Fragment {

    TextView FullName, UserName, Email, PhoneNo, City;
    ImageView Profile;
    ProgressBar progressBar;

    public CustomerProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        FindViewByID(view);

        progressBar.setVisibility(view.VISIBLE);

        showUserProfile(view);

        return view;
    }

    private void showUserProfile(View view) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();



        String UID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Database database = snapshot.getValue(Database.class);

                    if(database != null){

                        String fullName = database.getFullName();
                        String email = firebaseUser.getEmail();
                        String userName = database.getUserName();
                        String phoneNo = database.getPhoneNo();
                        String city = database.getCity();
                        String url = database.getImage();

                        Glide.with(getContext()).load(url).into(Profile);
                        FullName.setText(fullName);
                        UserName.setText(userName);
                        Email.setText(email);
                        PhoneNo.setText(phoneNo);
                        City.setText(city);

                        progressBar.setVisibility(view.GONE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FindViewByID(View view) {

        progressBar = view.findViewById(R.id.progress_bar_profile);
        FullName = view.findViewById(R.id.profile_full_name_c);
        UserName = view.findViewById(R.id.profile_username_c);
        Email = view.findViewById(R.id.profile_email_c);
        PhoneNo = view.findViewById(R.id.profile_phone_no_c);
        City = view.findViewById(R.id.profile_city_c);
        Profile = view.findViewById(R.id.profile_image_c);
    }
}