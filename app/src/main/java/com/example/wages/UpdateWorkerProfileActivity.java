package com.example.wages;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class UpdateWorkerProfileActivity extends AppCompatActivity {

    TextInputEditText UpdateFullNameW, UpdateUserNameW, UpdatePhoneNoW;
    TextInputLayout UpdateFullNameLW, UpdateUserNameLW, UpdatePhoneNoLW;
    TextView UpdateFullNameButton, UpdateUserNameButton, UpdatePhoneNoButton;
    int counterFullName, counterUserName, counterPhoneNo, counterCity;
    ImageView UpdateImage;
    ProgressBar UpdateProgressBar;
    Uri resultUri;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_worker_profile);

        counterFullName = counterUserName = counterPhoneNo = counterCity = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        findViewByID();

        assert firebaseUser != null;
        showData(firebaseUser);


        // IF USER CLICK FOR UPDATE THE FULL NAME OF THE USER
        UpdateFullNameButton.setOnClickListener(view -> {

            if(counterFullName == 0){
                UpdateFullNameW.setEnabled(true);
                UpdateFullNameW.requestFocus();
                UpdateFullNameLW.setCounterEnabled(true);
                UpdateFullNameButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                counterFullName++;
            }
            else{
                UpdateFullNameW.setEnabled(false);
                UpdateFullNameLW.setCounterEnabled(false);

                String fullName = UpdateFullNameW.getText().toString();

                Boolean flag = validateFullName(fullName);

                if(flag){

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
                    databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                Database database = snapshot.getValue(Database.class);

                                if(database != null){
                                    databaseReference.child(firebaseUser.getUid()).child("fullName").setValue(fullName);
                                    UpdateFullNameW.setText(fullName);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                UpdateFullNameButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
                counterFullName = 0;
            }
        });


        UpdateUserNameButton.setOnClickListener(view -> {

            if(counterUserName == 0){
                UpdateUserNameW.setEnabled(true);
                UpdateUserNameW.requestFocus();
                UpdateUserNameLW.setCounterEnabled(true);
                UpdateUserNameButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                counterUserName++;
            }
            else{
                UpdateUserNameW.setEnabled(false);
                UpdateUserNameLW.setCounterEnabled(false);

                String userName = UpdateUserNameW.getText().toString();
                Boolean flag = validateUserName(userName);

                if(flag){

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
                    databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                Database database = snapshot.getValue(Database.class);

                                if(database != null){
                                    databaseReference.child(firebaseUser.getUid()).child("userName").setValue(userName);
                                    UpdateUserNameW.setText(userName);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                UpdateUserNameButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
                counterUserName = 0;
            }

        });

        UpdatePhoneNoButton.setOnClickListener(view -> {

            if(counterPhoneNo == 0){
                UpdatePhoneNoW.setEnabled(true);
                UpdatePhoneNoW.requestFocus();
                UpdatePhoneNoLW.setCounterEnabled(true);
                UpdatePhoneNoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                counterPhoneNo++;
            }
            else{
                UpdatePhoneNoW.setEnabled(false);
                UpdatePhoneNoLW.setCounterEnabled(false);

                String phoneNo = UpdatePhoneNoW.getText().toString();
                Boolean flag = validatePhoneNo(phoneNo);

                if(flag){

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
                    databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                Database database = snapshot.getValue(Database.class);

                                if(database != null){
                                    databaseReference.child(firebaseUser.getUid()).child("phoneNo").setValue(phoneNo);
                                    UpdatePhoneNoW.setText(phoneNo);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                UpdatePhoneNoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit, 0, 0, 0);
                counterPhoneNo = 0;
            }

        });


        // UPDATE THE IMAGE OF THE USER
        UpdateImage.setOnClickListener(view1 -> ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    startForMediaPickerResult.launch(intent);
                    return null;
                }));

    }


    // FIND THE VIEW BY ID
    private void findViewByID() {

        UpdateImage = findViewById(R.id.update_image_w);
        UpdateProgressBar = findViewById(R.id.update_progress_bar);

        UpdateFullNameW = findViewById(R.id.update_full_name_edit_text_w);
        UpdateUserNameW = findViewById(R.id.update_user_name_edit_text_w);
        UpdatePhoneNoW = findViewById(R.id.update_phone_no_edit_text_w);

        UpdateFullNameLW = findViewById(R.id.update_full_name_text_w);
        UpdateUserNameLW = findViewById(R.id.update_user_name_text_w);
        UpdatePhoneNoLW = findViewById(R.id.update_phone_no_text_w);

        UpdateFullNameButton = findViewById(R.id.update_full_name_button_w);
        UpdateUserNameButton = findViewById(R.id.update_user_name_button_w);
        UpdatePhoneNoButton = findViewById(R.id.update_phone_no_button_w);
    }

    // SHOW THE DATA IN FRONT PAGE OF UPDATE
    private void showData(FirebaseUser firebaseUser) {

        UpdateProgressBar.setVisibility(View.VISIBLE);

        String UID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
        databaseReference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Database database = snapshot.getValue(Database.class);

                    if(database != null){

                        String fullName = database.getFullName();
                        String userName = database.getUserName();
                        String phoneNo = database.getPhoneNo();
                        String url = database.getImage();

                        UpdateFullNameW.setText(fullName);
                        UpdateUserNameW.setText(userName);
                        UpdatePhoneNoW.setText(phoneNo);
                        Glide.with(UpdateWorkerProfileActivity.this).load(url).into(UpdateImage);

                        UpdateProgressBar.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // VALIDATE THE FULL NAME OF THE USER
    private Boolean validateFullName(String fullName) {

        if (TextUtils.isEmpty(fullName)) {
            UpdateFullNameLW.setError(null);
            UpdateFullNameLW.setError("Enter the Full Name");
            UpdateFullNameW.requestFocus();
            return false;
        }
        else if (fullName.length() < 3) {
            UpdateFullNameLW.setError(null);
            UpdateFullNameLW.setError("Full Name contain at Least 3 characters");
            UpdateFullNameW.requestFocus();
            return false;
        }
        else {
            UpdateFullNameLW.setError(null);
            return true;
        }
    }


    //VALIDATE THE USERNAME OF THE USER
    private Boolean validateUserName(String userName) {

        Pattern numberCase = Pattern.compile("(.*[0-9].*)" );
        Pattern noWhiteSpace = Pattern.compile("(.*[' '].*)");

        if (TextUtils.isEmpty(userName)) {
            UpdateUserNameLW.setError(null);
            UpdateUserNameLW.setError("Enter the User Name");
            UpdateUserNameW.requestFocus();
            return false;
        }
        else if (userName.length() < 8) {
            UpdateUserNameLW.setError(null);
            UpdateUserNameLW.setError("User Name contain at Least 8 characters");
            UpdateUserNameW.requestFocus();
            return false;
        }
        else if (noWhiteSpace.matcher(userName).matches()) {
            UpdateUserNameLW.setError(null);
            UpdateUserNameLW.setError("White space not allowed");
            UpdateUserNameW.requestFocus();
            return false;
        }
        else if (!numberCase.matcher(userName).matches()) {
            UpdateUserNameLW.setError(null);
            UpdateUserNameLW.setError("User Name contain at Least 1 Numeric character");
            UpdateUserNameW.requestFocus();
            return false;
        }
        else{
            UpdateUserNameLW.setError(null);
            return true;
        }
    }


    // VALIDATE THE PHONE NO OF THE USER
    private Boolean validatePhoneNo(String phoneNo){

        if (phoneNo.length() != 13) {
            UpdatePhoneNoW.setError(null);
            UpdatePhoneNoW.setError("Please Enter 10 Digits Phone No");
            UpdatePhoneNoW.requestFocus();
            return false;
        }
        else{
            UpdatePhoneNoW.setError(null);
            return true;
        }

    }



    // ACTIVITY RESULT FOR IMAGE PICKER
    private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null && result.getResultCode() == Activity.RESULT_OK) {
                    resultUri = data.getData();

                    String email = firebaseUser.getEmail();
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReference().child(email);
                    storageReference.putFile(resultUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
                        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){

                                    Database database = snapshot.getValue(Database.class);

                                    if(database != null){
                                        databaseReference.child(firebaseUser.getUid()).child("image").setValue(uri.toString());
                                        Toast.makeText(UpdateWorkerProfileActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                        UpdateImage.setImageURI(resultUri);

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }));

                } else {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });

}