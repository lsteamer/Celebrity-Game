package com.elmexicano.lsteamer.celebritygame;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    CountDownTimer appRuns;

    private int sec;

    //Inner class that retrieves the source code.
    protected class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //String in which the url source code will be stored
            String resulte = "";


            try {
                //Read the URL
                URL url = new URL(strings[0]);

                //The pattern we're looking for
                Pattern p = Pattern.compile("td class=\"td-flag(.*?)/></a></td>");

                //Accessing the URL
                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                InputStream in = urlCon.getInputStream();

                //Everything in a reader
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line;

                //While there are things to read
                while((line = reader.readLine() ) != null){

                    //If you find a match
                    Matcher m = p.matcher(line);
                    if(m.find()){

                        resulte = resulte+ "****"+m.group(1);
                    }
                }

                in.close();

                //Returns the full source code of the url provided
                return resulte;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "ERROR";
            } catch (IOException e){
                e.printStackTrace();
                return "ERROR";
            }


        }
    }


    //Method that receives a sourcecode and a Pattern to look for, and returns a String Array with the occurrences
    protected String[] urlCleaner(String page,String pattern){

        int counter=0;

        //Receive the pattern
        Pattern p = Pattern.compile(pattern);
        //And match it with the code provided
        Matcher m = p.matcher(page);

        //count how many instances there are of the pattern
        while(m.find())
            counter++;

        //Create an array String to return
        String[] dataArr = new String[counter];

        //Reset the matcher on first position
        m = p.matcher(page);

        //Add the matches into the Array
        counter=0;
        while (m.find()){

            dataArr[counter] = m.group(1);
            counter++;
        }
        //Return the Array
        return dataArr;
    }


    protected void appRun(){

        //CHANGE THIS SOMEWHERE LATER.
        sec = 60;

        //Timer (time for the app) set
        appRuns = new CountDownTimer(sec*1000,1000) {

            //Changing data ever 'tic' (every second)
            @Override
            public void onTick(long l) {

                //Updating the screen timer
                /*
                timerT is a TextView from the previous app
                timert.setText(""+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(sec*1000),
                        TimeUnit.MILLISECONDS.toSeconds(sec*1000) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sec*1000))));

                */
                //decreasing the screen timer
                sec--;

            }

            @Override
            public void onFinish() {

            }
        };

        appRuns.start();

    }

    protected void countryFlag(View view){
        
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String that catches the result
        String result="";

        //AsyncTask Class
        DownloadTask task = new DownloadTask();


        //Call the Class that will download the source code
        try {
            result = task.execute("http://flagpedia.net/index").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //Arrays to hold the Name of the countries and the code for the URL Image File
        String[] countriesDataNames=null;
        String[] countriesDataURL=null;

        /*
            REMEMBER!!!!
            To add:
            http://flags.fmcdn.net/data/flags/normal/      +    countriesDataURL[counter]

         */

        //Calling the method that 'cleans' the results
        countriesDataNames = urlCleaner(result,"<img alt=\"Flag of (.*?)\"");
        countriesDataURL = urlCleaner(result,"/mini/(.*?)\"");





    }
}
