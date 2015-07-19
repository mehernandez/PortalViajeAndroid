package com.equinox.mariohernandez.portalviaje;

import android.database.Cursor;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONObject;

public class BuscarViaje3Activity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private JSONArray data ;
    private LayoutInflater inflater;
    private String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_viaje3);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        getSupportLoaderManager().initLoader(1,null,this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_buscar_viaje3, menu);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dat) {

        hash = dat.getString(Provider.HASH_COLUMN);

        String[] info = getIntent().getStringExtra("info2").split("_");

        data = darViajes(info[0],info[1],info[2],info[3],info[4]);

        ListView listView = (ListView) findViewById(R.id.ListViewBuscarViaje);
        listView.setAdapter(new CustomAdapter());
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

            View view = inflater.inflate(R.layout.buscar_viaje_cell, null);

            try {
                JSONObject bun = data.getJSONObject(position);

                TextView nombre = (TextView) view.findViewById(R.id.nombre_buscar_viaje);
                nombre.setText(bun.getString("nombre"));
                TextView hora = (TextView) view.findViewById(R.id.hora_buscar_viaje);
                hora.setText(bun.getString("hora"));
                TextView precio = (TextView) view.findViewById(R.id.precio_buscar_viaje);
                precio.setText(bun.getString("precio"));
                TextView calif = (TextView) view.findViewById(R.id.calificacion_buscar_viaje);
                calif.setText("("+bun.getInt("numcal")+")");
                TextView desc = (TextView) view.findViewById(R.id.descripcion_buscar_viaje);
                desc.setText(bun.getString("descripcion"));
                TextView ori = (TextView) view.findViewById(R.id.orientación_buscar_viaje);
                ori.setText(bun.getString("sentido"));
                TextView cont = (TextView) view.findViewById(R.id.contacto_buscar_viaje3);
                cont.setText(bun.getString("cel"));

                final Button btnSolicitar = (Button) view.findViewById(R.id.btn_solicitar_buscarViaje3);

                final int id = bun.getInt("id");

                if(bun.getBoolean("yahecha")){

                    btnSolicitar.setText("Solicitud Realizada");
                    btnSolicitar.setBackgroundColor(getResources().getColor(R.color.blue));
                }else{
                    btnSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            crearOferta(id);
                            btnSolicitar.setText("Solicitud Realizada");
                            btnSolicitar.setBackgroundColor(getResources().getColor(R.color.blue));

                        }
                    });
                }

                ImageView img = (ImageView) view.findViewById(R.id.calif_buscar_viaje3);

                double cali = bun.getDouble("calificacion");

                if(cali <2){
                    img.setImageResource(R.drawable.calif_meh);
                }else if(cali <3){
                    img.setImageResource(R.drawable.calif_norm);
                }else{
                    img.setImageResource(R.drawable.calif_bien);
                }

                TextView correo = (TextView) view.findViewById(R.id.contacto_buscar_viaje3);
                ImageView metod = (ImageView) view.findViewById(R.id.medio_buscar_viaje3);

                String medio = bun.getString("com");

                if(medio.equals("email")){
                    medio = bun.getString("email");
                    metod.setImageResource(R.drawable.mail);
                }else{
                    if(medio.equals("wa")){

                        metod.setImageResource(R.drawable.wa);
                    }else{

                        metod.setImageResource(R.drawable.cel);
                    }
                    medio = bun.getString("cel");
                }


                if(bun.getBoolean("moscel")){

                    correo.setText(medio);

                }



            }catch(Exception e){
                Toast.makeText(getBaseContext(), "Error leyendo el json", Toast.LENGTH_SHORT).show();
            }

            return view;
        }
    }

    public JSONArray darViajes(String sentido,String ubicacion,String cupos,String fecha, String hora){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "buscarviaje")
                .appendQueryParameter("login",hash)
                .appendQueryParameter("sentido",sentido)
                .appendQueryParameter("ubicación",ubicacion)
                .appendQueryParameter("cupos",cupos)
                .appendQueryParameter("fecha",fecha)
                .appendQueryParameter("hora",hora)
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
                .appendQueryParameter("idb", "" + id)
                .appendQueryParameter("cupos", "1")
                .appendQueryParameter("cel", getSharedPreferences(LoginActivity.PREF, 0).getString("numero", ""))
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
