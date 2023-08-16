package com.example.wages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class ChangeEmailActivity extends AppCompatActivity {


    TextInputEditText CurrentEmailText, CurrentPasswordText, NewEmailText;
    TextInputLayout CurrentEmailLayout, CurrentPasswordLayout, NewEmailLayout;
    Button Authenticate, UpdateEmail;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        findViewById();


        Authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String email = CurrentEmailText.getText().toString();
                String password = CurrentPasswordText.getText().toString();

                boolean flag = validateTheData(email, password);

                if(flag){

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(ChangeEmailActivity.this, "Authenticate Successfully", Toast.LENGTH_SHORT).show();

                            CurrentEmailText.setEnabled(false);
                            CurrentPasswordText.setEnabled(false);
                            Authenticate.setEnabled(false);

                            NewEmailText.setEnabled(true);
                            UpdateEmail.setEnabled(true);

                            progressBar.setVisibility(View.GONE);

                        }
                    });

                }
            }

        });

        UpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = NewEmailText.getText().toString();

                boolean flag = validateEmail(email);

                if (flag) {

                    updateEmail(email);
                }
            }
        });

    }


    private void findViewById() {

        CurrentEmailText = findViewById(R.id.current_email_edit_text);
        CurrentPasswordText = findViewById(R.id.current_password_edit_text);
        NewEmailText = findViewById(R.id.new_email_edit_text);

        CurrentEmailLayout = findViewById(R.id.current_email_text);
        CurrentPasswordLayout = findViewById(R.id.current_password_text);
        NewEmailLayout = findViewById(R.id.new_email_text);

        Authenticate  = findViewById(R.id.authenticate_button);
        UpdateEmail = findViewById(R.id.update_email_button);

        progressBar = findViewById(R.id.progress_bar);

    }

    private Boolean validateTheData(String email, String password) {

        //validation patterns
        Pattern lowerCase = Pattern.compile("(.*[a-z].*)");
        Pattern upperCase = Pattern.compile("(.*[A-Z].*)");
        Pattern numberCase = Pattern.compile("(.*[0-9].*)");
        Pattern symbolCheck = Pattern.compile("^(?=.*[_.()$&@#]).*$");
        Pattern noWhiteSpace = Pattern.compile("(.*[' '].*)");

        if (TextUtils.isEmpty(email)) {
            CurrentEmailLayout.setError(null);
            CurrentEmailLayout.setError("Please Enter the Email");
            CurrentEmailLayout.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CurrentEmailLayout.setError(null);
            CurrentEmailLayout.setError("Enter valid Email");
            CurrentEmailLayout.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            CurrentEmailLayout.setError(null);
            CurrentPasswordLayout.setError("Enter the Password");
            CurrentPasswordLayout.requestFocus();
            return false;
        } else if (password.length() < 8) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 8 characters");
            CurrentPasswordLayout.requestFocus();
            return false;
        } else if (noWhiteSpace.matcher(password).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("White pace not allowed");
            CurrentPasswordLayout.requestFocus();
            return false;
        } else if (!lowerCase.matcher(password).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 lower case characters");
            CurrentPasswordLayout.requestFocus();
            return false;
        } else if (!upperCase.matcher(password).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 upper case characters");
            CurrentPasswordLayout.requestFocus();
            return false;
        } else if (!numberCase.matcher(password).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 numeric characters");
            CurrentPasswordLayout.requestFocus();
            return false;
        } else if (!symbolCheck.matcher(password).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 Special Symbol");
            CurrentPasswordLayout.requestFocus();
            return false;
        }
        else{
            CurrentPasswordLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {

        if (TextUtils.isEmpty(email)) {
            NewEmailLayout.setError(null);
            NewEmailLayout.setError("Please Enter the Email");
            NewEmailLayout.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            NewEmailLayout.setError(null);
            NewEmailLayout.setError("Enter valid Email");
            NewEmailLayout.requestFocus();
            return false;
        }
        else{
            NewEmailLayout.setError(null);
            return true;
        }
    }

    private void updateEmail(String email) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ChangeEmailActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}