package com.equinox.mariohernandez.portalviaje;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Dictionary;


/**
 * A placeholder fragment containing a simple view.
 */
public class CalificacionesActivityFragment extends Fragment  {

    private Bundle data ;
    private LayoutInflater inflater;



    public CalificacionesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_calificaciones, container, false);
    }

}
