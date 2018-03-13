package com.example.erox.running;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public EditText userET;
    public EditText passwordET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userET = findViewById(R.id.user);
        passwordET = findViewById(R.id.password);
    }
    public void logInFunction(View view){

        if(getString(R.string.correctPasswd).equals(passwordET.getText().toString())
        && getString(R.string.correctUsr).equals(userET.getText().toString())){

            Intent in = new Intent(this, MapsActivity.class );
            startActivity(in);
        }
    }
}
