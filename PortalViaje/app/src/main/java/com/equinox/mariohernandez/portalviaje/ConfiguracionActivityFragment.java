package com.equinox.mariohernandez.portalviaje;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Set;


/**
 * A placeholder fragment containing a simple view.
 */
public class ConfiguracionActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean whatsapp;
    private boolean mail;
    private boolean celular;

    private View vista ;

    private String hash ;

    //
    private Bundle state;





    public ConfiguracionActivityFragment() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        state = savedInstanceState;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         vista =  inflater.inflate(R.layout.fragment_configuracion, container, false);

        getActivity().getSupportLoaderManager().initLoader(1,null,this);


        return vista;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("wa",whatsapp);
        outState.putBoolean("email",mail);
        outState.putBoolean("concel",celular);
        outState.putBoolean("moscel",((CheckBox)vista.findViewById(R.id.check_mostrarInfo_configuracion)).isChecked());
        outState.putBoolean("ofertas",((CheckBox)vista.findViewById(R.id.check_ofertaNueva_configuracion)).isChecked());
        outState.putBoolean("vcancelado",((CheckBox)vista.findViewById(R.id.check_viajeCancelado_configuracion)).isChecked());
        outState.putBoolean("ocancelado",((CheckBox)vista.findViewById(R.id.check_ofertaCancelada_configuracion)).isChecked());
        outState.putBoolean("cal",((CheckBox)vista.findViewById(R.id.check_calificacionPendiente_configuracion)).isChecked());
        outState.putBoolean("aceptado",((CheckBox)vista.findViewById(R.id.check_ofertaAceptada_configuracion)).isChecked());
        outState.putString("cel", ((TextView) vista.findViewById(R.id.numero_configuracion)).getText().toString());
        super.onSaveInstanceState(outState);
    }

    public JSONObject pedirInfo(){

        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param","getPreferenciasCel")
                .appendQueryParameter("login",hash).build().toString();

        JSONObject json = null;


        try {

            Log.v("prueba","Se envía el request : \n"+ url , null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            json = new JSONObject(res);

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }

        return json ;

    }

    public boolean enviarInfo(){

        Uri.Builder builder = new Uri.Builder();


        String numero = ((EditText)vista.findViewById(R.id.numero_configuracion)).getText().toString();

        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2C)
                .appendQueryParameter("param", "cambiarPreferenciasCel")
                .appendQueryParameter("login", hash)
                .appendQueryParameter("cel", numero)
                .appendQueryParameter("wa", "" + whatsapp)
                .appendQueryParameter("concel", "" + celular)
                .appendQueryParameter("conemail", "" + mail)
                .appendQueryParameter("moscel", "" + ((CheckBox) vista.findViewById(R.id.check_mostrarInfo_configuracion)).isChecked())
                .appendQueryParameter("ofertas", ""+((CheckBox)vista.findViewById(R.id.check_ofertaNueva_configuracion)).isChecked())
                .appendQueryParameter("vcancelado", ""+((CheckBox)vista.findViewById(R.id.check_viajeCancelado_configuracion)).isChecked())
                .appendQueryParameter("ocancelado", ""+((CheckBox)vista.findViewById(R.id.check_ofertaCancelada_configuracion)).isChecked())
                .appendQueryParameter("aceptado", ""+((CheckBox)vista.findViewById(R.id.check_ofertaAceptada_configuracion)).isChecked())
                .appendQueryParameter("cal", ""+((CheckBox)vista.findViewById(R.id.check_calificacionPendiente_configuracion)).isChecked())
                        .build().toString();

        JSONObject json = null;


        try {

            Log.v("prueba","Se envía el request : \n"+ url , null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


          //  Toast.makeText(getActivity().getBaseContext(),res,Toast.LENGTH_LONG).show();
          //  json = new JSONObject(res);
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(LoginActivity.PREF,0).edit();
            editor.putString("numero",numero);
            editor.commit();

            if(getActivity().findViewById(R.id.fragment_configuracion) == null) {

                getActivity().finish();
            }

            Toast.makeText(getActivity().getBaseContext(),"Su información ha sido guardada exitosamente !",Toast.LENGTH_LONG).show();

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SuperLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        hash = data.getString(Provider.HASH_COLUMN);


        JSONObject json ;
        if(state == null) {
            json = pedirInfo();
        }else {
            json = new JSONObject();
            Set<String> keys = state.keySet();
            for (String key : keys) {
                try {

                    json.put(key, JSONObject.wrap(state.get(key)));
                } catch (Exception e) {

                }
            }
        }


        Button btnCelular = (Button) vista.findViewById(R.id.btn_celular_configuracion);

        Button btnWhat = (Button) vista.findViewById(R.id.btn_whatsapp_configuracion);

        Button btnMail = (Button) vista.findViewById(R.id.btn_mail_configuracion);



        try {

            //Botones
            if (json.getBoolean("concel")) {

                btnCelular.setBackgroundColor(getResources().getColor(R.color.orange));

                celular = true;
                mail = false;
                whatsapp = false;
            }else if(json.getBoolean("wa")){


                btnWhat.setBackgroundColor(getResources().getColor(R.color.orange));

                celular = false;
                mail = false;
                whatsapp = true;
            }else{

                btnMail.setBackgroundColor(getResources().getColor(R.color.orange));
                celular = false;
                mail = true;
                whatsapp = false;
            }
            //checkBox

            ((CheckBox) vista.findViewById(R.id.check_mostrarInfo_configuracion)).setChecked(json.getBoolean("moscel"));
            ((CheckBox) vista.findViewById(R.id.check_ofertaNueva_configuracion)).setChecked(json.getBoolean("ofertas"));
            ((CheckBox) vista.findViewById(R.id.check_viajeCancelado_configuracion)).setChecked(json.getBoolean("vcancelado"));
            ((CheckBox) vista.findViewById(R.id.check_ofertaCancelada_configuracion)).setChecked(json.getBoolean("ocancelado"));
            ((CheckBox) vista.findViewById(R.id.check_calificacionPendiente_configuracion)).setChecked(json.getBoolean("cal"));
            ((CheckBox) vista.findViewById(R.id.check_ofertaAceptada_configuracion)).setChecked(json.getBoolean("aceptado"));


            //numero

            ((EditText) vista.findViewById(R.id.numero_configuracion)).setText(json.getString("cel"));

        }catch (Exception e){
            Log.v("prueba","Error leyendo el json");
        }



        // Botones

        btnCelular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celular = true;
                mail = false;
                whatsapp = false;

                ((Button) vista.findViewById(R.id.btn_celular_configuracion)).setBackgroundColor(getResources().getColor(R.color.orange));

                ((Button) vista.findViewById(R.id.btn_whatsapp_configuracion)).setBackgroundColor(getResources().getColor(R.color.white));

                ((Button) vista.findViewById(R.id.btn_mail_configuracion)).setBackgroundColor(getResources().getColor(R.color.white));

            }
        });

        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celular = false;
                mail = true;
                whatsapp = false;

                ((Button) vista.findViewById(R.id.btn_celular_configuracion)).setBackgroundColor(getResources().getColor(R.color.white));

                ((Button) vista.findViewById(R.id.btn_whatsapp_configuracion)).setBackgroundColor(getResources().getColor(R.color.white));

                ((Button) vista.findViewById(R.id.btn_mail_configuracion)).setBackgroundColor(getResources().getColor(R.color.orange));
            }
        });

        btnWhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celular = false;
                mail = false;
                whatsapp = true;

                ((Button) vista.findViewById(R.id.btn_celular_configuracion)).setBackgroundColor(getResources().getColor(R.color.white));

                ((Button) vista.findViewById(R.id.btn_whatsapp_configuracion)).setBackgroundColor(getResources().getColor(R.color.orange));

                ((Button) vista.findViewById(R.id.btn_mail_configuracion)).setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        ((Button) vista.findViewById(R.id.btn_guardar_configuracion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarInfo();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
