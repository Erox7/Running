package com.example.erox.running;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class EditVariables extends AppCompatActivity {
    EditText editName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_variables);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();
        editName = findViewById(R.id.edit_name);
    }

    public void backToProfile(View view) {

        String newName = editName.getText().toString();
        if (!TextUtils.isEmpty(newName)) {
            String uid = mAuth.getCurrentUser().getUid();
            mData.getReference("/User/" + uid + "/name").setValue(newName).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "ENTRY A NEW NAME, BRO", Toast.LENGTH_SHORT).show();
        }
    }
}
