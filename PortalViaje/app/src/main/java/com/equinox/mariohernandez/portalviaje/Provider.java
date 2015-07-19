package com.equinox.mariohernandez.portalviaje;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by mariohernandez on 7/07/15.
 */
public class Provider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.equinox.mariohernandez.portalviaje/content");

    private SQLiteDatabase contentDB;

    private static final String TAG = "ContentProvider";
    private static final String DB_NAME = "content.db";
    private static final int DB_VERSION = 1;
    private static final String CONTENT_TABLE = "content";

    public static final String KEY_ID = "_id";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_APELLIDOS = "apellidos";
    public static final String KEY_HASH = "hash";
    public static final String KEY_EMAIL = "email";
   // public static final String KEY_NUEVO = "nuevo";

    public static final int NOMBRE_COLUMN = 1;
    public static final int APELLIDOS_COLUMN = 2;
    public static final int HASH_COLUMN = 3;
    public static final int EMAIL_COLUMN = 4;
   // public static final int NUEVO_COLUMN = 5;

    public static final int ALL_RECORDS = 1;
    public static final int ONE_RECORD = 2;

    private static final UriMatcher uriMatcher;

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.equinox.mariohernandez.portalviaje","content",ALL_RECORDS);
        uriMatcher.addURI("com.equinox.mariohernandez.portalviaje","content/#",ONE_RECORD);


    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context,DB_NAME,null,DB_VERSION);
        contentDB = dbHelper.getWritableDatabase();

        return (contentDB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CONTENT_TABLE);

        switch (uriMatcher.match(uri)){
            case ONE_RECORD: {
                qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));

                break;
            }
            default: break;
        }

        String orderBy;

        if(TextUtils.isEmpty(sortOrder)){

            orderBy =KEY_ID;

        }else{
            orderBy = sortOrder;
        }

        Cursor c = qb.query(contentDB,projection,selection,selectionArgs,null,null,orderBy);
        c.setNotificationUri(getContext().getContentResolver(),uri);


        return c;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)){

            case ALL_RECORDS:
                return "vnd.android.cursor.dir/.vnd.equinox.mariohernandez.portalviaje";
            case ONE_RECORD:
                return "vnd.android.cursor.item/.vnd.equinox.mariohernandez.portalviaje";
            default: throw new IllegalArgumentException("Unsuported URI: "+ uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = contentDB.insert(CONTENT_TABLE,"content",values);

        if(rowID > 0){
            Uri uri2 = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri,null);
            return uri2;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count;

        switch (uriMatcher.match(uri)){
            case ALL_RECORDS:
                count = contentDB.delete(CONTENT_TABLE,selection,selectionArgs);
                break;
            case ONE_RECORD:
                String segment = uri.getPathSegments().get(1);
                count = contentDB.delete(CONTENT_TABLE,
                        KEY_ID + "=" + segment
                + (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection +")" : ""),selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsuported URI: "+ uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int count ;


        switch (uriMatcher.match(uri)){
            case ALL_RECORDS:
                count = contentDB.update(CONTENT_TABLE,values,selection,selectionArgs);
                break;
            case ONE_RECORD:

                String segment = uri.getPathSegments().get(1);
                count = contentDB.update(CONTENT_TABLE,values,
                        KEY_ID + "=" + segment +
                                (!TextUtils.isEmpty(selection) ? " AND ("+selection+")" : ""),selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsuported URI: "+ uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }

    private static class DBHelper extends SQLiteOpenHelper{

        private static final String DB_CREATE_TABLE =
                "create table " + CONTENT_TABLE + " (" +
                        KEY_ID + " integer primary key autoincrement, "+
                        KEY_NOMBRE + " TEXT, "+
                        KEY_APELLIDOS + " TEXT, "+
                        KEY_HASH + " TEXT, "+
                        KEY_EMAIL + " TEXT) ;";



        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DB_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.v(TAG,"Upgrading database from version "+ oldVersion + " to "+ newVersion +
                    " which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS "+ CONTENT_TABLE);
            onCreate(db);
        }
    }
}
