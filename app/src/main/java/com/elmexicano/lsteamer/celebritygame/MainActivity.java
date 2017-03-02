package com.elmexicano.lsteamer.celebritygame;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String[] countriesDataNames;
    String[] countriesDataURL;


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



    protected void urlCleaner(String page){

        int counter=0;
        Pattern p = Pattern.compile("<img alt=\"(.*?)\"");
        Pattern q = Pattern.compile("/mini/(.*?)\"");

        Matcher m = p.matcher(page);

        String swat="";
        String swaat="";


        while (m.find()){

            swat = m.group(1);
            Log.i("RESULT",swat);


        }


        m = q.matcher(page);
        while (m.find()){

            swaat = "http://flags.fmcdn.net/data/flags/normal/"+m.group(1);

            Log.i("RESULT",swaat);
        }

        //Pattern
        //p = Pattern.compile("\"(.*?)\"");



        //Log.i("GRUPO 1", swat);
       // Log.i("GRUPO 2", swaat);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String that catches the result
        String result="";

        countriesDataNames = new String[199];
        countriesDataURL = new String[199];


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


        //Calling the method that 'cleans' the results
        urlCleaner(result);


    }
}
