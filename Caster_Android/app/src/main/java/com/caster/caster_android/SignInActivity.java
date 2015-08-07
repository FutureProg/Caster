package com.caster.caster_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.caster.caster_android.utils.Bin;

import java.util.concurrent.ExecutionException;


public class SignInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signIn(View view){
        String email = ((TextView)findViewById(R.id.email_field)).getText().toString();
        String pass = ((TextView)findViewById(R.id.password_field)).getText().toString();
        try {
            if (Bin.signIn(email,pass) == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Incorrect Email/Password").setTitle("Message");
                builder.create().show();
                return;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }
}