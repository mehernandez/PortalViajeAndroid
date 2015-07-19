package com.equinox.mariohernandez.portalviaje;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class PasajeroActivity extends ActionBarActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    private Menu men;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero);




        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));

            // se puede llamar a setIcon()
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_pasajero, menu);
        men = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        /* else if(id == R.id.action_crear_ubicacion){
            startActivityForResult(new Intent(this.getBaseContext(), CrearUbicacionActivity.class), 0);
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode == RESULT_OK) {
                String res = data.getStringExtra("act");

                if (res.equals("ubi")) {
                    ((BaseAdapter) ((ListView) findViewById(R.id.listViewUbicaciones)).getAdapter()).notifyDataSetChanged();
                }
            }
        }catch (Exception e){
            // NO hay que actualizar
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        mViewPager.setCurrentItem(tab.getPosition());
/*        if(tab.getPosition() == 1){
            men.findItem(R.id.action_crear_ubicacion).setVisible(true);
        }else{
            men.findItem(R.id.action_crear_ubicacion).setVisible(false);
        }*/
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {



            switch (position) {
                case 0:

                    return new ViajesDisponiblesFragment();
                case 1:
                    return new OfertasRealizadasFragment();

            /*    case 2:
                    return new BuscarViajeFragment();
                case 3:
                    return new UbicacionesFragment();
                    */
            }
            return null;
        }

        @Override
        public int getCount() {

            return 2;

            //es 4
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Viajes Disponibles";
                case 1:
                    return "Ofertas";

             /*   case 2:
                    return "Buscar Viaje";
                case 3:
                    return "Ubicaciones";
                    */
            }
            return null;
        }
    }


}