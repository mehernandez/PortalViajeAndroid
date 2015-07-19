package com.equinox.mariohernandez.portalviaje;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mariohernandez on 24/06/15.
 */
class GetTask extends AsyncTask<String, String, String> {

    public static final String SCHEME = "http";
    public static final String SCHEME2 = "https";

    public static final String AUTHORITY = "157.253.242.26";
    public static final String AUTHORITY2 = "shrouded-mountain-9044.herokuapp.com";

    public static final String PATH1 = "Portalviaje";
    public static final String PATH1_2 = "mensaje";

    public static final String PATH2 = "mobile";
    public static final String PATH2P = "mobilep";
    public static final String PATH2C = "mobilec";

    public final String URL = "http://157.253.242.26/Portalviaje/mobile/?";
    public final String URLP = "http://157.253.242.26/Portalviaje/mobilep/?";
    public final String URLC = "http://157.253.242.26/Portalviaje/mobilec/?";
    public static final String PARAM = "param";
    public final String temp = "http://157.253.236.252:6080/arcgis/rest/services/geonetwork/Network_Bogota/MapServer?";

    @Override
    protected String doInBackground(String... uri) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        String responseString = null;

        try {

            URL url = new URL(uri[0]);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            responseString = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }


        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }

    public static String crearUrl(String [] parametros , String [] valores){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(AUTHORITY)
                .appendPath(PATH1)
                .appendPath(PATH2);

        for (int i= 0 ; i < parametros.length; i ++){
            builder.appendQueryParameter(parametros[i],valores[i]);
        }

        return builder.build().toString();


    }
}