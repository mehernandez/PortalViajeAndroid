package com.equinox.mariohernandez.portalviaje;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViajesDisponiblesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private JSONArray data ;
    private LayoutInflater inflater;
    private View rootView;
    private String hash;
    private int num =1;

    public static ViajesDisponiblesFragment newInstance(int sectionNumber) {
        ViajesDisponiblesFragment fragment = new ViajesDisponiblesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ViajesDisponiblesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_viajes_disponibles, container, false);

        LoaderManager lm = getActivity().getSupportLoaderManager();
        try {
            num = savedInstanceState.getInt("num");

            num++;

        }catch (Exception e){}
        lm.initLoader(num, null, this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("num",num);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SuperLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dat) {

        hash = dat.getString(Provider.HASH_COLUMN);

        data = darViajes();

        ListView listView = (ListView) rootView.findViewById(R.id.listViewViajesDisponibles);
        listView.setAdapter(new ViajesDisponiblesFragment.CustomAdapter());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class CustomAdapter extends BaseAdapter {

        public CustomAdapter(){

        }

        @Override
        public int getCount() {
            return data.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return data.getJSONObject(position);
            }catch (Exception e){
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view = inflater.inflate(R.layout.viajes_disponibles_cell, null);

            try {
                JSONObject bun = data.getJSONObject(position);

                TextView nombre = (TextView) view.findViewById(R.id.nombre_viajes_disponibles);
                nombre.setText(bun.getString("nombre"));
                TextView hora = (TextView) view.findViewById(R.id.hora_viajes_disponibles);
                hora.setText(bun.getString("hora"));
                TextView precio = (TextView) view.findViewById(R.id.precio_viajes_disponibles);
                precio.setText("$"+ bun.getString("precio"));
                TextView calif = (TextView) view.findViewById(R.id.numeroCalificacion_viajes_disponibles);
                calif.setText("("+bun.getString("numcal")+")");
                TextView desc = (TextView) view.findViewById(R.id.descripcion_viajes_disponibles);
                desc.setText(bun.getString("descripcion"));
                TextView sent = (TextView) view.findViewById(R.id.sentido_viajes_disponibles);
                sent.setText(bun.getString("sentido"));
                final Button solic = (Button) view.findViewById(R.id.btn_solicitar_viajes_disponibles);

                final int id = bun.getInt("id");

                if(!bun.getString("yahecha").equals("NOEXISTE")) {
                    solic.setText("Cupo Solicitado");
                    solic.setTextColor(getActivity().getResources().getColor(R.color.white));
                    solic.setBackgroundColor(getActivity().getResources().getColor(R.color.blue));
                }else{
                    solic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            crearOferta(id);

                            solic.setText("Cupo Solicitado");
                            solic.setTextColor(getActivity().getResources().getColor(R.color.white));
                            solic.setBackgroundColor(getActivity().getResources().getColor(R.color.blue));

                            ((BaseAdapter)((ListView) getActivity().findViewById(R.id.ListViewOfertasRealizadas)).getAdapter()).notifyDataSetChanged();
                        }
                    });
                }

                ImageView img = (ImageView) view.findViewById(R.id.img_calificacion_viajes_disponibles);

                double cali = bun.getDouble("calificacion");

                if(cali <2){
                    img.setImageResource(R.drawable.calif_meh);
                }else if(cali <3){
                    img.setImageResource(R.drawable.calif_norm);
                }else{
                    img.setImageResource(R.drawable.calif_bien);
                }


            }catch (Exception e){
                Toast.makeText(getActivity().getBaseContext(),"Error leyendo el json",Toast.LENGTH_SHORT).show();
            }



            return view;
        }
    }


    public JSONArray darViajes(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "darviajetodos")
                .appendQueryParameter("login", hash)
                .build().toString();

        JSONArray json = null;


        try {

            json = new JSONArray("[]");

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            JSONObject js = new JSONObject(res).getJSONArray("arreglo").getJSONObject(0);

            json = js.getJSONArray("resultados");


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public boolean crearOferta(int id){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "crearOferta")
                .appendQueryParameter("login", hash)
                .appendQueryParameter("idb",""+id)
                .appendQueryParameter("cupos","1")
                .appendQueryParameter("cel", getActivity().getSharedPreferences(LoginActivity.PREF, 0).getString("numero", ""))
                .build().toString();

        boolean ress = true;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

            ress = new JSONObject(res).getBoolean("error");

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return !ress;
    }
}

