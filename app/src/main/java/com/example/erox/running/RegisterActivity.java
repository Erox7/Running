package com.example.erox.running;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailET;
    private EditText passwordET;
    private EditText nameET;
    private String password, email, name, token;
    private FirebaseAuth mAuth;
    private User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        passwordET = findViewById(R.id.password);
        emailET = findViewById(R.id.user);
        nameET = findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        token = FirebaseInstanceId.getInstance().getToken();
    }

    public void backToLogin(View view) {
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        name = nameET.getText().toString();
        if (!(email == null || password == null)) {
            if (validateEmail(email)) {
                if (name != null) {
                    userObj = new User(name, email, password, token);
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userObj.setUID(user.getUid());
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("User").child(user.getUid()).setValue(userObj);
                                    Toast.makeText(RegisterActivity.this, getString(R.string.authSuccs),
                                            Toast.LENGTH_SHORT).show();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent in = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(in);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, getString(R.string.authFail),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this, getString(R.string.invalidMail),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, getString(R.string.missInformation),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void showPasswordDetection(View view) {
        if (((CheckBox) view).isChecked()) {
            passwordET.setTransformationMethod(null);
        } else {
            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
