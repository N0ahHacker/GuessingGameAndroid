package com.noahhacker.guessinggame;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText txtGuess;
    private Button btnGuess;
    private TextView lblOutput;
    private int theNumber;
    private int range =100;
    private int chances = 7;
    private TextView lblRange;
    private int numberOfTries;

    public void checkGuess() {
        String guessText = txtGuess.getText().toString();
        String message = "";
        numberOfTries = numberOfTries +1;
        try{
            int guess = Integer.parseInt(guessText);
            if(guess < theNumber)
                message = guess +" is too low, try again, you have " + (chances - numberOfTries) +" guesses left";
            else if(guess > theNumber)
                message = guess + " is too high, try again, you have " + (chances - numberOfTries) +" guesses left";
            else{
                message = guess +" is correct. You got it in "+ numberOfTries+" guesses! Let's play again!";
                Toast.makeText(MainActivity.this, message,
                        Toast.LENGTH_SHORT).show();
                ///adds one to number of games won for each win
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                int gamesWon = preferences.getInt("gamesWon", 0) +1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("gamesWon", gamesWon);
                editor.apply();
                newGame();
            }
        }catch (Exception e) {
            message = "Enter a whole number between 1 and "+ range+ ".";
        }finally {
            if(numberOfTries >= chances) {
                message = "You ran out of tries, the number was " +theNumber+ ". Better luck next time!";
                Toast.makeText(MainActivity.this, message,
                        Toast.LENGTH_LONG).show();
                newGame();
            }
            lblOutput.setText(message);
            txtGuess.requestFocus();
            txtGuess.selectAll();
        }
    }
    public void reset(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int gamesWon = 0;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("gamesWon", gamesWon);
        editor.apply();
        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.putInt("totalGames", 0);
        editor2.apply();
    }

    public void newGame() {
        theNumber = (int) (Math.random() * range + 1);
        chances = (int)(Math.log(range)/Math.log(2) +1);
        lblRange.setText("Enter a number between 1 and "+ range + ".");
        txtGuess.setText(""+ range/2);
        txtGuess.requestFocus();
        txtGuess.selectAll();
        numberOfTries = 0;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int totalGames = preferences.getInt("totalGames", 0)+1;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("totalGames", totalGames);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtGuess = (EditText)findViewById(R.id.txtGuess);
        btnGuess = (Button) findViewById(R.id.btnGuess);
        lblOutput = (TextView) findViewById(R.id.lblOutput);
        lblRange = (TextView) findViewById(R.id.textView3);
        newGame();

        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });
        txtGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkGuess();
                return true;
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                final CharSequence[] items = {"1 to 100", "1 to 1000", "1 to 10000"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select the Range: ");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                   ///@Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item) {
                            case 0:
                                range =100;
                                newGame();
                                break;
                            case 1:
                                range = 1000;
                                newGame();
                                break;
                            case 2:
                                range = 10000;
                                newGame();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_newGame:
                newGame();
                return true;
            case R.id.action_gamestats:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                int gamesWon = preferences.getInt("gamesWon", 0);
                int totalGames = preferences.getInt("totalGames", 0);
                float winPercentage = ((float)gamesWon/totalGames)*100;
                AlertDialog statDialog = new AlertDialog.Builder(MainActivity.this).create();
                statDialog.setTitle("Guessing Game Stats");
                statDialog.setMessage("You have won " + gamesWon + " games, and played "+ totalGames+ " games. That's "+winPercentage+"% Good Job!");
                statDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                statDialog.show();
                return true;
            case R.id.action_about:
                AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
                aboutDialog.setTitle("About Guessing Game");
                aboutDialog.setMessage("(c)2018 Noah Hacker");
                aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                aboutDialog.show();
                return true;
            case R.id.action_resetstats:
                AlertDialog resetStatsDialog = new AlertDialog.Builder(MainActivity.this).create();
                resetStatsDialog.setTitle("Reset Stats");
                resetStatsDialog.setMessage("Do your really want to reset your stats?");
                resetStatsDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes, really",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                            }
                        });
                resetStatsDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }
}
