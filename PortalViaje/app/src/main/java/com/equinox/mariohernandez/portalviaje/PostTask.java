package com.equinox.mariohernandez.portalviaje;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mariohernandez on 7/07/15.
 */
public class PostTask extends AsyncTask<String, String, String> {

    public static final String RUTA = "https://shrouded-mountain-9044.herokuapp.com/conversacion";
    //public static final String RUTA2 = "https://shrouded-mountain-9044.herokuapp.com/conversacion";

    private OutputStream os;


    @Override
    protected String doInBackground(String... uri) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        String responseString = null;

        try {

            URL url = new URL(uri[0]);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            urlConnection.connect();

            //setup send
             os = new BufferedOutputStream(urlConnection.getOutputStream());
            os.write(uri[1].getBytes());
            //clean up
            os.flush();

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
                    os.close();
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


}
