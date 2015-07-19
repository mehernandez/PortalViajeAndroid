package com.equinox.mariohernandez.portalviaje;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class InicialActivityFragment extends Fragment {

    private String contacto;

    public static final String CELULAR = "celular";
    public static final String WHATSAPP = "whatsapp";
    public static final String MAIL = "mail";



    public InicialActivityFragment() {
        contacto = CELULAR;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicial, container, false);

       final EditText nombre = (EditText) view.findViewById(R.id.nombre_inicial);

       final EditText numero = (EditText) view.findViewById(R.id.celular_inicial);


        final SharedPreferences shared = getActivity().getSharedPreferences(LoginActivity.PREF,0);

        nombre.setText(shared.getString("nombre","") + " "+shared.getString("apellidos",""));
        //numero.setText(shared.getString("numero"));

        final CheckBox check1 = (CheckBox) view.findViewById(R.id.checkBox_inicial);
        final CheckBox check2 = (CheckBox) view.findViewById(R.id.checkBox_inicial2);
        final Button btnCelular = (Button) view.findViewById(R.id.btn_celular_inicial);
        final Button btnWhat = (Button) view.findViewById(R.id.btn_whatsapp_inicial);
        final Button btnMail = (Button) view.findViewById(R.id.btn_mail_inicial);
        Button btnContinuar = (Button) view.findViewById(R.id.btn_continuar_inicial);

        btnCelular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacto = CELULAR;

                btnCelular.setBackgroundColor(getResources().getColor(R.color.orange));
                btnMail.setBackgroundColor(getResources().getColor(R.color.white));
                btnWhat.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btnWhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacto = WHATSAPP;

                btnCelular.setBackgroundColor(getResources().getColor(R.color.white));
                btnMail.setBackgroundColor(getResources().getColor(R.color.white));
                btnWhat.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        });

        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacto = MAIL;

                btnCelular.setBackgroundColor(getResources().getColor(R.color.white));
                btnMail.setBackgroundColor(getResources().getColor(R.color.orange));
                btnWhat.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check2.isChecked()){

                    SharedPreferences.Editor editor = shared.edit();

                    editor.putString("nombre",nombre.getText().toString());
                    editor.putString("numero",numero.getText().toString());
                    editor.putString("contacto",contacto);
                    editor.putBoolean("mostrarNum",check1.isChecked());
                    editor.putBoolean("nuevo",false);

                    editor.commit();



                    getActivity().finish();
                }else{
                    Toast.makeText(getActivity().getBaseContext(),"Debe aceptar los términos y condiciones para continuar",Toast.LENGTH_SHORT).show();
                }
            }
        });





        return view ;
    }
/*
    public static String crearUrl(String  celular , boolean mostrarNum, ){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2);

        for (int i= 0 ; i < parametros.length; i ++){
            builder.appendQueryParameter(parametros[i],valores[i]);
        }

        return builder.build().toString();


    }
    */


}
