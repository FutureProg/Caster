package com.caster.caster_android.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caster.caster_android.R;

import org.w3c.dom.Text;

/**
 * Created by Teo on 8/6/2015.
 *
 */

public class SignUpActivity extends Activity {
    private int minimumPasswordCharLength = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
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

    public void signUp(View v){
        String email = ((TextView)findViewById(R.id.sign_up_email_field)).getText().toString();
        String password = ((TextView)findViewById(R.id.sign_up_password_field)).getText().toString();
        String passwordConfirm = ((TextView)findViewById(R.id.sign_up_password_confirm_field)).getText().toString();


        //Check password security before creating user
        if (!password.equals(passwordConfirm)){
            Toast.makeText(SignUpActivity.this, "Oops! Looks like your passwords don't match!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < minimumPasswordCharLength){
            Toast.makeText(SignUpActivity.this, "Oops! Looks like your password is too short!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            //TODO: Create user, don't know how to connect to your server yet or exactly what information you need.
        }
    }
}
