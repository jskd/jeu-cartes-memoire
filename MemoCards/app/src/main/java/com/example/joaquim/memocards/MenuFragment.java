package com.example.joaquim.memocards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends android.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_home:
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_gestion:
                intent = new Intent(getActivity(), GestionActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_learn:
                intent = new Intent(getActivity(), LaunchActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_download:
                intent = new Intent(getActivity(), DownloadActivity.class);
                startActivity(intent);
                return true;
            case R.id.menun_settings:
                intent = new Intent(getActivity(), PreferencesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
