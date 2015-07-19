package com.equinox.mariohernandez.portalviaje;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BuscarViaje2Activity extends ActionBarActivity {

    private String quick = "15";
    private boolean enFecha = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_viaje2);



        final Button btnFecha = (Button) findViewById(R.id.btn_fecha_buscarViaje2);
        final Button btnHora = (Button) findViewById(R.id.btn_hora_buscarViaje2);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker_buscarViaje2);
        final TimePicker timepicker = (TimePicker) findViewById(R.id.time_picker_buscarViaje2);
        final Button btnSeleccionar = (Button) findViewById(R.id.btn_seleccionar_buscarViaje2);

        final Button btn15 = (Button) findViewById(R.id.btn_15_buscarViaje2);
        final Button btn30 = (Button) findViewById(R.id.btn_30_buscarViaje2);
        final Button btn1hr = (Button) findViewById(R.id.btn_1hr_buscarViaje2);
        final Button btn3hr = (Button) findViewById(R.id.btn_3hr_buscarViaje2);

        btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enFecha){

               //    int dia =  datePicker.getDayOfMonth();
                 //   String strDia = dia ;

                    btnFecha.setText(datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth());
                    datePicker.setVisibility(View.INVISIBLE);

                }else{
                    btnHora.setText(timepicker.getCurrentHour()+":"+timepicker.getCurrentMinute());
                    timepicker.setVisibility(View.INVISIBLE);

                }
                btnSeleccionar.setVisibility(View.INVISIBLE);
            }
        });

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enFecha = true;
                quick = "N";
                btn15.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn30.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn1hr.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn3hr.setBackgroundColor(getResources().getColor(R.color.yellow));

                datePicker.setVisibility(View.VISIBLE);
                btnSeleccionar.setVisibility(View.VISIBLE);

            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enFecha = false;
                quick = "N";
                btn15.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn30.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn1hr.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn3hr.setBackgroundColor(getResources().getColor(R.color.yellow));

                timepicker.setVisibility(View.VISIBLE);
                btnSeleccionar.setVisibility(View.VISIBLE);

            }
        });

        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quick = "15";
                btn15.setBackgroundColor(getResources().getColor(R.color.orange));
                btn30.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn1hr.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn3hr.setBackgroundColor(getResources().getColor(R.color.yellow));

                btnFecha.setText("Fecha");
                btnHora.setText("Hora");
            }
        });

        btn30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quick = "30";
                btn15.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn30.setBackgroundColor(getResources().getColor(R.color.orange));
                btn1hr.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn3hr.setBackgroundColor(getResources().getColor(R.color.yellow));


                btnFecha.setText("Fecha");
                btnHora.setText("Hora");
            }
        });

        btn1hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quick = "1hr";
                btn15.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn30.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn1hr.setBackgroundColor(getResources().getColor(R.color.orange));
                btn3hr.setBackgroundColor(getResources().getColor(R.color.yellow));


                btnFecha.setText("Fecha");
                btnHora.setText("Hora");
            }
        });

        btn3hr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quick = "3hr";
                btn15.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn30.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn1hr.setBackgroundColor(getResources().getColor(R.color.yellow));
                btn3hr.setBackgroundColor(getResources().getColor(R.color.orange));


                btnFecha.setText("Fecha");
                btnHora.setText("Hora");
            }
        });

        // Boton buscar

        findViewById(R.id.btnContinuar_buscarViaje2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quick.equals("N")){
                    String fech = btnFecha.getText().toString();
                    if(!fech.equals("Fecha")){

                        String hor = btnHora.getText().toString();

                        if(!hor.equals("Hora")){

                            startActivity(new Intent(getBaseContext(),
                                    BuscarViaje3Activity.class).putExtra("info2",getIntent().getStringExtra("info")+"_"+fech+"_"+hor));
                        }else{
                            Toast.makeText(getBaseContext(),"No has escogido una hora",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getBaseContext(),"No has escogido una fecha",Toast.LENGTH_SHORT).show();
                    }
                }else{


                    Calendar hoy = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                    hoy.setTimeZone(TimeZone.getTimeZone("America/Bogota"));

                    hoy.add(Calendar.MINUTE,-15);
                    hoy.add(Calendar.HOUR,12);

                    if(quick.equals("15")){
                        hoy.add(Calendar.MINUTE,15);

                    }else if(quick.equals("30")){
                        hoy.add(Calendar.MINUTE,30);

                    }else if (quick.equals("1hr")){
                        hoy.add(Calendar.HOUR,1);

                    } else{

                        hoy.add(Calendar.HOUR,3);
                    }

                    String currentDate24Hrs = (String) DateFormat.format(
                            "yyyy-MM-dd_kk:mm", hoy.getTime());

                   // Toast.makeText(getBaseContext(),currentDate24Hrs,Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getBaseContext(),
                            BuscarViaje3Activity.class).putExtra("info2", getIntent().getStringExtra("info") + "_"+currentDate24Hrs));

                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscar_viaje2, menu);
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
}
