package com.satersoft.stardestroyer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.satersoft.stardestroyer.domain.InfoWrapper;
import com.satersoft.stardestroyer.ui.FullScreenActivity;
import com.satersoft.stardestroyer.ui.SerialBitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WelcomeActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new LoadingManager().execute();
    }


    private class LoadingManager extends AsyncTask<Void, Integer, InfoWrapper> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
/*
            //Create a new progress dialog
            progressDialog = new ProgressDialog(LoadingScreenActivity.this);
            //Set the progress dialog to display a horizontal progress bar
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //Set the dialog title to 'Loading...'
            progressDialog.setTitle("Loading...");
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Loading application View, please wait...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(false);
            //The maximum number of items is 100
            progressDialog.setMax(100);
            //Set the current progress to zero
            progressDialog.setProgress(0);
            //Display the progress dialog
*/

        }

        @Override
        protected InfoWrapper doInBackground(Void... params) {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            //try {
                InfoWrapper info;
                //Get the current thread's token
                synchronized (this) {
                    //Initialize an integer (that will act as a counter) to zero
                    /*int counter = 0;
                    //While the counter is smaller than four
                    while (counter <= 4) {
                        //Wait 850 milliseconds
                        this.wait(850);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter * 25);*/
                    info = new InfoWrapper();
                    loadXML(info);
                    publishProgress(50);
                    loadBitmaps(info);
                    publishProgress(100);

                }
                return info;
            /*} catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //return null;
        }

        private void loadXML(InfoWrapper info) {
            try {
                StringBuilder buf=new StringBuilder();
                InputStream xml=getAssets().open("entities.xml");
                BufferedReader in = new BufferedReader(new InputStreamReader(xml, "UTF-8"));
                String str;

                while ((str=in.readLine()) != null) { buf.append(str); }
                in.close();
                info.file = buf.toString();
            } catch(IOException e) {}
        }

        private void loadBitmaps(InfoWrapper info) {
            info.bitHash.put(R.drawable.enemyheavy, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.enemyheavy)));
            info.bitHash.put(R.drawable.enemylight, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.enemylight)));
            info.bitHash.put(R.drawable.enemymedium, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.enemymedium)));
            info.bitHash.put(R.drawable.enemyprojectile, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.enemyprojectile)));
            info.bitHash.put(R.drawable.playerheavy, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerheavy)));
            info.bitHash.put(R.drawable.playerlight, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerlight)));
            info.bitHash.put(R.drawable.playermedium, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playermedium)));
            info.bitHash.put(R.drawable.playerprojectile, new SerialBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerprojectile)));
        }

        private Bitmap getBitmapFromAsset(String strName)
        {
            AssetManager assetManager = getAssets();
            InputStream istr = null;
            try {
                istr = assetManager.open(strName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            return bitmap;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            //progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(InfoWrapper result) {
            //close the progress dialog
            //progressDialog.dismiss();
            //initialize the View
            //setContentView(R.layout.activity_main);
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("info", result);
            startActivity(intent);
            finish();
        }
    }
}
