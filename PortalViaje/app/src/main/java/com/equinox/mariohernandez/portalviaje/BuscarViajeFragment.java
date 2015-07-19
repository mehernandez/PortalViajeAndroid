package com.equinox.mariohernandez.portalviaje;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;



public class BuscarViajeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_SECTION_NUMBER = "section_number";

    private String sentido;
    private int cupos;
    private int idUbicacion;
    private boolean ubicaciones;
    private JSONArray data;
    private String hash;
    private View rootView;


    public static final String HACIA = "Hacia u";
    public static final String DESDE = "Desde u";

    public BuscarViajeFragment newInstance(int sectionNumber) {
        BuscarViajeFragment fragment = new BuscarViajeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BuscarViajeFragment() {
        sentido = HACIA;
        cupos = 0;
        idUbicacion = -1;
        ubicaciones = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_buscar_viaje, container, false);



        rootView.findViewById(R.id.btn_continuar_buscarViaje1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cupos > 0 && idUbicacion > -1 ) {
                    startActivity(new Intent(getActivity().getBaseContext(), BuscarViaje2Activity.class).putExtra("info",sentido+"_"+idUbicacion+"_"+cupos));
                }else{
                    Toast.makeText(getActivity().getBaseContext(),"Debes llenar toda la información requerida",Toast.LENGTH_SHORT).show();
                }
            }
        });

        getActivity().getSupportLoaderManager().initLoader(1,null,this);

        return rootView;
    }

    public JSONArray darUbicaciones(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "darubicaciones")
                .appendQueryParameter("login", hash)
                .build().toString();

        JSONArray json = null;


        try {

            json = new JSONArray("[]");

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

            json = new JSONObject(res).getJSONArray("arreglo");


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SuperLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dat) {

        hash = dat.getString(Provider.HASH_COLUMN);

        final Button btnUbicaciones = (Button) rootView.findViewById(R.id.btn_ubicacion_buscarViaje1);
        final Button btnCupos = (Button) rootView.findViewById(R.id.btn_cupos_buscarViaje1);

        final Button btnHacia = (Button)rootView.findViewById(R.id.btn_hacia_buscarViaje1);
        final Button btnDesde = (Button)rootView.findViewById(R.id.btn_desde_buscarViaje1);

        btnHacia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHacia.setBackgroundColor(getResources().getColor(R.color.orange));
                btnHacia.setTextColor(getResources().getColor(R.color.white));

                btnDesde.setBackgroundColor(getResources().getColor(R.color.white));
                btnDesde.setTextColor(getResources().getColor(R.color.black));

                sentido = HACIA;
            }
        });

        btnDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDesde.setBackgroundColor(getResources().getColor(R.color.orange));
                btnDesde.setTextColor(getResources().getColor(R.color.white));

                btnHacia.setBackgroundColor(getResources().getColor(R.color.white));
                btnHacia.setTextColor(getResources().getColor(R.color.black));

                sentido = DESDE;
            }
        });

        final NumberPicker pick = (NumberPicker)rootView.findViewById(R.id.number_picker_buscarViaje1);


        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ubicaciones){
                    try {
                        idUbicacion = data.getJSONObject(pick.getValue()).getInt("id");
                        btnUbicaciones.setText(data.getJSONObject(pick.getValue()).getString("nombre"));
                        pick.setVisibility(View.INVISIBLE);

                    }catch (Exception e ){
                        Toast.makeText(getActivity().getBaseContext(),"Error guardando tu ubicación",Toast.LENGTH_SHORT).show();
                    }
                }else{

                    cupos = pick.getValue();
                    btnCupos.setText(""+cupos);
                    pick.setVisibility(View.INVISIBLE);
                }

            }
        });

        btnCupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ubicaciones = false;
                String[] arrr = {"1", "2", "3", "4", "5"};

                pick.setDisplayedValues(null);
                pick.setMaxValue(5);
                pick.setMinValue(1);


                pick.setDisplayedValues(arrr);

                pick.setVisibility(View.VISIBLE);
            }
        });

        btnUbicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                data = darUbicaciones();

                if (data.length() > 0) {


                    try {
                        ubicaciones = true;

                        String[] arrr = new String[data.length()];


                        for (int i = 0; i < arrr.length; i++) {
                            arrr[i] = data.getJSONObject(i).getString("nombre");
                        }

                        pick.setDisplayedValues(null);

                        pick.setMinValue(0);
                        pick.setMaxValue(arrr.length - 1);

                        pick.setDisplayedValues(arrr);

                        pick.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(getActivity().getBaseContext(), "Error leyendo tus ubicaciones", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity().getBaseContext(), "No tienes ubicaciones para seleccionar", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
