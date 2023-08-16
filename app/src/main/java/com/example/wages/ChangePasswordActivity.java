package com.example.wages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputEditText CurrentPasswordText, NewPasswordText, ConfirmPasswordText;
    TextInputLayout CurrentPasswordLayout, NewPasswordLayout, ConfirmPasswordLayout;
    Button AuthenticateButton, ChangePasswordButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        //FIND THE VIEW BY ID
        findTheViewById();





        // WHEN USER ON THE AUTHENTICATE BUTTON
        AuthenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentPassword = CurrentPasswordText.getText().toString();

                boolean flag = validatePassword(currentPassword);

                if(flag){

                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(ChangePasswordActivity.this, "Authenticate Successfully", Toast.LENGTH_SHORT).show();

                                CurrentPasswordText.setEnabled(false);
                                AuthenticateButton.setEnabled(false);

                                NewPasswordText.setEnabled(true);
                                ConfirmPasswordText.setEnabled(true);
                                ChangePasswordButton.setEnabled(true);

                                progressBar.setVisibility(View.GONE);
                            }
                            else{
                                try {
                                    throw (task.getException());
                                }
                                catch (Exception e) {

                                    CurrentPasswordLayout.setError(null);
                                    CurrentPasswordLayout.setError(e.getMessage());
                                    progressBar.setVisibility(View.GONE);

                                }
                            }

                        }
                    });

                }
            }
        });




        ChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = NewPasswordText.getText().toString();
                String confirmPassword = ConfirmPasswordText.getText().toString();

                if(validatePassword(newPassword)){

                    boolean flag = validateConfirmPassword(newPassword, confirmPassword);

                    if(flag){

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });
    }




    // FIND THE VIEW BY ID FUNCTION
    private void findTheViewById() {

        CurrentPasswordText = findViewById(R.id.current_password_edit_text);
        NewPasswordText = findViewById(R.id.new_password_edit_text);
        ConfirmPasswordText = findViewById(R.id.new_confirm_password_edit_text);

        CurrentPasswordLayout = findViewById(R.id.current_password_text);
        NewPasswordLayout = findViewById(R.id.new_password_text);
        ConfirmPasswordLayout = findViewById(R.id.new_confirm_password_text);

        AuthenticateButton = findViewById(R.id.authenticate_button);
        ChangePasswordButton = findViewById(R.id.update_password_button);

        progressBar = findViewById(R.id.progress_bar);

    }





    // VALIDATE THE PASSWORD
    private boolean validatePassword(String currentPassword) {


        //validation patterns
        Pattern lowerCase = Pattern.compile("(.*[a-z].*)");
        Pattern upperCase = Pattern.compile("(.*[A-Z].*)");
        Pattern numberCase = Pattern.compile("(.*[0-9].*)");
        Pattern symbolCheck = Pattern.compile("^(?=.*[_.()$&@#]).*$");
        Pattern noWhiteSpace = Pattern.compile("(.*[' '].*)");


        if (TextUtils.isEmpty(currentPassword)) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Enter the Password");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else if (currentPassword.length() < 8) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 8 characters");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else if (noWhiteSpace.matcher(currentPassword).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("White pace not allowed");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else if (!lowerCase.matcher(currentPassword).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 lower case characters");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else if (!upperCase.matcher(currentPassword).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 upper case characters");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else if (!numberCase.matcher(currentPassword).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 numeric characters");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else if (!symbolCheck.matcher(currentPassword).matches()) {
            CurrentPasswordLayout.setError(null);
            CurrentPasswordLayout.setError("Password contains at least 1 Special Symbol");
            CurrentPasswordText.requestFocus();
            return false;
        }
        else{
            CurrentPasswordLayout.setError(null);
            return true;
        }
    }





    private Boolean validateConfirmPassword(String newPassword, String confirmPassword) {

        if(!newPassword.matches(confirmPassword)){
            ConfirmPasswordLayout.setError(null);
            ConfirmPasswordLayout.setError("Password must be match");
            ConfirmPasswordText.requestFocus();
            return false;
        }
        else{
            ConfirmPasswordLayout.setError(null);
            return true;
        }

    }
}