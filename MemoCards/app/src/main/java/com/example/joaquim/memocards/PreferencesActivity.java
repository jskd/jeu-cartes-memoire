package com.example.joaquim.memocards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PreferencesActivity extends PreferenceActivity {

    public static class PreferencesFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();


        if(savedInstanceState != null) {

        }
        else {
            MenuFragment menu = new MenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(menu, "menu");
            fragmentTransaction.commit();
        }

    }
}
