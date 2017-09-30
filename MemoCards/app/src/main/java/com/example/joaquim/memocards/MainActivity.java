package com.example.joaquim.memocards;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    Intent cardsUseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            MenuFragment menu = new MenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(menu, "menu");
            fragmentTransaction.commit();
        }

        cardsUseService = new Intent(MainActivity.this, CardsUseService.class);
        startService(cardsUseService);
    }

    public void onStop(){
        super.onStop();
        if(stopService(new Intent(MainActivity.this, CardsUseService.class)))
            Log.v("CardsUseService", "stopService was successful");
        else
            Log.v("CardsUseService", "stopService was unsuccessful");
    }


    public void gestion_cards(View v){
        Intent intent = new Intent(this, GestionActivity.class);
        startActivity(intent);
    }

    public void launch_session(View v){
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
    }

    public void download_cards(View v){
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

    public void preferences(View v){
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }
}
