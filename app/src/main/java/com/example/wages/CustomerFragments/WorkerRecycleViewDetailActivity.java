package com.example.wages.CustomerFragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wages.Database;
import com.example.wages.MessageActivity;
import com.example.wages.Notification.Data;
import com.example.wages.Orders;
import com.example.wages.R;
import com.example.wages.Review;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerRecycleViewDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private final String[] professionList = new String[] {"Select Rating","*", "**", "***", "****", "*****"};
    private String fullName, userName, email, phoneNo, city, profession, imageUrl,workerId,logedFullName, latitudeW, longitudeW;
    private  Double latitudeDW, longitudeDW;
    Button button;
    Spinner review;
    TextView FullName, UserName, Email, PhoneNo, Profession,Detail,onlyOnce;
    FloatingActionButton chatButton;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_recycle_view_detail);

        Bundle extras = getIntent().getExtras();
        fullName = extras.getString("fullName");
        userName = extras.getString("userName");
        email = extras.getString("email");
        phoneNo = extras.getString("phoneNo");
        city = extras.getString("city");
        profession = extras.getString("profession");
        imageUrl = extras.getString("imageUrl");
        workerId = extras.getString("userId");
        Toast.makeText(this, workerId, Toast.LENGTH_SHORT).show();
        latitudeW = extras.getString("latitude");
        longitudeW = extras.getString("longitude");

        user = FirebaseAuth.getInstance().getCurrentUser();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync((OnMapReadyCallback) this);

        FindViewById();

        SetPersonalData(fullName, userName, email, phoneNo, profession);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.profession_spinner_model, professionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        review.setAdapter(adapter);
        review.setOnItemSelectedListener(this);


        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerRecycleViewDetailActivity.this, MessageActivity.class);
                intent.putExtra("UserName",userName);
                intent.putExtra("Image",imageUrl);
                intent.putExtra("userId",workerId);
                startActivity(intent);
            }
        });



        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customers");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot sp: snapshot.getChildren()){
                        Database db = sp.getValue(Database.class);
                        if(user.getUid().equals(db.getUserId())){
                            logedFullName= db.getFullName();
                            Toast.makeText(WorkerRecycleViewDetailActivity.this, logedFullName, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference revv = FirebaseDatabase.getInstance().getReference("Reviews");
        revv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sp: snapshot.getChildren()){
                    Review rv = sp.getValue(Review.class);
                    if(rv.getWorkerid().equals(workerId)){
                        onlyOnce.setText(rv.getRating());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot sp : snapshot.getChildren()){
                        Orders orders = sp.getValue(Orders.class);
                        if(orders.getReceiver().equals(workerId)){
                            button.setVisibility(View.GONE);
                            Detail.setText(orders.getStatus());
                            Detail.setVisibility(View.VISIBLE);
                        }
                        else{
                            onlyOnce.setText("");
                        }
                        if(orders.getStatus().equals("Done")){
                            onlyOnce.setText("Review are not editable");
                            review.setVisibility(View.VISIBLE);
//                            Toast.makeText(WorkerRecycleViewDetailActivity.this, review.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Orders order = new Orders(user.getUid(),workerId,logedFullName,"Waiting for Response");
                reference.child("Orders").child(user.getUid()).setValue(order);
                button.setVisibility(View.GONE);
                Detail.setVisibility(View.VISIBLE);
                Detail.setText("Waiting for Response");
            }
        });


    }

    private void FindViewById() {

        FullName = findViewById(R.id.full_name);
        UserName = findViewById(R.id.user_name);
        Email = findViewById(R.id.email);
        PhoneNo = findViewById(R.id.phone_no);
        Profession = findViewById(R.id.profession);
        chatButton = findViewById(R.id.floatingActionButton);
        button = findViewById(R.id.hireButton);
        Detail = findViewById(R.id.detailView);
        review = findViewById(R.id.reviewStars);
        onlyOnce = findViewById(R.id.onlyOnce);
    }

    private void SetPersonalData(String fullName, String userName, String email, String phoneNo, String profession) {


        FullName.setText(fullName);
        UserName.setText(userName);
        Email.setText(email);
        PhoneNo.setText(phoneNo);
        Profession.setText(profession);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        String UID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Database database = snapshot.getValue(Database.class);

                    if (database != null) {

                        Double latitudeDC = database.getLatitude();
                        Double longitudeDC = database.getLongitude();

                        latitudeDW = Double.parseDouble(latitudeW);
                        longitudeDW = Double.parseDouble(longitudeW);

                        GoogleMap mMap = googleMap;


                        LatLng Customer = new LatLng(latitudeDC, longitudeDC);
                        mMap.addMarker(new MarkerOptions().position(Customer).title("CUSTOMER"));

                        LatLng Worker = new LatLng(latitudeDW, longitudeDW);
                        mMap.addMarker(new MarkerOptions().position(Worker).title("WORKER"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Worker, 15));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                ref.child("Reviews").child(user.getUid()).setValue(1);
//                review.setVisibility(View.GONE);
                break;
            case 1:
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                Review rev = new Review(workerId,"1");
                ref1.child("Reviews").child(workerId).setValue(rev);
                review.setVisibility(View.GONE);
                onlyOnce.setText("You rated this customer by : 1 star");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot sp : snapshot.getChildren()){
                            Orders or = sp.getValue(Orders.class);
                            if(or.getReceiver().equals(workerId)){
                                Toast.makeText(WorkerRecycleViewDetailActivity.this, "hello", Toast.LENGTH_SHORT).show();
                                reference1.child("Orders1").child(user.getUid()).setValue(or);
                                sp.getRef().removeValue();
                            }
//                            reference1.setValue()
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case 2:
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                Review rev1 = new Review(workerId,"2");
                ref2.child("Reviews").child(workerId).setValue(rev1);
                review.setVisibility(View.GONE);
                onlyOnce.setText("You rated this customer by : 2 star");
                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Orders");
                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot sp : snapshot.getChildren()){
                            Orders or = sp.getValue(Orders.class);
                            if(or.getReceiver().equals(workerId)){
                                Toast.makeText(WorkerRecycleViewDetailActivity.this, "hello", Toast.LENGTH_SHORT).show();
                                reference3.child("Orders1").child(user.getUid()).setValue(or);
                                sp.getRef().removeValue();
                            }
//                            reference1.setValue()
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case 3:
                DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference();
                Review rev2 = new Review(workerId,"3");
                ref3.child("Reviews").child(workerId).setValue(rev2);
                review.setVisibility(View.GONE);
                onlyOnce.setText("You rated this customer by : 3 star");
                DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("Orders");
                DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference();
                reference4.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot sp : snapshot.getChildren()){
                            Orders or = sp.getValue(Orders.class);
                            if(or.getReceiver().equals(workerId)){
                                reference5.child("Orders1").child(user.getUid()).setValue(or);
                                sp.getRef().removeValue();
                            }
//                            reference1.setValue()
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case 4:
                DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference();
                Review rev3 = new Review(workerId,"4");
                ref4.child("Reviews").child(workerId).setValue(rev3);
                review.setVisibility(View.GONE);
                onlyOnce.setText("You rated this customer by : 4 star");
                DatabaseReference reference6 = FirebaseDatabase.getInstance().getReference("Orders");
                DatabaseReference reference7 = FirebaseDatabase.getInstance().getReference();
                reference6.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot sp : snapshot.getChildren()){
                            Orders or = sp.getValue(Orders.class);
                            if(or.getReceiver().equals(workerId)){
                                Toast.makeText(WorkerRecycleViewDetailActivity.this, "hello", Toast.LENGTH_SHORT).show();
                                reference7.child("Orders1").child(user.getUid()).setValue(or);
                                sp.getRef().removeValue();
                            }
//                            reference1.setValue()
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case 5:
                DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
                Review rev4 = new Review(workerId,"5");
                ref5.child("Reviews").child(workerId).setValue(rev4);
                review.setVisibility(View.GONE);
                onlyOnce.setText("You rated this customer by : 5 star");
                DatabaseReference reference8 = FirebaseDatabase.getInstance().getReference("Orders");
                DatabaseReference reference9 = FirebaseDatabase.getInstance().getReference();
                reference8.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot sp : snapshot.getChildren()){
                            Orders or = sp.getValue(Orders.class);
                            if(or.getReceiver().equals(workerId)){
                                Toast.makeText(WorkerRecycleViewDetailActivity.this, "hello", Toast.LENGTH_SHORT).show();
                                reference9.child("Orders1").child(user.getUid()).setValue(or);
                                sp.getRef().removeValue();
                            }
//                            reference1.setValue()
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

}