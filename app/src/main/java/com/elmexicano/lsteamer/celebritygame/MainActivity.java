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
import android.widget.RelativeLayout;
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

    //Checks if the game has started
    private boolean started;

    //Keeps the Counter, the score, current country that's the winner, past county that was the winner and which button was clicked by the user
    private int counter, score, winner, prevWin, res;
    //It is to note, however, that prevWin ended up being a flag for when the game has been activated, but either no choice has been made, or there is a pause.

    //Array for the 4 Buttons
    private Button[] selectors;
    //The 2 TextViews in the game
    private TextView resultTV, scoreTV;
    //The image in the game
    private ImageView bandera;

    //Win Screen
    RelativeLayout winScreen;

    //The String that includes the wikipedia link
    private String linkedText;

    //The 4 numbers/countries that constitute the current clickable options
    private int[] currentOpt;

    //Flags that have been correctly guessed before
    private ArrayList<Integer> usedFlags;
    //Flags that have been correctly guessed before plus the options in this run
    private ArrayList<Integer> currentRun;


    DownloadImage locTask;

    //Random number Class
    Random randNum;

    //Names and URL
    String[] countriesDataNames;
    String[] countriesDataURL;

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
    public class DownloadTask extends AsyncTask<String, Void, String>{

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
    public String[] urlCleaner(String page,String pattern){

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


    //Method that's called whenever the user presses a button
    //The logic of the game is in this method
    public void countryFlag(View view){

        //PREPPING THE SCREEN/LOGIC OF THE GAME

        //Game started but no option has been selected, counter has not started, make the rest of the buttons appear and give a choice
        if(score==-1){
            //It has started
            started = true;
            //Animate the buttons
            for(int i = 0; i<4; i++){
                selectors[i].animate().alpha(1).start();
                selectors[i].setClickable(true);

            }

            //Score and counter are no longer -1
            score++;
            counter++;

            //'flag' for when you rotate the screen to know that the game has started, yet no option has been selected yet
            prevWin = 2000;
        }

        //Game started and options have been selected
        else if(started) {
            //See the tag of the Country the user has selected
            res = (Integer) view.getTag();

            //Give a clickable link to wikipedia of the previous country
            String dynamicURL = "https://en.wikipedia.org/wiki/" + countriesDataNames[currentOpt[winner]];

            //If the previous answer was right
            if(res==1) {
                score++;
                linkedText =  "Correct! That was "+ String.format("<a href=\"%s\">" + countriesDataNames[currentOpt[winner]]+ "</a> ", dynamicURL);
                scoreTV.setText(Html.fromHtml(linkedText));
                scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
                usedFlags.add(currentOpt[winner]);
            }
            //If the previous answer was wrong
            else if(res==0){

                linkedText =  "Wrong! That was "+ String.format("<a href=\"%s\">" + countriesDataNames[currentOpt[winner]]+ "</a> ", dynamicURL);
                scoreTV.setText(Html.fromHtml(linkedText));
                scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
            }

            //Update the counter
            counter++;
            resultTV.setText(score + " / " + counter);

            //let the variable know who was the previous winner
            prevWin = currentOpt[winner];

        }

        //If we're coming out of a score screen.
        if(started==false){
            //Start the game again
            started=true;
            prevWin=2500;
            //activate the buttons
            for(int i = 0; i<4; i++){
                selectors[i].animate().alpha(1).start();
                selectors[i].setClickable(true);
            }

            //Hide the score screen
            winScreen = (RelativeLayout) findViewById(R.id.winLay);
            winScreen.animate().alpha(0).start();
        }

        //If the counter reaches a certain point. Pause the game and call the scoreScreen
        //The stops in the game are 25, 50, 100 & 200
        if((counter==25||counter==50||counter==100||counter==200)&&prevWin!=2500){
            started=false;
            scoreScreen();
        }


        //If the game is not paused
        if (started){
            //Select one of the numbers to be the winner
            winner = (randNum.nextInt(4));
            currentRun = usedFlags;


            //If the winner exists before, select again
            currentOpt[winner] = (randNum.nextInt(199));
            while (currentRun.contains(currentOpt[winner])) {
                currentOpt[winner] = (randNum.nextInt(199));
            }

            //Add the winner to the 'banned for this run' list
            currentRun.add(currentOpt[winner]);


            //Download the image process
            locTask = new DownloadImage();
            Bitmap image=null;

            try {
                image = locTask.execute(countriesDataURL[currentOpt[winner]]).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            //Selects the 4 options
            for(int i=0; i<4; i++) {
                if(winner!=i) {
                    currentOpt[i] = (randNum.nextInt(199));
                    while (currentRun.contains(currentOpt[i])) {
                        currentOpt[i] = (randNum.nextInt(199));
                    }

                    currentRun.add(currentOpt[i]);

                }
            }

            //Call the new method to fill the screen
            newScreen(image);

        }


    }

    //Method that creates the Game screen.
    protected void newScreen(Bitmap image){

        //Buttons are set
        for(int i=0; i<4; i++){

            selectors[i].setText(countriesDataNames[currentOpt[i]]);
            selectors[i].setTag(0);

        }
        //Image arranged
        image = Bitmap.createScaledBitmap(image, 810, 450, true);
        bandera.setImageBitmap(image);
        //Set the winner tag
        selectors[winner].setTag(1);

    }
    /*
     * newscreen and scoreScreen could be the same method.
     * Only difference is if started is true or not, the bitmap could be null if false
     * I'll fix that later on. Would simplify the app, make it more readable and manageable
     */


    //Method that creates the score screen.
    protected void scoreScreen(){


        //Manipulating the congratulation screens
        TextView congr = (TextView) findViewById(R.id.cong);
        TextView tryAgain = (TextView) findViewById(R.id.tryagain);


        //Show the Score Screen
        winScreen = (RelativeLayout) findViewById(R.id.winLay);
        winScreen.animate().alpha(1).start();


        //Hide all the buttons
        for(int i = 0; i<4; i++){
            selectors[i].animate().alpha(0).start();
            selectors[i].setClickable(false);

        }

        //If you have a perfect score after the 199 flags
        if(score==199){
            bandera.setImageResource(R.drawable.damson);
            TextView tryAgain2 = (TextView) findViewById(R.id.tryagain2);
            congr.setText("Well I'm impressed");
            tryAgain.setText("You're good at this");;
            tryAgain2.setText("Maybe a bit too good");

        }
        else{

            //Congratulation text
            congr = (TextView) findViewById(R.id.cong);

            if(score==counter)
                congr.setText("Perfect score!");
            else if(score > (counter*(90.0f/100.0f)))
                congr.setText("Impressive");
            else if(score > (counter*(80.0f/100.0f)))
                congr.setText("Great");
            else if(score > (counter*(70.0f/100.0f)))
                congr.setText("Very Good");
            else if(score > (counter*(60.0f/100.0f)))
                congr.setText("Good");
            else if(score > (counter*(50.0f/100.0f)))
                congr.setText("Alright");
            else if(score > (counter*(40.0f/100.0f)))
                congr.setText("You need to get better");
            else if(score > (counter*(30.0f/100.0f)))
                congr.setText("At least you're trying");
            else
                congr.setText("You're \"good\" at this");

            //Score TextView
            TextView scoret = (TextView) findViewById(R.id.scorete);
            scoret.setText(""+score);
            //Total TextView
            TextView totalt = (TextView) findViewById(R.id.totalte);
            totalt.setText(""+counter);

            //Hide the result
            resultTV.setText("");
            //Shown the info (Wikipedia Link) for the previous one
            scoreTV.setText(Html.fromHtml(linkedText));
            scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
            //Set the App Banner
            bandera.setImageResource(R.drawable.flagintro);

            //No more buttons to show
            if(counter==200){
                tryAgain = (TextView) findViewById(R.id.tryagain);
                tryAgain.setText("200 turns? You're committed!");
            }
            //Offer the "Keep going"
            else{
                selectors[3].animate().alpha(1).start();
                selectors[3].setClickable(true);
                selectors[3].setText("Do "+(counter*2)+" more!");

            }

        }
    }



    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);

        //Variables that help build the app
        bundle.putInt("Winner",winner);
        bundle.putInt("Previous Winner", prevWin);
        bundle.putIntArray("Current Options",currentOpt);
        bundle.putInt("Score",score);
        bundle.putInt("Total",counter);
        bundle.putInt("Choice",res);
        bundle.putStringArray("Countries list URL", countriesDataURL);
        bundle.putStringArray("Countries list Names", countriesDataNames);
        bundle.putBoolean("Started",started);
        bundle.putIntegerArrayList("Used Flags",usedFlags);
        bundle.putIntegerArrayList("Current Run",currentRun);
        bundle.putString("Linked Text",linkedText);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        winScreen = (RelativeLayout) findViewById(R.id.winLay);
        //winScreen.animate().alpha(1).start();

        //nothing picked
        res=0;

        //Intro screen
        score = -1;

        //If the screen is rotated, choose a different layout
        if(Configuration.ORIENTATION_PORTRAIT==this.getResources().getConfiguration().orientation)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_main_lc);


        //Flags that will still show up
        usedFlags = new ArrayList<Integer>();
        currentRun = new ArrayList<Integer>();

        //Text to create a clickable link
        linkedText= "";

        //String that catches the result
        String  result="";

        //AsyncTask Class
        DownloadTask task = new DownloadTask();

        //Random number toolkit defined
        randNum = new Random();

        //Has the app started?
        started=false;

        //Buttons
        selectors = new Button[4];
        selectors[0] = (Button)  findViewById(R.id.button1);
        selectors[0].setClickable(false);
        selectors[1] = (Button)  findViewById(R.id.button2);
        selectors[2] = (Button)  findViewById(R.id.button3);
        selectors[2].setClickable(false);
        selectors[3] = (Button)  findViewById(R.id.button4);
        selectors[3].setClickable(false);


        //TextViews
        resultTV = (TextView) findViewById(R.id.result);
        scoreTV = (TextView) findViewById(R.id.score);
        resultTV.setText("");
        scoreTV.setText("");

        //ImageView of the flag displayed
        bandera = (ImageView) findViewById(R.id.flagImage);

        if(savedInstanceState!=null){

            //Get data of the variables
            started = (boolean) savedInstanceState.get("Started");
            winner = (Integer) savedInstanceState.get("Winner");
            prevWin = (Integer) savedInstanceState.get("Previous Winner");
            counter = (Integer) savedInstanceState.get("Total");
            score = (Integer) savedInstanceState.get("Score");
            res = (Integer) savedInstanceState.get("Choice");
            currentRun = (ArrayList<Integer>) savedInstanceState.get("Current Run");
            usedFlags = (ArrayList<Integer>) savedInstanceState.get("Used Flags");
            linkedText = (String) savedInstanceState.get("Linked Text");
            currentOpt = (int[]) savedInstanceState.get("Current Options");
            countriesDataURL = (String[]) savedInstanceState.get("Countries list URL");
            countriesDataNames = (String[]) savedInstanceState.get("Countries list Names");

            //If game has been started beyond the 'intro' screen
            if(started){
                for(int i = 0; i<4; i++) {
                    selectors[i].animate().alpha(1).start();
                    selectors[i].setClickable(true);
                }

                //If the user has a previous Right or wrong
                if(prevWin!=2000){


                    //Set the Wikipedia clickable link
                    if(res==1){
                        scoreTV.setText(Html.fromHtml(linkedText));
                        scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    else{
                        scoreTV.setText(Html.fromHtml(linkedText));
                        scoreTV.setMovementMethod(LinkMovementMethod.getInstance());
                    }

                    resultTV.setText(score + " / " + counter);
                }

                //Download the image process
                locTask = new DownloadImage();
                Bitmap image=null;

                try {
                    image = locTask.execute(countriesDataURL[currentOpt[winner]]).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                //Fill the screen
                newScreen(image);
            }
            else{

                scoreScreen();
            }

        }
        else{
            if(score==-1){
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
                for(int i=0; i<countriesDataURL.length; i++ ) {
                    countriesDataURL[i] = "http://flags.fmcdn.net/data/flags/normal/" + countriesDataURL[i];
                }
                //Overall counter
                counter = -1;
                score = counter;

                //Saved instances of the 4 options shown
                currentOpt = new int[4];

            }

        }

    }
}
