package com.example.wages.SignUp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wages.CommonFunction;
import com.example.wages.Database;
import com.example.wages.LoginActivity;
import com.example.wages.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class WorkerSignupFragment extends Fragment {

    private final String[] professionList = new String[] {"Electrician", "Plumber", "carpenter"};

    AutoCompleteTextView ProfessionW;
    TextView LoginTextViewW;
    TextInputEditText FullNameW, UserNameW, PhoneNoW, EmailW, CityW, PasswordW, ConfirmPasswordW;
    TextInputLayout FullNameLW, UserNameLW, PhoneNoLW, EmailLW, CityLW, ProfessionLW, PasswordLW,
                    ConfirmPasswordLW;
    ImageView ProfileImageW;
    Button SignUpW;
    Boolean ImageChecker = false;
    Uri resultUri;
    ProgressBar progressBar;


    public WorkerSignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_worker_signup, container, false);




        //finding the id of all view
        findById(view);




        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {

                        Location location = task.getResult();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Geocoder geocoder =  new Geocoder(getContext(), Locale.getDefault());
                        ArrayList<Address> addresses;
                        try {

                            addresses = (ArrayList<Address>) geocoder.getFromLocation(latitude, longitude,1);
                            String address = addresses.get(0).getAddressLine(0);

                            SharedPreferences sharedpreferences = requireContext().getSharedPreferences("currentLocationW", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("latitude", Double.toString(latitude));
                            editor.putString("longitude", Double.toString(longitude));
                            editor.putString("address", address);
                            editor.apply();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });

        }

        SharedPreferences sharedpreferences = requireContext().getSharedPreferences("currentLocationW", Context.MODE_PRIVATE);
        Double latitude = Double.valueOf(sharedpreferences.getString("latitude", "Latitude not found"));
        Double longitude = Double.valueOf(sharedpreferences.getString("longitude", "Longitude not found"));
        String address = sharedpreferences.getString("address", "Address not found");

        CityW.setText(address);
        FullNameW.setEnabled(true);
        UserNameW.setEnabled(true);
        EmailW.setEnabled(true);
        PhoneNoW.setEnabled(true);
        PasswordW.setEnabled(true);
        ConfirmPasswordLW.setEnabled(true);
        SignUpW.setEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.profession_spinner_model, professionList);
        ProfessionW.setAdapter(adapter);





        //if user click on image
        ProfileImageW.setOnClickListener(view1 -> ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    startForMediaPickerResult.launch(intent);
                    return null;
                }));





        //if user click on the signup button
        SignUpW.setOnClickListener(view12 -> {

            CommonFunction commonFunction = new CommonFunction();

            if(commonFunction.isConnected(requireContext())){


                //Get the information from type input edit text
                String fullNameW = Objects.requireNonNull(FullNameW.getText()).toString();
                String userNameW = Objects.requireNonNull(UserNameW.getText()).toString();
                String phoneNoW = "+92" + Objects.requireNonNull(PhoneNoW.getText());
                String emailW = Objects.requireNonNull(EmailW.getText()).toString();
                String cityW = Objects.requireNonNull(CityW.getText()).toString();
                String professionW = ProfessionW.getText().toString();
                String passwordW = Objects.requireNonNull(PasswordW.getText()).toString();
                String confirmPasswordW = Objects.requireNonNull(ConfirmPasswordW.getText()).toString();


                //Call the validate function to validate the data
                if(ImageChecker){

                    Boolean flag = validateData(fullNameW, userNameW, phoneNoW, emailW, cityW, professionW, passwordW, confirmPasswordW);


                    if(flag){

                        authentication(fullNameW, userNameW, phoneNoW, emailW, cityW, professionW, passwordW, latitude, longitude, view);

                    }
                }
                else{

                    Toast.makeText(getContext(), "Please Select the Image", Toast.LENGTH_SHORT).show();

                }
            }
            else{

                // IF INTERNET CONNECTION IS NOT AVAILABLE
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("NO INTERNET CONNECTION");
                builder.setMessage("Internet connection is not available pleas check your internet connection ")

                        .setPositiveButton("Connect", (dialogInterface, i) -> startActivityForResult(new Intent(
                                Settings.ACTION_WIFI_SETTINGS), 0))

                        .setNegativeButton("No", (dialog, which) -> System.exit(0));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }


        });


        // if user click on the login text
        LoginTextViewW.setOnClickListener(view13 -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        return view;
    }


    //FIND VIEW BY ID
    private void findById(View view){
        ProfessionW = view.findViewById(R.id.signup_profession_edit_text_w);
        FullNameW = view.findViewById(R.id.signup_full_name_edit_text_w);
        UserNameW = view.findViewById(R.id.signup_user_name_edit_text_w);
        PhoneNoW = view.findViewById(R.id.signup_phone_no_edit_text_w);
        EmailW = view.findViewById(R.id.signup_email_edit_text_w);
        CityW = view.findViewById(R.id.signup_city_edit_text_w);
        PasswordW = view.findViewById(R.id.signup_password_edit_text_w);
        ConfirmPasswordW = view.findViewById(R.id.signup_confirm_password_edit_text_w);
        ProfileImageW = view.findViewById(R.id.signup_profile_image_w);
        SignUpW = view.findViewById(R.id.signup_button_w);
        LoginTextViewW = view.findViewById(R.id.login_text_view_w);
        progressBar = view.findViewById(R.id.progress_bar);
        FullNameLW = view.findViewById(R.id.signup_full_name_text_w);
        UserNameLW = view.findViewById(R.id.signup_user_name_text_w);
        PhoneNoLW = view.findViewById(R.id.signup_phone_no_text_w);
        EmailLW = view.findViewById(R.id.signup_email_text_w);
        CityLW = view.findViewById(R.id.signup_city_text_w);
        ProfessionLW = view.findViewById(R.id.signup_profession_text_w);
        PasswordLW = view.findViewById(R.id.signup_password_text_w);
        ConfirmPasswordLW = view.findViewById(R.id.signup_confirm_password_text_w);
    }





    //VALIDATE THE DATA
    private Boolean validateData(String fullNameW, String userNameW, String phoneNoW, String emailW,
                                 String cityW, String professionW, String passwordW, String confirmPasswordW) {

        //validation patterns
        Pattern lowerCase = Pattern.compile("(.*[a-z].*)");
        Pattern upperCase = Pattern.compile("(.*[A-Z].*)");
        Pattern numberCase = Pattern.compile("(.*[0-9].*)" );
        Pattern symbolCheck = Pattern.compile("^(?=.*[_.()$&@#]).*$");
        Pattern noWhiteSpace = Pattern.compile("(.*[' '].*)");

        if(TextUtils.isEmpty(fullNameW)){
            noError();
            FullNameLW.setError("Enter the Full Name");
            FullNameW.requestFocus();
            return false;
        }
        else if(fullNameW.length() < 3) {
            noError();
            FullNameLW.setError("Full Name contain at Least 3 characters");
            FullNameW.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(userNameW)){
            noError();
            UserNameLW.setError("Enter the User Name");
            UserNameW.requestFocus();
            return false;
        }
        else if(userNameW.length() < 8) {
            noError();
            UserNameLW.setError("User Name contain at Least 8 characters");
            UserNameW.requestFocus();
            return false;
        }
        else if(noWhiteSpace.matcher(userNameW).matches()){
            noError();
            UserNameLW.setError("White Space not allowed");
            UserNameW.requestFocus();
            return false;
        }
        else if(!numberCase.matcher(userNameW).matches()){
            noError();
            UserNameLW.setError("User Name contain at Least 1 Numeric character");
            UserNameW.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(emailW)){
            noError();
            EmailLW.setError("Please Enter the Email");
            EmailW.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailW).matches()){
            noError();
            EmailLW.setError("Enter valid Email");
            EmailW.requestFocus();
            return false;
        }
        else if(phoneNoW.length() != 13){
            noError();
            PhoneNoLW.setError("Please Enter 10 Digits Phone No");
            PhoneNoW.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(cityW)){
            noError();
            CityLW.setError("Enter the City Name");
            CityW.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(professionW)){
            noError();
            ProfessionLW.setError("Select the Profession");
            ProfessionLW.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(passwordW)){
            noError();
            PasswordLW.setError("Enter the Password");
            PasswordW.requestFocus();
            return false;
        }
        else if(passwordW.length() < 8){
            noError();
            PasswordLW.setError("Password contains at least 8 characters");
            PasswordW.requestFocus();
            return false;
        }
        else if(noWhiteSpace.matcher(passwordW).matches()){
            noError();
            PasswordLW.setError("White space not allowed");
            PasswordW.requestFocus();
            return false;
        }
        else if(!lowerCase.matcher(passwordW).matches()){
            noError();
            PasswordLW.setError("Password must contain lower case letter");
            PasswordW.requestFocus();
            return false;
        }
        else if(!upperCase.matcher(passwordW).matches()){
            noError();
            PasswordLW.setError("Password must contain upper case letter");
            PasswordW.requestFocus();
            return false;
        }
        else if(!numberCase.matcher(passwordW).matches()){
            noError();
            PasswordLW.setError("Password must contain digits");
            PasswordW.requestFocus();
            return false;
        }
        else if(!symbolCheck.matcher(passwordW).matches()){
            noError();
            PasswordLW.setError("password must contain [@#$%&]");
            PasswordW.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(confirmPasswordW)) {
            noError();
            ConfirmPasswordLW.setError("Enter the Confirm Password");
            ConfirmPasswordW.requestFocus();
            return false;
        }
        else if(!confirmPasswordW.equals(passwordW)){
            noError();
            ConfirmPasswordLW.setError("Password must be match");
            ConfirmPasswordW.requestFocus();
            return false;
        }
        else {
            noError();
            return true;
        }
    }





    // REMOVE ERROR FROM TEXT FIELDS
    private void noError(){

        FullNameLW.setError(null);
        UserNameLW.setError(null);
        EmailLW.setError(null);
        PhoneNoLW.setError(null);
        CityLW.setError(null);
        ProfessionLW.setError(null);
        PasswordLW.setError(null);
        ConfirmPasswordLW.setError(null);

    }





