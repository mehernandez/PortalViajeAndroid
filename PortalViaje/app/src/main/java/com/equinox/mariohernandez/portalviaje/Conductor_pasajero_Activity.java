package com.equinox.mariohernandez.portalviaje;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class Conductor_pasajero_Activity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ImageButton btnConductor;

    private GoogleCloudMessaging gcm;

    private String registrationId ;

    public static final String PROJECT_NUMBER = "531007793399";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor_pasajero_);

        btnConductor = (ImageButton)findViewById(R.id.btnConductor);

        btnConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegistrationId();
            }
        });

        findViewById(R.id.btnPasajero).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getBaseContext() , PasajeroActivity.class));
            }
        });

        SharedPreferences shared = getSharedPreferences(LoginActivity.PREF,0);

        if(shared.getBoolean("nuevo",false)){
            startActivity(new Intent(getBaseContext() , PasajeroActivity.class));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_conductor_pasajero_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Intent intent = new Intent(this.getBaseContext(), ConfiguracionActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_calificaciones){
            Intent intent = new Intent(this.getBaseContext(), CalificacionesActivity.class);
            startActivity(intent);
            return true;
        }

        /* else if(id == R.id.action_chat){
            //TODO hay que cambiar el id del user


         //   getSupportLoaderManager().initLoader(1,null,this);

             SharedPreferences shared = getSharedPreferences(LoginActivity.PREF,0);

            registrarUsuario(shared.getString("nombre",""),shared.getString("apellidos",""),
                    shared.getString("hash",""),shared.getString("email",""));

           Cursor c = darUsuario();

            Toast.makeText(getBaseContext(),c.getString(Provider.NOMBRE_COLUMN),Toast.LENGTH_LONG).show();


            Intent intent = new Intent(this.getBaseContext(), ChatActivity.class).putExtra("user","1");
             startActivity(intent);
            return true;
        }  */

        return super.onOptionsItemSelected(item);
    }


    public void getRegistrationId(){

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                try{
                    if (gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    registrationId = gcm.register(PROJECT_NUMBER);

                    msg = "Device registered with id : "+ registrationId;

                    Log.v("prueba",msg);

                }catch (Exception e){
                    msg = e.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
            }
        }.execute(null,null,null);
    }

    public void registrarUsuario(String nombre , String apellidos , String hash , String email){

        ContentResolver cr = getContentResolver();

        ContentValues values = new ContentValues();

        values.put(Provider.KEY_NOMBRE,nombre);
        values.put(Provider.KEY_APELLIDOS,apellidos);
        values.put(Provider.KEY_HASH,hash);
        values.put(Provider.KEY_EMAIL,email);

        cr.insert(Provider.CONTENT_URI, values);

    }

    public Cursor darUsuario(){

        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(Provider.CONTENT_URI,null,null,null,null);

        if (c==null) return c;

        c.moveToFirst();

        return c;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SuperLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Toast.makeText(getBaseContext(),data.getString(Provider.NOMBRE_COLUMN),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
