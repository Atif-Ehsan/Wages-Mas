package com.example.wages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wages.CustomerFragments.CustomerHome;
import com.example.wages.WorkerFragments.WorkerHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity {

    private static final int splashScreenDuration = 5000;
    TextView splashScreenTitleText;
    ImageView splashScreenLogoImage;
    Animation splashScreenLogoAnimation, splashScreenTitleAnimation;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-------------------Remove menu bar code----------------
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.main_spalsh_screen);

        splashScreenLogoImage = findViewById(R.id.splashScreenLogoImageView);
        splashScreenTitleText = findViewById(R.id.splashScreenTitleTextView);

        splashScreenLogoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_logo_animation);
        splashScreenLogoImage.setAnimation(splashScreenLogoAnimation);

        splashScreenTitleAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_title_animation);
        splashScreenTitleText.setAnimation(splashScreenTitleAnimation);

        new Handler().postDelayed(() -> {
            CommonFunction commonFunction = new CommonFunction();

            if(commonFunction.isConnected(getApplicationContext())){

                firebaseAuth = FirebaseAuth.getInstance();
                if(firebaseAuth.getCurrentUser() != null){

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String UID = firebaseUser.getUid();

                    DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference("Customers");

                    customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(UID)) {

                                startActivity(new Intent(MainScreenActivity.this, CustomerHome.class));

                            }
                            else{

                                startActivity(new Intent(MainScreenActivity.this, WorkerHome.class));
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    startActivity(new Intent(MainScreenActivity.this, LoginActivity.class));
                    finish();
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

        },splashScreenDuration);
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
}