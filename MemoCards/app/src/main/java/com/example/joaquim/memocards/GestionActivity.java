package com.example.joaquim.memocards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GestionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText name_set;
    EditText question;
    EditText answer;
    Spinner liste_set1;
    Spinner liste_set2;

    BaseCards db;
    ArrayList<String> liste_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion);

        if(savedInstanceState == null) {
            MenuFragment menu = new MenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(menu, "menu");
            fragmentTransaction.commit();
        }

        name_set = (EditText) findViewById(R.id.editText_set_name);
        question = (EditText) findViewById(R.id.editText_question);
        answer = (EditText) findViewById(R.id.editText_answer);

        liste_set1 = (Spinner) findViewById(R.id.spinner_liste_set);
        liste_set2 = (Spinner) findViewById(R.id.spinner_liste_set2);

        liste_set1.setOnItemSelectedListener(this);
        liste_set2.setOnItemSelectedListener(this);

        db = new BaseCards(this);
        refreshSpinners();
    }

    public void refreshSpinners(){
        liste_set = db.getSetsNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, liste_set);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        liste_set1.setAdapter(adapter);
        liste_set2.setAdapter(adapter);
    }

    public void add_set(View v){
        db.add_set(name_set.getText().toString());
        refreshSpinners();
    }

    public void delete_set(View v){
        db.delete_set(liste_set2.getSelectedItem().toString());
        refreshSpinners();
    }

    public void add_card(View v){

        String set = liste_set1.getSelectedItem().toString();
        String question = this.question.getText().toString();
        String answer = this.answer.getText().toString();

        Card c = new Card(question, answer, 5);

        if(db.add_card(set, c)){
            Toast annonce = Toast.makeText(this, "Ajout réussi !", Toast.LENGTH_SHORT);
            annonce.show();
        }
        else{
            Toast annonce = Toast.makeText(this, "L'ajout à échoué.", Toast.LENGTH_SHORT);
            annonce.show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView selectedText = (TextView) parent.getChildAt(0);
        if (selectedText != null) {
            selectedText.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
