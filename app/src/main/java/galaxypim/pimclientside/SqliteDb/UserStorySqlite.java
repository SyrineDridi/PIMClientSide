package galaxypim.pimclientside.SqliteDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import galaxypim.pimclientside.Entities.UserStory;

/**
 * Created by Syrine on 22/10/2016.
 */

public class UserStorySqlite  extends SQLiteOpenHelper{

    public static final String TABLE_EVENTS = "userstories";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_AVANCEMENT = "avancement";
    public static final String COLUMN_ESTIMATION = "estimation";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_NOMPROJET = "nomprojet";



    private static final String DATABASE_NAME = "userstories.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_EVENTS + "(" + COLUMN_ID
            + "INTEGER PRIMARY KEY ," + COLUMN_NOM
            + "text not null ," + COLUMN_DESC
            + "text not null ,"+COLUMN_AVANCEMENT
            + "text not null ,"+COLUMN_ESTIMATION
            + "text not null ,"+COLUMN_NOMPROJET
            + " text not null"+
            ")";

    public UserStorySqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UserStorySqlite.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    // Adding new contact
    public void addContact(UserStory contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, contact.getNom()); // Contact Name
        values.put(COLUMN_DESC, contact.getDesc());
        values.put(COLUMN_ID, contact.getId());
        values.put(COLUMN_NOMPROJET, contact.getNom_projet());
        values.put(COLUMN_AVANCEMENT, contact.getAvancement());
        values.put(COLUMN_PRIORITY, contact.getPriority());
        values.put(COLUMN_ESTIMATION, contact.getEstimation());
        // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
    }
}
