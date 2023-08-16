package com.example.wages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wages.CustomerFragments.CustomerHome;
import com.example.wages.SignUp.SignupActivity;
import com.example.wages.WorkerFragments.WorkerHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextView SignUp;
    Button LogIn;
    TextInputEditText Email, Password;
    TextInputLayout EmailL, PasswordL;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-------------------Remove menu bar code----------------
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_login);

//        Find the Id of All View
        findById();


        LogIn.setOnClickListener(view -> {

            CommonFunction commonFunction = new CommonFunction();

            if(commonFunction.isConnected(getApplicationContext())){

                String email = Objects.requireNonNull(Email.getText()).toString();
                String password = Objects.requireNonNull(Password.getText()).toString();

                Boolean flag = validateData(email, password);

                if(flag){

                    progressBar.setVisibility(view.VISIBLE);

                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                               FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                               String UID = firebaseUser.getUid();

                                DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference("Customers");

                                customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.hasChild(UID)) {

                                            progressBar.setVisibility(view.GONE);

                                            Intent intent = new Intent(LoginActivity.this, CustomerHome.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        }
                                        else{

                                            progressBar.setVisibility(view.GONE);

                                            Intent intent = new Intent(LoginActivity.this, WorkerHome.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else{

                                try {
                                    throw task.getException();
                                }

                                catch (FirebaseAuthInvalidUserException e){

                                    progressBar.setVisibility(view.GONE);

                                    noError();
                                    EmailL.setError("Email does not exist.");
                                    Email.requestFocus();

                                }

                                catch (FirebaseAuthInvalidCredentialsException e){

                                    progressBar.setVisibility(view.GONE);

                                    noError();
                                    PasswordL.setError("Password is wrong.");
                                    Password.requestFocus();

                                }
                                catch (Exception e){

                                    progressBar.setVisibility(view.GONE);

                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                }

            }
            else{

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("NO INTERNET CONNECTION");
                builder.setMessage("Internet connection is not available pleas check your internet connection ")
                        .setPositiveButton("Connect", (dialogInterface, i) -> startActivityForResult(new Intent(
                                Settings.ACTION_WIFI_SETTINGS), 0))
                        .setNegativeButton("No", (dialog, which) -> {
                            System.exit(0);
                            dialog.cancel();
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });

        SignUp.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

    }


    private void findById(){

        progressBar = findViewById(R.id.progress_bar);
        SignUp = findViewById(R.id.signup_text_view);
        LogIn = findViewById(R.id.login_button);
        Email = findViewById(R.id.login_email_edit_text);
        Password = findViewById(R.id.login_password_edit_text);
        EmailL = findViewById(R.id.login_email_text);
        PasswordL = findViewById(R.id.login_password_text);

    }

    private Boolean validateData(String email, String password) {

        //validation patterns
        Pattern lowerCase = Pattern.compile("(.*[a-z].*)");
        Pattern upperCase = Pattern.compile("(.*[A-Z].*)");
        Pattern numberCase = Pattern.compile("(.*[0-9].*)" );
        Pattern symbolCheck = Pattern.compile("^(?=.*[_.()$&@#]).*$");
        Pattern noWhiteSpace = Pattern.compile("(.*[' '].*)");

        if(TextUtils.isEmpty(email)){
            noError();
            EmailL.setError("Please Enter the Email");
            Email.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            noError();
            EmailL.setError("Enter valid Email");
            Email.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            noError();
            PasswordL.setError("Enter the Password");
            Password.requestFocus();
            return false;
        }
        else if (password.length() < 8) {
            noError();
            PasswordL.setError("Password contains at least 8 characters");
            Password.requestFocus();
            return false;
        }
        else if (noWhiteSpace.matcher(password).matches()) {
            noError();
            PasswordL.setError("White pace not allowed");
            Password.requestFocus();
            return false;
        }
        else if (!lowerCase.matcher(password).matches()) {
            noError();
            PasswordL.setError("Password contains at least 1 lower case characters");
            Password.requestFocus();
            return false;
        }
        else if (!upperCase.matcher(password).matches()) {
            noError();
            PasswordL.setError("Password contains at least 1 upper case characters");
            Password.requestFocus();
            return false;
        }
        else if (!numberCase.matcher(password).matches()) {
            noError();
            PasswordL.setError("Password contains at least 1 numeric characters");
            Password.requestFocus();
            return false;
        }
        else if (!symbolCheck.matcher(password).matches()) {
            noError();
            PasswordL.setError("Password contains at least 1 Special Symbol");
            Password.requestFocus();
            return false;
        }
        else {
            noError();
            return true;
        }
    }

    private void noError(){

        EmailL.setError(null);
        PasswordL.setError(null);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            CommonFunction commonFunction = new CommonFunction();
            if (commonFunction.isConnected(getApplicationContext())) {
                System.exit(0);
            } else {
                System.exit(0);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("CLOSING APP")
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


}

