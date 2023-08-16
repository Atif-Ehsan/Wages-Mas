package com.example.wages.WorkerFragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wages.ChangeEmailActivity;
import com.example.wages.ChangePasswordActivity;
import com.example.wages.LoginActivity;
import com.example.wages.Notification.Token;
import com.example.wages.R;
import com.example.wages.UpdateCustomerProfileActivity;
import com.example.wages.UpdateWorkerProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class WorkerHome extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home);
        getSupportActionBar().setTitle("DAILY WAGES");

        bottomNavigationView = findViewById(R.id.bottom_navigation_worker_home);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame_layout_worker, new WorkerChatFragment());
        ft.commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.chat) {
                FragmentManager fmc = getSupportFragmentManager();
                FragmentTransaction ftc = fmc.beginTransaction();
                ftc.replace(R.id.frame_layout_worker, new WorkerChatFragment());
                ftc.commit();
                return true;
            }
            if (item.getItemId() == R.id.request){
                FragmentManager fmw = getSupportFragmentManager();
                FragmentTransaction ftw = fmw.beginTransaction();
                ftw.replace(R.id.frame_layout_worker, new WorkerRequestFragment());
                ftw.commit();
                return true;
            }
            if (item.getItemId() == R.id.profile){
                FragmentManager fmw = getSupportFragmentManager();
                FragmentTransaction ftw = fmw.beginTransaction();
                ftw.replace(R.id.frame_layout_worker, new WorkerProfileFragment());
                ftw.commit();
                return true;
            }
            return false;
        });
//        updateToken(FirebaseMessaging.getInstance().getToken().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.commom_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }
        else if(id == R.id.update_profile){
            startActivity(new Intent(this, UpdateWorkerProfileActivity.class));
        }
        else if(id == R.id.change_email){
            startActivity(new Intent(this, ChangeEmailActivity.class));
        }
        else if(id == R.id.change_password){
            startActivity(new Intent(this, ChangePasswordActivity.class));
        }
        else if(id == R.id.logout){
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Intent intent = new Intent(WorkerHome.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Token");
        Token token1 = new Token(token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child(user.getUid()).setValue(token1);
    }
}