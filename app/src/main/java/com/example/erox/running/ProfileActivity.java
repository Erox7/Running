package com.example.erox.running;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private TextView nameTv, emailTv;
    private String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();
        nameTv = findViewById(R.id.user_profile_name);
        emailTv = findViewById(R.id.user_profile_email);
        loadData();
    }

    private void loadData() {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference name = mData.getReference("/User/" + uid + "/name");
        name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameTv.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference email = mData.getReference("/User/" + uid + "/email");
        email.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String emails = emailTv.getText().toString() + dataSnapshot.getValue().toString();
                emailTv.setText(emails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void nameChanger(View view) {
        Intent intent = new Intent(this, EditVariables.class);
        startActivity(intent);

    }
}
