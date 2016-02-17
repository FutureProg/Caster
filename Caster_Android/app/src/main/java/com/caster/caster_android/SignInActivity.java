package com.caster.caster_android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caster.caster_android.utils.Bin;

import java.util.concurrent.ExecutionException;



public class SignInActivity extends AppCompatActivity{

    public static final int SIGN_IN_REQUEST = 0x021;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //set the toolbar as the actionbar
    }

    public void onSubmit(View v) {
        String email = ((TextView)findViewById(R.id.email_field)).getText().toString();
        String pass = ((TextView)findViewById(R.id.password_field)).getText().toString();
        try {
            User appUser = Bin.signIn(email, pass);
            if (appUser == null){
                Toast.makeText(this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences preferences = getSharedPreferences("user_info",0);
            preferences.edit().putInt("user_id",appUser.getId()).commit();
            Intent re = new Intent();
            setResult(Activity.RESULT_OK,re);
            finish();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent re = new Intent();
        setResult(Activity.RESULT_CANCELED,re);
        finish();
    }
}
