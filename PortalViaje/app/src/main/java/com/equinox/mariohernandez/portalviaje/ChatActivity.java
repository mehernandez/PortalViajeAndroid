package com.equinox.mariohernandez.portalviaje;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatActivity extends ActionBarActivity {




    private ListView listView;
    private String hash = "";

    private JSONArray data;
    private LayoutInflater inflater;
    private CustomAdapter customAdapter;

    private int conversationId = -1;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        hash = getSharedPreferences(LoginActivity.PREF,0).getString("hash","");


        final EditText text = (EditText) findViewById(R.id.text_chat);

        final Button btnSend = (Button) findViewById(R.id.btn_enviar_chat);

        final String user = getIntent().getStringExtra("user");


        conversationId = crearConversacion(user);

      //  conversationId = 3;

        if(conversationId > -1){

            data = darMensajesIniciales();

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSend.setText("...");
                    String mess = text.getText().toString();
                    boolean res = enviarMensaje(mess);
                    if(res){

                        try {
                            text.setText("");
                            JSONObject obj = new JSONObject();
                            obj.put("delivered", true);
                            obj.put("contenido", mess);
                            obj.put("idsender", "2");
                            obj.put("idconversacion", conversationId);
                            obj.put("id",0);
                            obj.put("hora",new JSONObject().put("$date",0));
                            //TODO debe ser hash el idsender


                           // String arrr = data.toString().substring(0, data.toString().length() - 1)+","+obj.toString()+"]";
                           // data= new JSONArray(arrr);
                            data.put(obj);

                            customAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            Toast.makeText(getBaseContext(), "No fué posible enviar el mensaje", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getBaseContext(), "No fué posible enviar el mensaje", Toast.LENGTH_SHORT).show();
                    }
                    btnSend.setText("Send");
                }
            });
        }else{
            finish();
            Toast.makeText(getBaseContext(), "No fué posible iniciar la conversación", Toast.LENGTH_SHORT).show();
        }

        listView = (ListView) findViewById(R.id.list_view_chat);
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONArray arr = darMensajesSinLeer();
                if(arr.length() > 0) {
                    try {
                        for (int i =0; i < arr.length(); i++){
                            data.put(arr.getJSONObject(i));
                        }

                        customAdapter.notifyDataSetChanged();
                    }catch (Exception e){
                        Toast.makeText(getBaseContext(),"Error incluyendo mensajes",Toast.LENGTH_SHORT).show();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);

     //   listView.smoothScrollToPosition(customAdapter.getCount());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    public class CustomAdapter extends BaseAdapter {

        public CustomAdapter(){

        }

        @Override
        public void notifyDataSetChanged() {
           // data = darMensajesIniciales();
            super.notifyDataSetChanged();
            listView.smoothScrollToPosition(getCount());
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

            View view = inflater.inflate(R.layout.chat_cell, null);

            try {
                JSONObject bun = data.getJSONObject(position);

                //TODO cambiar "2" a hash
                if(bun.getString("idsender").equals("2")) {
                    ((TextView) view.findViewById(R.id.nombre_chat)).setText("Tu");
                }else{
                    ((TextView) view.findViewById(R.id.nombre_chat)).setText("Persona");
                }

                if(bun.getBoolean("delivered")){
                    ((TextView) view.findViewById(R.id.estado_chat)).setText("Enviado");
                }

                ((TextView) view.findViewById(R.id.mensaje_chat)).setText(bun.getString("contenido"));

            }catch(Exception e){
                Toast.makeText(getBaseContext(), "Error leyendo el json", Toast.LENGTH_SHORT).show();
            }

            return view;
        }
    }


    public void register(View view) {
        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
        intent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        intent.putExtra("sender", "med.hernandez10@gmail.com");
        startService(intent);
    }

    public JSONArray darMensajesSinLeer(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("https")
                .authority(GetTask.AUTHORITY2)
                .appendPath(GetTask.PATH1_2)
                .appendPath(conversationId + "&2")
                .build().toString();

        //TODO deberia cambiar el &2 por un & + hash

        JSONArray json = null;


        try {

            json = new JSONArray("[]");

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            json = new JSONObject(res).getJSONArray("messages");
            //  Toast.makeText(getActivity().getBaseContext(),res,Toast.LENGTH_LONG).show();


            // Toast.makeText(getBaseContext(), "Su información ha sido guardada exitosamente !", Toast.LENGTH_LONG).show();

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public JSONArray darMensajesIniciales(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("https")
                .authority(GetTask.AUTHORITY2)
                .appendPath(GetTask.PATH1_2)
                .appendQueryParameter("idC", conversationId + "")
                .appendQueryParameter("contador", "-1")
                .build().toString();

        JSONArray json = null;


        try {

            json = new JSONArray("[]");

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            json = new JSONObject(res).getJSONArray("messages");
            //  Toast.makeText(getActivity().getBaseContext(),res,Toast.LENGTH_LONG).show();


            // Toast.makeText(getBaseContext(), "Su información ha sido guardada exitosamente !", Toast.LENGTH_LONG).show();

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public boolean enviarMensaje(String message){


       /* Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "crearOferta")
                .appendQueryParameter("login", getSharedPreferences(LoginActivity.PREF, 0).getString("hash", ""))
                .appendQueryParameter("idb", "" + "")
                .appendQueryParameter("cupos", "1")
                .appendQueryParameter("cel", getSharedPreferences(LoginActivity.PREF, 0).getString("numero", ""))
                .build().toString();
                */

        String url = PostTask.RUTA+"/"+conversationId;
        boolean ress = false;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            JSONObject obj = new JSONObject();
            obj.put("contenido",message);
            obj.put("idSender","2");
            //TODO asi deberia ser
            //obj.put("idSender",hash);

            String res = new PostTask().execute(url,obj.toString()).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            //  Toast.makeText(getActivity().getBaseContext(),res,Toast.LENGTH_LONG).show();
            if(new JSONObject(res).getInt("idMensaje") > -1){
                ress = true;
            }

            // Toast.makeText(getBaseContext(), "Su información ha sido guardada exitosamente !", Toast.LENGTH_LONG).show();

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return ress;
    }

    public int crearConversacion(String user){


       /* Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "crearOferta")
                .appendQueryParameter("login", getSharedPreferences(LoginActivity.PREF, 0).getString("hash", ""))
                .appendQueryParameter("idb", "" + "")
                .appendQueryParameter("cupos", "1")
                .appendQueryParameter("cel", getSharedPreferences(LoginActivity.PREF, 0).getString("numero", ""))
                .build().toString();
                */
        String url = PostTask.RUTA;

        int ress = -1;


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            JSONObject obj = new JSONObject();

            obj.put("id1","2");
            //TODO asi deberia ser
            //obj.put("id1",hash);
            obj.put("id2",user);

            String res = new PostTask().execute(url,obj.toString()).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            //  Toast.makeText(getActivity().getBaseContext(),res,Toast.LENGTH_LONG).show();
            ress = new JSONObject(res).getInt("idC");

            // Toast.makeText(getBaseContext(), "Su información ha sido guardada exitosamente !", Toast.LENGTH_LONG).show();

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return ress;
    }
}