//    FIREBASE AUTHENTICATION FUNCTION
    private void authentication(String fullNameW, String userNameW, String phoneNoW, String emailW,
                                String cityW, String professionW, String passwordW, Double latitude, Double longitude, View view) {

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailW, passwordW).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(emailW);
                storageReference.putFile(resultUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Workers");

                    assert firebaseUser != null;
                    Database workerDatabase = new Database(fullNameW, userNameW,
                            emailW, phoneNoW, cityW, professionW,firebaseUser.getUid(), latitude, longitude, uri.toString());

                    databaseReference.child(firebaseUser.getUid()).setValue(workerDatabase)
                            .addOnCompleteListener(task1 -> {

                                if(task1.isSuccessful()){

                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getContext(), "Signup Successfully", Toast.LENGTH_SHORT).show();

                                }
                                else{

                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getContext(), "Signup Unsuccessfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                    getActivity().finish();

                                }

                            });

                }));

            }
            else{

                try {

                    throw Objects.requireNonNull(task.getException());

                }

                catch (FirebaseAuthUserCollisionException e){

                    progressBar.setVisibility(View.GONE);

                    EmailLW.setError(null);
                    EmailLW.setError("This Email is already Exist");
                    EmailW.requestFocus();

                }
                catch (Exception e){

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }





    // RESULT ACTIVITY FOR IMAGE PICKER
    private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null && result.getResultCode() == Activity.RESULT_OK) {
                    resultUri = data.getData();
                    ProfileImageW.setImageURI(resultUri);
                    ImageChecker = true;
                }
                else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });





    // FOR RESULT OF THE INTENT ACTIVITY
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            Intent intent=new Intent();
            intent.setClass(getContext(), this.getClass());
            this.startActivity(intent);
            this.requireActivity().finish();

        }

    }
}