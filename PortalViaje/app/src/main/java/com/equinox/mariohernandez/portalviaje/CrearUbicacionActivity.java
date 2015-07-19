package com.equinox.mariohernandez.portalviaje;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

public class CrearUbicacionActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private LatLng coordinates = null;
    private String hash ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ubicacion);

        findViewById(R.id.btn_gps_crearUbicacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getBaseContext(), CrearUbicacionMapActivity.class), 0);
            }


        });

        findViewById(R.id.btn_buscar_crearUbicacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             String ubi = buscarUbicacion();

                if(ubi != null) {

                    startActivityForResult(new Intent(getBaseContext(), CrearUbicacionMapActivity.class).putExtra("loc", ubi), 0);

                }else{
                    Toast.makeText(getBaseContext(),"Lo sentimos , no pudimos encontrar la dirección",Toast.LENGTH_SHORT).show();
                }


            }


        });

        getSupportLoaderManager().initLoader(1,null,this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String res = data.getStringExtra("coord");


        if (res != null){

            String[] results = res.split("_");

            coordinates = new LatLng(Double.parseDouble(results[1]),Double.parseDouble(results[2]));

            if(results[0].equals("gps")){
                ((Button)findViewById(R.id.btn_gps_crearUbicacion)).setText("GPS OK!");
                ((Button)findViewById(R.id.btn_buscar_crearUbicacion)).setText("Buscar");

            }else{
                ((Button)findViewById(R.id.btn_gps_crearUbicacion)).setText("GPS");
                ((Button)findViewById(R.id.btn_buscar_crearUbicacion)).setText("Dirección OK!");

            }


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_crear_ubicacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String buscarUbicacion(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "getDir")
                .appendQueryParameter("direccion", ((EditText) findViewById(R.id.text_direccion_crearUbicacion)).getText().toString())
                .build().toString();

        String json = null;


        try {



            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            JSONObject js = new JSONObject(res).getJSONArray("candidates").getJSONObject(0).getJSONObject("location");

            json = js.getDouble("y")+"_"+js.getDouble("x");

        }catch (Exception e ){
            Log.v("prueba","No se encontró ninguna ubicación",null);
        }
        return json;
    }

    public boolean crearUbicación(String nom){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "agregarubicacion")
                .appendQueryParameter("login", hash)
                .appendQueryParameter("nombre", nom)
                .appendQueryParameter("lat",""+coordinates.latitude)
                .appendQueryParameter("lon",""+coordinates.longitude)
                .build().toString();

        boolean json = true;


        try {



            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            json = new JSONObject(res).getBoolean("error");


        }catch (Exception e ){
            Log.v("prueba","No se encontró ninguna ubicación",null);
        }
        return !json;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SuperLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        hash = data.getString(Provider.HASH_COLUMN);

        findViewById(R.id.btn_crear_crearUbicacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nom = ((EditText)findViewById(R.id.text_nombre_crearUbicacion)).getText().toString();

                if(coordinates != null ){

                    if(!nom.isEmpty()){

                        if(crearUbicación(nom)){
                            setResult(RESULT_OK,new Intent().putExtra("act","ubi"));
                            finish();
                        }else{
                            Toast.makeText(getBaseContext(), "No fué posible crear la ubicación", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(getBaseContext(), "No le haz asignado un nombre a la ubicación", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getBaseContext(), "No haz seleccionado las coordenadas de tu ubicación", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
