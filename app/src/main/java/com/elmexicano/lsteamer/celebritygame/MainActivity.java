package com.elmexicano.lsteamer.celebritygame;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //Inner class that retrieves the source code.
    protected class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //String in which the url source code will be stored
            String resulte = null;
            try {
                URL url = new URL(strings[0]);

                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();

                InputStream in = urlCon.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data!= -1){
                    char current = (char) data;
                    resulte += current;
                    data = reader.read();

                }

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
        Pattern p = Pattern.compile("<img src=(.*?)/>");

        Matcher m = p.matcher(page);

        ArrayList<String> some = new ArrayList<String>();

        while (m.find()){
            some.add(m.group(1));
        }

        String swat = some.get(1);

        Log.i("THE RESULT - ",swat);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String that catches the result
        String result="";
        //Async Task
        DownloadTask task = new DownloadTask();


        try {
            result = task.execute("http://www.posh24.com/celebrities").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Calling the method that 'cleans' the results
        urlCleaner(result);


    }
}
