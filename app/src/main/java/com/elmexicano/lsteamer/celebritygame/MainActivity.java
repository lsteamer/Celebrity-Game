package com.elmexicano.lsteamer.celebritygame;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    private boolean started;
    private  int counter, score, winner, prevWin, res;
    private Button[] selectors;
    private TextView resultTV, scoreTV;
    private ImageView bandera;

    private int[] currentOpt;

    //ArrayList<Integer> previousFlags;



    DownloadImage locTask;

    //Random number Class
    Random randNum;

    String[] countriesDataNames;
    String[] countriesDataURL;

    /*
     * Considering NOT using a timer.
     * Functionality is in place in case I do.
    CountDownTimer appRuns;
    private int sec;
    protected void appRun(){

        //CHANGE THIS SOMEWHERE LATER.
        sec = 60;

        //Timer (time for the app) set
        appRuns = new CountDownTimer(sec*1000,1000) {

            //Changing data ever 'tic' (every second)
            @Override
            public void onTick(long l) {

                //Updating the screen timer

                timerT is a TextView from the previous app
                timert.setText(""+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(sec*1000),
                        TimeUnit.MILLISECONDS.toSeconds(sec*1000) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sec*1000))));


                //decreasing the screen timer
                sec--;

            }

            @Override
            public void onFinish() {

            }
        };

        appRuns.start();

    }

    */
    //Inner class that given a URL, Downloads an image
    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap result;

            try{
                URL url = new URL(params[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                result = BitmapFactory.decodeStream(inputStream);

                return result;

            }
            catch (MalformedURLException e){
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }


    }


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




    protected void countryFlag(View view){
        //In case we have the Intro Screen, make the rest of the buttons appear and give a choice
        if(score==-1){
            //It has started
            started = true;
            //Animate the buttons
            for(int i = 0; i<4; i++)
                selectors[i].animate().alpha(1).start();
            //Score and counter are no longer -1
            score++;
            counter++;

            //'flag' for when you rotate the screen
            prevWin = 2000;
        }
        //If the intro screen is no longer there
        else {
            res = (Integer) view.getTag();
            String dynamicURL = "https://en.wikipedia.org/wiki/" + countriesDataNames[currentOpt[winner]];
            String linkedText;
            if(res==1) {
                score++;
                linkedText =  "Correct! That was "+ String.format("<a href=\"%s\">" + countriesDataNames[currentOpt[winner]]+ "</a> ", dynamicURL);
                scoreTV.setText(Html.fromHtml(linkedText));
                scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else{

                linkedText =  "Wrong! That was "+ String.format("<a href=\"%s\">" + countriesDataNames[currentOpt[winner]]+ "</a> ", dynamicURL);
                scoreTV.setText(Html.fromHtml(linkedText));
                scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
            }
            counter++;
            resultTV.setText(score + " / " + counter);
            prevWin = currentOpt[winner];

        }

        //Select the 4 options
        for(int i=0; i<4; i++) {
            currentOpt[i] = (randNum.nextInt(199));
        }
        //Select one of the numbers to be the winner
        winner = (randNum.nextInt(4));

        newScreen();

    }

    //New screen created based on determined values
    protected void newScreen(){

        locTask = new DownloadImage();
        Bitmap image=null;
        for(int i=0; i<4; i++){

            selectors[i].setText(countriesDataNames[currentOpt[i]]);
            selectors[i].setTag(0);
            if(i==winner){
                try {
                    image = locTask.execute(countriesDataURL[currentOpt[i]]).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                image = Bitmap.createScaledBitmap(image, 810, 450, true);

                bandera.setImageBitmap(image);
            }
        }
        selectors[winner].setTag(1);

    }


    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        //bundle.putIntegerArrayList("Previous",previousFlags);
        bundle.putInt("Winner",winner);
        bundle.putInt("Previous Winner", prevWin);
        bundle.putIntArray("Current Options",currentOpt);
        bundle.putInt("Score",score);
        bundle.putInt("Total",counter);
        bundle.putInt("Choice",res);
        bundle.putStringArray("Countries list URL", countriesDataURL);
        bundle.putStringArray("Countries list Names", countriesDataNames);
        bundle.putBoolean("Started",started);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res=0;

        //If the screen is rotated, choose a different layout
        if(Configuration.ORIENTATION_PORTRAIT==this.getResources().getConfiguration().orientation)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_main_lc);




        //String that catches the result
        String result="";

        //AsyncTask Class
        DownloadTask task = new DownloadTask();

        //Random number toolkit defined
        randNum = new Random();

        //Has the app started?
        started=false;

        //Buttons
        selectors = new Button[4];
        selectors[0] = (Button)  findViewById(R.id.button1);
        selectors[1] = (Button)  findViewById(R.id.button2);
        selectors[2] = (Button)  findViewById(R.id.button3);
        selectors[3] = (Button)  findViewById(R.id.button4);


        //TextViews
        resultTV = (TextView) findViewById(R.id.result);
        scoreTV = (TextView) findViewById(R.id.score);
        resultTV.setText("");
        scoreTV.setText("");

        //ImageView of the flag displayed
        bandera = (ImageView) findViewById(R.id.flagImage);

        if(savedInstanceState!=null){
            started = (boolean) savedInstanceState.get("Started");

            winner = (Integer) savedInstanceState.get("Winner");
            prevWin = (Integer) savedInstanceState.get("Previous Winner");
            counter = (Integer) savedInstanceState.get("Total");
            score = (Integer) savedInstanceState.get("Score");

            res = (Integer) savedInstanceState.get("Choice");

            currentOpt = (int[]) savedInstanceState.get("Current Options");

            countriesDataURL = (String[]) savedInstanceState.get("Countries list URL");
            countriesDataNames = (String[]) savedInstanceState.get("Countries list Names");

            if(started){

                for(int i = 0; i<4; i++)
                    selectors[i].animate().alpha(1).start();

                if(prevWin!=2000){

                    if(res==1) {
                        scoreTV.setText("Correct! That was "+ countriesDataNames[prevWin]);
                    }
                    else{
                        scoreTV.setText("Wrong. That was "+ countriesDataNames[prevWin]);
                    }

                    resultTV.setText(score + " / " + counter);

                }


                newScreen();
            }
            else{

            }


        }
        else{
            //Call the Class that will download the source code
            try {
                result = task.execute("http://flagpedia.net/index").get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //Calling the method that 'cleans' the results
            countriesDataNames = urlCleaner(result,"<img alt=\"Flag of (.*?)\"");
            countriesDataURL = urlCleaner(result,"/mini/(.*?)\"");

            //Adding the full URL to the Images
            for(int i=0; i<countriesDataURL.length; i++ )
                countriesDataURL[i] = "http://flags.fmcdn.net/data/flags/normal/" + countriesDataURL[i];

            //Overall counter
            counter = -1;
            score = counter;



            //Saved instances of the 4 options shown
            currentOpt = new int[4];

        }



        //previousFlags = new ArrayList<Integer>();

    }
}
