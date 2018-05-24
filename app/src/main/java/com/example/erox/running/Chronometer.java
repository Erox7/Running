/*package com.example.erox.running;

import android.os.AsyncTask;

public class Chronometer extends AsyncTask<Float, Integer, String> {
    public static boolean stopChrono = false;
    private float startTime;
    @Override
    protected String doInBackground(Float... floats) {
        this.startTime = floats[0];
        publishProgress(0);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... voids){
        float currentTime = System.currentTimeMillis() - this.startTime;
        System.out.println(currentTime);
        String str = Float.toString(currentTime);
        MapsActivity.timeCalculator(str);
    }

    @Override
    protected void onPostExecute(String result) {
        MapsActivity.timeCalculator(result);
    }
}
*/