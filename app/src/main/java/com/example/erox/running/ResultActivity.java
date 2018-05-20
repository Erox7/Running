package com.example.erox.running;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    RunningLogs log;
    TextView results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        results = findViewById(R.id.tv);
        if(getIntent() != null){
            Bundle b = getIntent().getExtras();
            log = (RunningLogs) b.getSerializable("log");
            results.setText(log.toString());
        } else{
            results.setText(getString(R.string.noResults));
        }

    }

    public void returnToMapFunction(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}
