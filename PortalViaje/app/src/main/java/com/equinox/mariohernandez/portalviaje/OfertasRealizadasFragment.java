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


public class OfertasRealizadasFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private JSONArray data ;
    private LayoutInflater inflater;
    private String hash;
    private View rootView;
    private int num;

    public OfertasRealizadasFragment newInstance(int sectionNumber) {
        OfertasRealizadasFragment fragment = new OfertasRealizadasFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public OfertasRealizadasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ofertas_realizadas, container, false);

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

        data = darOfertas();

        ListView listView = (ListView) rootView.findViewById(R.id.ListViewOfertasRealizadas);
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
            data = darOfertas();
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

            View view = inflater.inflate(R.layout.ofertas_realizadas_cell, null);

            try {

                JSONObject bun = data.getJSONObject(position);

                TextView nombre = (TextView) view.findViewById(R.id.nombre_ofertas_realizadas);
                nombre.setText(bun.getString("nombre"));
                TextView hora = (TextView) view.findViewById(R.id.hora_ofertas_realizadas);
                hora.setText(bun.getString("hora"));
                TextView precio = (TextView) view.findViewById(R.id.precio_ofertas_realizadas);
                precio.setText("$"+bun.getString("precio"));
                TextView calif = (TextView) view.findViewById(R.id.numeroCalificacion_ofertas_realizadas);
                calif.setText("("+bun.getString("numcal")+")");
                TextView desc = (TextView) view.findViewById(R.id.descripcion_ofertas_realizadas);
                desc.setText(bun.getString("descripcion"));
                TextView ori = (TextView) view.findViewById(R.id.orientación_ofertas_realizadas);
                ori.setText(bun.getString("sentido"));

                TextView estado = (TextView) view.findViewById(R.id.estado_ofertas_realizadas);
                String estad = bun.getString("estado");
                estado.setText(estad);

                final int idOferta = bun.getInt("id");

                Button btnCan = (Button) view.findViewById(R.id.btn_cancelar_ofertas);

                if(estado.getText().toString().equals("PENDIENTE")){
                    estado.setTextColor(getResources().getColor(R.color.blue));

                    btnCan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            cancelarOferta(idOferta);
                            //    data = darOfertas();
                                notifyDataSetChanged();

                        }
                    });
                }else if (estado.getText().toString().equals("ACEPTADA")){
                    estado.setTextColor(getResources().getColor(R.color.green));

                    btnCan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            cancelarOferta(idOferta);
                            //    data = darOfertas();
                                notifyDataSetChanged();

                        }
                    });
                }else{

                    btnCan.setBackgroundColor(getResources().getColor(R.color.blue));
                    btnCan.setText("Reenviar Solicitud");

                    btnCan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            reenviarOferta(idOferta);
                             //   data = darOfertas();
                                notifyDataSetChanged();

                        }
                    });
                }

                ImageView img = (ImageView) view.findViewById(R.id.calif_ofertas_realizadas);

                double cali = bun.getDouble("calificacion");

                if(cali <2){
                    img.setImageResource(R.drawable.calif_meh);
                }else if(cali <3){
                    img.setImageResource(R.drawable.calif_norm);
                }else{
                    img.setImageResource(R.drawable.calif_bien);
                }

                TextView correo = (TextView) view.findViewById(R.id.correo_ofertas_realizadas);
                ImageView metod = (ImageView) view.findViewById(R.id.medio_ofertas_realizadas);

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

                }else{
                    if(estado.equals("ACEPTADA")){

                        correo.setText(medio);
                    }
                }


                // falta la imagen del método de comunicación, falta imagende calificación del usuario
            }catch (Exception e ){
                Toast.makeText(getActivity().getBaseContext(),"Error leyendo el JSON",Toast.LENGTH_SHORT).show();
            }


            return view;
        }
    }

    public JSONArray darOfertas(){


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "darOfertasPasajero")
                .appendQueryParameter("login", hash)
                .build().toString();

        JSONArray json = null;


        try {

            json = new JSONArray("[]");

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            JSONObject js = new JSONObject(res).getJSONArray("arreglo").getJSONObject(0);

            json = js.getJSONArray("ofertas");


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }
        return json;
    }

    public boolean cancelarOferta(int id){
        boolean full = false;


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "cancelarOfertaPasajero")
                .appendQueryParameter("id", "" + id)
                .appendQueryParameter("login", hash)
                .build().toString();




        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


             full =!( new JSONObject(res).getBoolean("error"));

        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }

        return full;
    }

    public boolean reenviarOferta(int id){
        boolean full = false;


        Uri.Builder builder = new Uri.Builder();
        String url = builder.scheme("http")
                .authority(GetTask.AUTHORITY)
                .appendPath(GetTask.PATH1)
                .appendPath(GetTask.PATH2P)
                .appendQueryParameter("param", "rehacerOfertaConId")
                .appendQueryParameter("id", "" + id)
                .appendQueryParameter("login", hash)
                .build().toString();


        try {

            Log.v("prueba", "Se envía el request : \n" + url, null);

            String res = new GetTask().execute(url).get();

            Log.v("prueba","Se recibe : \n"+ res , null);


            full =!( new JSONObject(res).getBoolean("error"));


        }catch (Exception e ){
            Log.v("prueba","No fué posible obtener la info del servidor",null);
        }

        return full;
    }

}
