package com.equinox.mariohernandez.portalviaje;

import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;


public class CalificacionesActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private JSONObject data ;
    private LayoutInflater inflater;
    private CustomAdapter customAdapter;

    private String hash;
    private int boton = 1;
    private Bundle instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificaciones);

        instance = savedInstanceState;

        if(instance != null){
            boton = instance.getInt("boton");
        }

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        getSupportLoaderManager().initLoader(1,null,this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_calificaciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("boton",boton);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SuperLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dat) {

        hash = dat.getString(Provider.HASH_COLUMN);

        final Button btnCon = (Button) findViewById(R.id.btn_conductor_calificaciones);
        final Button btnPas = (Button) findViewById(R.id.btn_pasajero_calificaciones);
        final ImageView img = (ImageView) findViewById(R.id.calificacion_propia_calificaciones);
        final TextView mensaje = (TextView) findViewById(R.id.text_mensaje_calificaciones);

        btnPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boton = 1;

                btnPas.setBackgroundColor(getResources().getColor(R.color.yellow));
                btnCon.setBackgroundColor(getResources().getColor(R.color.orange));

                data = darCalificacionesPasajero();
                customAdapter.notifyDataSetChanged();

                mensaje.setText("Tu calificación como pasajero es");

                double cali = darCalPropiaPasajero();

                if(cali < 2){
                    img.setImageResource(R.drawable.calif_meh_full);
                }else if(cali < 3){
                    img.setImageResource(R.drawable.calif_norm_full);
                }else{
                    img.setImageResource(R.drawable.calif_bien_full);
                }
            }
        });

        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boton = 2;

                btnPas.setBackgroundColor(getResources().getColor(R.color.orange));
                btnCon.setBackgroundColor(getResources().getColor(R.color.yellow));

                data = darCalificacionesConductor();
                customAdapter.notifyDataSetChanged();

                mensaje.setText("Tu calificación como conductor es");

                double cali = darCalPropiaConductor();

                if(cali < 2){
                    img.setImageResource(R.drawable.calif_meh_full);
                }else if(cali < 3){
                    img.setImageResource(R.drawable.calif_norm_full);
                }else{
                    img.setImageResource(R.drawable.calif_bien_full);
                }
            }
        });


        ListView listView = (ListView) findViewById(R.id.listViewCalificaciones);
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        if(boton == 1) {
            btnPas.performClick();

            double cal = darCalPropiaPasajero();


            if (cal < 2) {
                img.setImageResource(R.drawable.calif_meh_full);
            } else if (cal < 3) {
                img.setImageResource(R.drawable.calif_norm_full);
            } else {
                img.setImageResource(R.drawable.calif_bien_full);
            }
        }else{
            btnCon.performClick();

            double cal = darCalPropiaConductor();


            if (cal < 2) {
                img.setImageResource(R.drawable.calif_meh_full);
            } else if (cal < 3) {
                img.setImageResource(R.drawable.calif_norm_full);
            } else {
                img.setImageResource(R.drawable.calif_bien_full);
            }
        }



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public class CustomAdapter extends BaseAdapter {

        public CustomAdapter(){

        }

        @Override
        public int getCount() {
            try {
                return data.getJSONArray("arreglo").length();
            }catch (Exception e){
                Toast.makeText(getBaseContext(),"error en el count",Toast.LENGTH_SHORT);
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            try{
                return data.getJSONArray("arreglo").get(position);
            }catch(Exception e){
                Toast.makeText(getBaseContext(),"error en el getItem",Toast.LENGTH_SHORT);
                return null;
            }
        }

        @Override
        public long getItemId(int position) {

           try {
               return ((JSONObject)data.getJSONArray("arreglo").get(position)).getInt("id");
           }catch(Exception e){
               Toast.makeText(getBaseContext(),"error en el getItemId",Toast.LENGTH_SHORT);
               return 0;
           }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View view = inflater.inflate(R.layout.calificaciones_cell, null);

            try {
                JSONObject bun = (JSONObject) data.getJSONArray("arreglo").get(position);

                final int id = bun.getInt("id");

                TextView nombre = (TextView) view.findViewById(R.id.nombre_calificaciones);
                nombre.setText(bun.getString("nombres"));
                TextView fecha = (TextView) view.findViewById(R.id.fecha_calificaciones);
                fecha.setText(bun.getString("fecha"));
                TextView hora = (TextView) view.findViewById(R.id.hora_calificaciones);
                hora.setText(bun.getString("hora"));




                view.findViewById(R.id.btn_noViaje_calificaciones).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       calificar(id, -1) ;

                        data = darCalificacionesPasajero();
                        notifyDataSetChanged();

                    }
                });



                view.findViewById(R.id.btn_mal_calificaciones).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calificar(id, 1);

                        data = darCalificacionesPasajero();

                        notifyDataSetChanged();
                    }
                });

                view.findViewById(R.id.btn_normal_calificaciones).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calificar(id, 2);

                        data = darCalificacionesPasajero();

                        notifyDataSetChanged();
                    }
                });

                view.findViewById(R.id.btn_bien_calificaciones).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calificar(id,3) ;

                        data = darCalificacionesPasajero();

                        notifyDataSetChanged();
                    }
                });

            }catch (Exception e){
                Toast.makeText(getBaseContext(), "Error creando celdas", Toast.LENGTH_SHORT).show();
            }

            return view;
        }
    }

    public JSONObject darCalificacionesPasajero(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "getCalificacionesPasajero")
                .appendQueryParameter("login", hash)
                .build().toString();

        JSONObject json = null;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

              json = new JSONObject(res);


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public JSONObject darCalificacionesConductor(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "getCalificacionesConductor")
                .appendQueryParameter("login", hash)
                .build().toString();

        JSONObject json = null;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

            json = new JSONObject(res);


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public boolean calificar(int id , int rating){
        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "calificar")
                .appendQueryParameter("login", hash)
                .appendQueryParameter("id",""+id)
                .appendQueryParameter("rating",""+rating)
                .build().toString();

        boolean propio = false;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

            propio = new JSONObject(res).getBoolean("error");


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return !propio;
    }

    public double darCalPropiaConductor(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "getCalActualConductor")
                .appendQueryParameter("login", hash)
                .build().toString();

        double json = -1;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

            json = new JSONObject(res).getDouble("promedio");


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public double darCalPropiaPasajero(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "getCalActualPasajero")
                .appendQueryParameter("login", hash)
                .build().toString();

        double json = -1;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);

            json = new JSONObject(res).getDouble("promedio");


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

}
