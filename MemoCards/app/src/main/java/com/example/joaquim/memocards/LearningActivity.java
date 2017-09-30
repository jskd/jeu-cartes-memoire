package com.example.joaquim.memocards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Date;

public class LearningActivity extends AppCompatActivity {

    String currentSet;
    Card currentCard = null;
    int currentCard_id;
    BaseCards db;

    EditText edit_answer;
    TextView view_result;
    TextView view_solution;
    RatingBar rating;
    Button button_validate;
    Button button_next_card;
    ProgressBar timer_bar;

    CountDownTimer mCountDownTimer;
    int timer_duration = 10; // seconds
    int current_timer = 0;
    boolean validation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        MenuFragment menu = new MenuFragment();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        timer_duration = Integer.parseInt(settings.getString("timer_duration", "10"));


        db = new BaseCards(this);
        edit_answer = (EditText) findViewById(R.id.editText_learning_answer);
        view_result = (TextView) findViewById(R.id.textView_learning_result);
        view_solution = (TextView) findViewById(R.id.textView_learning_solution);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        button_validate = (Button) findViewById(R.id.button_learning_validate);
        button_next_card = (Button) findViewById(R.id.button_learning_next);
        timer_bar = (ProgressBar) findViewById(R.id.progressBar_learning_time);

        timer_bar.setMax(timer_duration);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            currentSet = b.getString("set");
        }

        if(savedInstanceState != null) {
            current_timer = savedInstanceState.getInt("current_timer");
            validation = savedInstanceState.getBoolean("validation");
            currentCard_id = savedInstanceState.getInt("current_card_id");
            view_result.setText(savedInstanceState.getString("result"));
            view_solution.setText(savedInstanceState.getString("solution"));

            currentCard = db.getCardById(currentSet, currentCard_id);

            mCountDownTimer = new CountDownTimer(( (timer_duration-current_timer) * 1000), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    current_timer++;
                    timer_bar.setProgress(current_timer);
                }

                @Override
                public void onFinish() {
                    current_timer++;
                    timer_bar.setProgress(current_timer);

                    if (validation != true)
                        validate(null);
                }
            };

            if(validation == false)
                mCountDownTimer.start();
        }
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(menu, "menu");
            fragmentTransaction.commit();

            settings = getSharedPreferences("memocards_last_uses", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(currentSet, new Date().getTime());
            editor.apply();

            if(db.setIsFinish(currentSet)){
                button_validate.setEnabled(false);
                Toast annonce = Toast.makeText(this,"Congratulation ! The set is learned !", Toast.LENGTH_SHORT);
                annonce.show();
                finish();
            }
            else{
                new_card();
            }
        }

        if(validation){
            button_next_card.setEnabled(true);
            button_validate.setEnabled(false);
        }
        else{
            button_next_card.setEnabled(false);
            button_validate.setEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        view_result = (TextView) findViewById(R.id.textView_learning_result);
        view_solution = (TextView) findViewById(R.id.textView_learning_solution);

        savedInstanceState.putInt("current_timer", current_timer);
        savedInstanceState.putBoolean("validation", validation);
        savedInstanceState.putInt("current_card_id", currentCard_id);
        savedInstanceState.putString("result", view_result.getText().toString());
        savedInstanceState.putString("solution", view_solution.getText().toString());
    }

    public void new_card(){
        validation = false;
        edit_answer.setText("");
        current_timer = 0;
        timer_bar.setProgress(current_timer);

        // CHOIX DE LA BOITE
        SharedPreferences settings = getSharedPreferences("memocards_current_box", 0);
        int current_box = settings.getInt(currentSet, 0);
        Log.v("CURRENT_BOX", String.valueOf(current_box));

        while(!db.hasCardsInBox(currentSet, current_box)){
            current_box = (current_box + 1) % 6;
            Log.v("NEW_CURRENT_BOX", String.valueOf(current_box));
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(currentSet, current_box);
        editor.apply();

        // RECUPERATION D'UNE CARTE
        currentCard = db.getRandomCardOf(currentSet, current_box);
        currentCard_id = currentCard.getId();

        CardFragment fragment;
        if(currentCard != null) fragment = CardFragment.newInstance(currentCard.getQuestion());
        else fragment = new CardFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.card_fragment, fragment);
        fragmentTransaction.commit();

        if(currentCard != null) {
            rating.setRating(currentCard.getPriority());

            mCountDownTimer = new CountDownTimer((timer_duration * 1000), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    current_timer++;
                    timer_bar.setProgress(current_timer);
                }

                @Override
                public void onFinish() {
                    current_timer++;
                    timer_bar.setProgress(current_timer);

                    if (validation != true)
                        validate(null);
                }
            };
            mCountDownTimer.start();
        }
    }

    public void validate(View v){
        validation = true;
        mCountDownTimer.cancel();

        String answer = edit_answer.getText().toString();
        String solution = currentCard.getAnswer();

        int priority = currentCard.getPriority();
        int rate = priority;

        if(answer.equals(solution)){
            view_result.setText("Correct !");
            view_solution.setText("");

            if(priority > 0) {
                rate = priority - 1;
                rating.setRating(rate);
            }
        }
        else{
            view_result.setText("Wrong !");
            view_solution.setText("Solution : " + solution);

            if(priority < 5) {
                rate = priority + 1;
                rating.setRating(rate);
            }
        }

        button_next_card.setEnabled(true);
        button_validate.setEnabled(false);
    }

    public void next_card(View v){
        int priority = (int)rating.getRating();

        SharedPreferences settings = getSharedPreferences("memocards_current_box", 0);
        int current_box = settings.getInt(currentSet, 0);
        if(current_box == 5 && priority == 0)
            priority = -1;

        db.setPriorityOf(currentSet, currentCard_id, priority);

        if(db.setIsFinish(currentSet)){
            Toast annonce = Toast.makeText(this,"Congratulation ! The set is learned !", Toast.LENGTH_SHORT);
            annonce.show();
            finish();
        }
        else{
            new_card();
        }

        button_validate.setEnabled(true);
        button_next_card.setEnabled(false);
        view_result.setText("");
        view_solution.setText("");
    }
}
