package com.chat.saumya.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.chat.saumya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Transaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashscreenActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen2);

        Toast.makeText(this,"Splash_screen",Toast.LENGTH_LONG);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(SplashscreenActivity.this,HomeActivity.class));
            finish();
        }else{
            startActivity(new Intent(SplashscreenActivity.this,LoginActivity.class));
            finish();
        }




    //    Toast.makeText(this,"Splash_screen",Toast.LENGTH_LONG);
      //  Intent i=new Intent(this,LoginActivity.class);
       // startActivity(i);
        //finish();

    }
}
