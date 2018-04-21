package com.example.erox.running;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity{
    CheckBox checkbox;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        checkbox = findViewById(R.id.checkbox);
        password = findViewById(R.id.password);
    }

    public void backToLogin(View view) {

    }

    public void showPasswordDetection(View view) {
        if(((CheckBox)view).isChecked()){
            password.setTransformationMethod(null);
        }else{
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
