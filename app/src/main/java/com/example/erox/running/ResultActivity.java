package com.example.erox.running;

import android.content.Intent;
import android.net.Uri;
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

public class ResultActivity extends AppCompatActivity {
    RunningLogs log;
    TextView results;
    String finalUrl,userName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        results = findViewById(R.id.tv);
        if (getIntent() != null) {
            Bundle b = getIntent().getExtras();
            log = (RunningLogs) b.getSerializable("log");
            results.setText(log.toString());
        } else {
            results.setText(getString(R.string.noResults));
        }
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();

    }

    public void returnToMapFunction(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void returnToServer(View view) {
        prepareString();
        goToUrl(finalUrl);
    }

    private void prepareString() {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference name = mData.getReference("/User/" + uid + "/name");
        name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finalUrl = "https://peaceful-peak-99231.herokuapp.com/timePass?";
        finalUrl += "time=" + log.getTimeInSeconds();
        finalUrl += "&avgTime=" + log.getAvgWalkingTime().toString();
        finalUrl += "&name=" + userName;
    }

    private void goToUrl(String s) {
        Uri uriUrl = Uri.parse(s);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
