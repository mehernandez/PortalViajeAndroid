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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class UbicacionesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private JSONArray data ;
    private LayoutInflater inflater;
    private String hash;
    private View rootView;
    private int num = 1;

    public UbicacionesFragment newInstance(int sectionNumber) {
        UbicacionesFragment fragment = new UbicacionesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UbicacionesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ubicaciones, container, false);

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

        data = darUbicaciones();

        ListView listView = (ListView) rootView.findViewById(R.id.listViewUbicaciones);
        listView.setAdapter(new CustomAdapter());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class CustomAdapter extends BaseAdapter {

        public CustomAdapter(){

        }

        @Override
        public void notifyDataSetChanged() {
            data = darUbicaciones();
            super.notifyDataSetChanged();
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

            View view = inflater.inflate(R.layout.ubicaciones_cell,null);

            try {
                JSONObject bun = data.getJSONObject(position);

                TextView nombre = (TextView) view.findViewById(R.id.titulo_ubicaciones);
                nombre.setText(bun.getString("nombre"));

                final int id = bun.getInt("id");

                view.findViewById(R.id.btn_borrar_ubicaciones).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        borrarUbicacion(id);

                        notifyDataSetChanged();
                    }
                });
            }catch (Exception e){
                Toast.makeText(getActivity().getBaseContext(), "Error leyendo el Json", Toast.LENGTH_SHORT);
            }


            return view;
        }
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

    public boolean borrarUbicacion(int id){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "borrarUbicacionConId")
                .appendQueryParameter("login", hash)
                .appendQueryParameter("id",""+id)
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
