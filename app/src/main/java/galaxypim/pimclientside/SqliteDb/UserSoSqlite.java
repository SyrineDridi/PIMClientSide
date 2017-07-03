package galaxypim.pimclientside.SqliteDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import galaxypim.pimclientside.Entities.UserStory;

/**
 * Created by Syrine on 18/05/2017.
 */

public class UserSoSqlite  extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "userManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "userstories";

    // Contacts Table Columns names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOM = "nom";
    private static final String COLUMN_DESC = "description";
    private static final String COLUMN_NOMPROJET = "nomprojet";
    private static final String COLUMN_AVANCEMENT = "avancement";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_ESTIMATION = "estimation";

    // Contact Phone Number

    public UserSoSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NOM + " TEXT,"
                + COLUMN_DESC + " TEXT" +COLUMN_NOMPROJET + " TEXT" + COLUMN_AVANCEMENT + " TEXT" +COLUMN_PRIORITY + " TEXT" +COLUMN_ESTIMATION + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

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
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }
    public ArrayList<UserStory> getAllContacts() {
        ArrayList<UserStory> contactList = new ArrayList<UserStory>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserStory contact = new UserStory();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNom(cursor.getString(1));
                contact.setDesc(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

}