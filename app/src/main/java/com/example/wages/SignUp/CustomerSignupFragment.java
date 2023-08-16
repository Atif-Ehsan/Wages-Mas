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

public class CustomerSignupFragment extends Fragment {

    TextView LoginTextViewC;
    TextInputEditText FullNameC, UserNameC, EmailC, PhoneNoC, CityC, PasswordC, ConfirmPasswordC;
    TextInputLayout FullNameLC, UserNameLC, EmailLC, PhoneNoLC, CityLC, PasswordLC, ConfirmPasswordLC;
    ImageView ProfileImageC;
    Button SignUpC;
    Boolean ImageChecker = false;
    Uri resultUri;
    ProgressBar progressBar;


    public CustomerSignupFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_signup, container, false);

        //finding the id of all view
        findTheId(view);

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

                                SharedPreferences sharedpreferences = requireContext().getSharedPreferences("currentLocation", Context.MODE_PRIVATE);
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


        SharedPreferences sharedpreferences = requireContext().getSharedPreferences("currentLocation", Context.MODE_PRIVATE);
        Double latitude = Double.valueOf(sharedpreferences.getString("latitude", "Latitude not found"));
        Double longitude = Double.valueOf(sharedpreferences.getString("longitude", "Longitude not found"));
        String address = sharedpreferences.getString("address", "Address not found");

        FullNameC.setEnabled(true);
        UserNameC.setEnabled(true);
        EmailC.setEnabled(true);
        PhoneNoC.setEnabled(true);
        CityC.setText(address);
        PasswordC.setEnabled(true);
        ConfirmPasswordC.setEnabled(true);
        SignUpC.setEnabled(true);



        //if user click on image
        ProfileImageC.setOnClickListener(view1 -> ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    startForMediaPickerResult.launch(intent);
                    return null;
                }));


        //if user click on the signup button
        SignUpC.setOnClickListener(view12 -> {

            CommonFunction commonFunction = new CommonFunction();

            if (commonFunction.isConnected(requireContext())) {


//                Get the information from type input edit text
                String fullNameC = Objects.requireNonNull(FullNameC.getText()).toString();
                String userNameC = Objects.requireNonNull(UserNameC.getText()).toString();
                String emailC = Objects.requireNonNull(EmailC.getText()).toString();
                String phoneNoC = "+92" + Objects.requireNonNull(PhoneNoC.getText());
                String cityC = Objects.requireNonNull(CityC.getText()).toString();
                String passwordC = Objects.requireNonNull(PasswordC.getText()).toString();
                String confirmPasswordC = Objects.requireNonNull(ConfirmPasswordC.getText()).toString();


//                Call the validate function to validate the data
                if (ImageChecker) {

                    Boolean flag = validateData(fullNameC, userNameC, emailC, phoneNoC, cityC, passwordC, confirmPasswordC);

                    if (flag) {

                        authentication(fullNameC, userNameC, emailC, phoneNoC, cityC, passwordC, latitude, longitude);
                    }
                } else {

                    Toast.makeText(getContext(), "PLease Select The Image", Toast.LENGTH_SHORT).show();

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
        LoginTextViewC.setOnClickListener(view13 -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        return view;
    }





    //FIND VIEW BY ID
    private void findTheId(View view) {
        FullNameC = view.findViewById(R.id.signup_full_name_edit_text_c);
        UserNameC = view.findViewById(R.id.signup_user_name_edit_text_c);
        EmailC = view.findViewById(R.id.signup_email_edit_text_c);
        PhoneNoC = view.findViewById(R.id.signup_phone_no_edit_text_c);
        CityC = view.findViewById(R.id.signup_city_edit_text_c);
        PasswordC = view.findViewById(R.id.signup_password_edit_text_c);
        ConfirmPasswordC = view.findViewById(R.id.signup_confirm_password_edit_text_c);
        ProfileImageC = view.findViewById(R.id.signup_profile_image_c);
        SignUpC = view.findViewById(R.id.signup_button_c);
        LoginTextViewC = view.findViewById(R.id.login_text_view_c);
        progressBar = view.findViewById(R.id.progress_bar);
        FullNameLC = view.findViewById(R.id.signup_full_name_text_c);
        UserNameLC = view.findViewById(R.id.signup_user_name_text_c);
        EmailLC = view.findViewById(R.id.signup_email_text_c);
        PhoneNoLC = view.findViewById(R.id.signup_phone_no_text_c);
        CityLC = view.findViewById(R.id.signup_city_text_c);
        PasswordLC = view.findViewById(R.id.signup_password_text_c);
        ConfirmPasswordLC = view.findViewById(R.id.signup_confirm_password_text_c);
    }





    // VALIDATE THE DATA
    private Boolean validateData(String fullNameC, String userNameC, String emailC, String phoneNoC,
                                 String cityC, String passwordC, String confirmPasswordC) {

        //validation patterns
        Pattern lowerCase = Pattern.compile("(.*[a-z].*)");
        Pattern upperCase = Pattern.compile("(.*[A-Z].*)");
        Pattern numberCase = Pattern.compile("(.*[0-9].*)");
        Pattern symbolCheck = Pattern.compile("^(?=.*[_.()$&@#]).*$");
        Pattern noWhiteSpace = Pattern.compile("(.*[' '].*)");


        if (TextUtils.isEmpty(fullNameC)) {
            noError();
            FullNameLC.setError("Enter the Full Name");
            FullNameC.requestFocus();
            return false;
        } else if (fullNameC.length() < 3) {
            noError();
            FullNameLC.setError("Full Name contain at Least 3 characters");
            FullNameC.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(userNameC)) {
            noError();
            UserNameLC.setError("Enter the User Name");
            UserNameC.requestFocus();
            return false;
        } else if (userNameC.length() < 8) {
            noError();
            UserNameLC.setError("User Name contain at Least 8 characters");
            UserNameC.requestFocus();
            return false;
        } else if (noWhiteSpace.matcher(userNameC).matches()) {
            noError();
            UserNameLC.setError("White space not allowed");
            UserNameC.requestFocus();
            return false;
        } else if (!numberCase.matcher(userNameC).matches()) {
            noError();
            UserNameLC.setError("User Name contain at Least 1 Numeric character");
            UserNameC.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(emailC)) {
            noError();
            EmailLC.setError("Please Enter the Email");
            EmailC.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailC).matches()) {
            noError();
            EmailLC.setError("Enter valid Email");
            EmailC.requestFocus();
            return false;
        } else if (phoneNoC.length() != 13) {
            noError();
            PhoneNoLC.setError("Please Enter 10 Digits Phone No");
            PhoneNoC.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(cityC)) {
            noError();
            CityLC.setError("Enter the City Name");
            CityC.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(passwordC)) {
            noError();
            PasswordLC.setError("Enter the Password");
            PasswordC.requestFocus();
            return false;
        } else if (passwordC.length() < 8) {
            noError();
            PasswordLC.setError("Password contains at least 8 characters");
            PasswordC.requestFocus();
            return false;
        } else if (noWhiteSpace.matcher(passwordC).matches()) {
            noError();
            PasswordLC.setError("White pace not allowed");
            PasswordC.requestFocus();
            return false;
        } else if (!lowerCase.matcher(passwordC).matches()) {
            noError();
            PasswordLC.setError("Password contains at least 1 lower case characters");
            PasswordC.requestFocus();
            return false;
        } else if (!upperCase.matcher(passwordC).matches()) {
            noError();
            PasswordLC.setError("Password contains at least 1 upper case characters");
            PasswordC.requestFocus();
            return false;
        } else if (!numberCase.matcher(passwordC).matches()) {
            noError();
            PasswordLC.setError("Password contains at least 1 numeric characters");
            PasswordC.requestFocus();
            return false;
        } else if (!symbolCheck.matcher(passwordC).matches()) {
            noError();
            PasswordLC.setError("Password contains at least 1 Special Symbol");
            PasswordC.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirmPasswordC)) {
            noError();
            ConfirmPasswordLC.setError("Enter the Confirm Password");
            ConfirmPasswordC.requestFocus();
            return false;
        } else if (!confirmPasswordC.equals(passwordC)) {
            noError();
            ConfirmPasswordLC.setError("Password must be match");
            ConfirmPasswordC.requestFocus();
            return false;
        } else {
            noError();
            return true;
        }
    }





    // REMOVE THE ERROR FROM TEXT FIELDS
    private void noError() {

        FullNameLC.setError(null);
        UserNameLC.setError(null);
        EmailLC.setError(null);
        PhoneNoLC.setError(null);
        CityLC.setError(null);
        PasswordLC.setError(null);
        ConfirmPasswordLC.setError(null);

    }





    // ACTIVITY RESULT FOR IMAGE PICKER
    private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null && result.getResultCode() == Activity.RESULT_OK) {
                    resultUri = data.getData();
                    ProfileImageC.setImageURI(resultUri);
                    ImageChecker = true;
                } else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });





    // AUTHENTICATION AND SAVE THE DATA IN FIREBASE
    private void authentication(String fullNameC, String userNameC, String emailC, String phoneNoC,
                                String cityC, String passwordC, Double latitude, Double longitude) {

        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailC, passwordC).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference().child(emailC);
                storageReference.putFile(resultUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Customers");

                    assert firebaseUser != null;
                    Database customerDatabase = new Database(fullNameC, userNameC, phoneNoC, cityC, firebaseUser.getUid(), latitude, longitude, uri.toString());

                    databaseReference.child(firebaseUser.getUid()).setValue(customerDatabase).addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Signup Successfully", Toast.LENGTH_SHORT).show();

                        } else {

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Signup Unsuccessfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();

                        }

                    });

                }));

            } else {

                try {

                    throw Objects.requireNonNull(task.getException());

                } catch (FirebaseAuthUserCollisionException e) {

                    progressBar.setVisibility(View.GONE);

                    EmailLC.setError(null);
                    EmailLC.setError("This Email is already Exist");
                    EmailC.requestFocus();

                } catch (Exception e) {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }





    // FOR RESULT OF THE INTENT ACTIVITY
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            Intent intent=new Intent();
            intent.setClass(getContext(), this.getClass());
            this.startActivity(intent);
            this.getActivity().finish();

        }

    }

}


