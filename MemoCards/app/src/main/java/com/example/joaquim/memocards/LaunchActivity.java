package com.example.joaquim.memocards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LaunchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String selected_set;
    Spinner spinner_liste_set;
    BaseCards db;
    ArrayList<String> liste_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if(savedInstanceState != null) {

        }
        else{
            MenuFragment menu = new MenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(menu, "menu");
            fragmentTransaction.commit();
        }

        db = new BaseCards(this);
        liste_set = db.getSetsNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, liste_set);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_liste_set = (Spinner) findViewById(R.id.spinner_launch_liste);
        spinner_liste_set.setOnItemSelectedListener(this);
        spinner_liste_set.setAdapter(adapter);
    }

    public void start_learning(View v){
        if(selected_set != null) {
            Intent intent = new Intent(this, LearningActivity.class);
            Bundle b = new Bundle();
            b.putString("set", selected_set);
            intent.putExtras(b);
            if(!db.setIsEmpty(selected_set)) {
                startActivity(intent);
            }
            else{
                Toast annonce = Toast.makeText(this,"The set is empty !", Toast.LENGTH_SHORT);
                annonce.show();
            }
        }
        else{
            Toast annonce = Toast.makeText(this,"No set selected !", Toast.LENGTH_SHORT);
            annonce.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView selectedText = (TextView) parent.getChildAt(0);

        if (selectedText != null) {
            this.selected_set = selectedText.getText().toString();
            selectedText.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
