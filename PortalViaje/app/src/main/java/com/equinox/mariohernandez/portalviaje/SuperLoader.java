package com.equinox.mariohernandez.portalviaje;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by mariohernandez on 12/07/15.
 */
public class SuperLoader extends CursorLoader{

    private Activity activity;

    public SuperLoader(Activity act) {
        super(act.getBaseContext());
        activity = act;
    }

    @Override
    public Cursor loadInBackground() {
        return darUsuario();
    }

    public Cursor darUsuario(){


        ContentResolver cr = activity.getContentResolver();
        Cursor c = cr.query(Provider.CONTENT_URI,null,null,null,null);

        if (c==null) return c;

        c.moveToFirst();

        return c;
    }
}
