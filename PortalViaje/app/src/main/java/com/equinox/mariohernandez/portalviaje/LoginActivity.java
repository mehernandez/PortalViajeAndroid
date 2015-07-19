package com.equinox.mariohernandez.portalviaje;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {

    public static final String PREF = "preferences";

    private Bundle state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        state = savedInstanceState;

    /*    SharedPreferences shared = getSharedPreferences(PREF,0);

        if(!shared.getBoolean("nuevo",true)){

            SharedPreferences.Editor editor = shared.edit();
            editor.putString("numero",darNumero());
            editor.commit();

            startActivity(new Intent(getBaseContext(), Conductor_pasajero_Activity.class));
        }
        */

        final EditText user = (EditText) findViewById(R.id.usuario);

        final EditText pass = (EditText) findViewById(R.id.clave);

        if(state != null){
            user.setText(state.getString("usuario"));
            pass.setText(state.getString("clave"));
        }


        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = "";
                boolean error = true;


                try {
                    //result = new GetTask().execute("http://should-i.herokuapp.com/").get();

                    String ruta = hacerRutaLogin(user.getText().toString(), pass.getText().toString());

                    Log.v("prueba", "La ruta es  :  " + ruta);
                    // hace el http request
                    result = new GetTask().execute(ruta).get();

                    // se parsea el json
                    JSONObject jObject = new JSONObject(result);

                    error = jObject.getBoolean("error");


                    if (error) {
                        Toast.makeText(getBaseContext(), "No fue posible iniciar sesión", Toast.LENGTH_SHORT).show();
                    } else {


                        // se guardan los datos localmente
                        SharedPreferences shared = getSharedPreferences(PREF, 0);

                        SharedPreferences.Editor editor = shared.edit();

                        editor.putString("nombre", jObject.getString("nombre"));
                        editor.putString("hash", jObject.getString("hash"));
                        editor.putString("email", jObject.getString("email"));
                        editor.putString("apellidos", jObject.getString("apellidos"));
                        editor.putBoolean("nuevo", jObject.getBoolean("nuevo"));
                        editor.putString("numero",darNumero());  // se quita después

                        editor.commit();

                        //

                        eliminarUsuario();

                        registrarUsuario(shared.getString("nombre",""),shared.getString("apellidos",""),
                                shared.getString("hash",""),shared.getString("email",""));


                        Log.v("prueba", "La respuesta es  :  \n " + result);

                        //Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();


                        Intent intent = new Intent(v.getContext(), Conductor_pasajero_Activity.class);
                        startActivity(intent);
                    }


                } catch (Exception e) {
                    Log.e("error", "Ubo un problema en la url");
                }


            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("usuario",((EditText)findViewById(R.id.usuario)).getText().toString());
        outState.putString("clave",((EditText)findViewById(R.id.clave)).getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String hacerRutaLogin(String usuario , String clave){

        String [] arr = {GetTask.PARAM,"login","password"};
        String [] arr2 = {"loginUser",usuario , clave};
        return GetTask.crearUrl(arr,arr2);

    }

    public String darNumero(){

        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2)
                .appendQueryParameter("param","darCelularUsuario")
                .appendQueryParameter("login",getSharedPreferences(LoginActivity.PREF,0).getString("hash","")).build().toString();

        String full = null;


        try {

            Log.v("prueba","Se envía el request : \n"+ url , null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            full = new JSONObject(res).getString("cel");

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return full;
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

    public void eliminarUsuario(){

        ContentResolver cr = getContentResolver();

        String [] s = {};
        cr.delete(Provider.CONTENT_URI,"", s);


    }
}
