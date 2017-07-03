package galaxypim.pimclientside.Datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import galaxypim.pimclientside.Entities.UserStory;
import galaxypim.pimclientside.SqliteDb.UserStorySqlite;

/**
 * Created by Syrine on 18/05/2017.
 */

public class UserStoryDataSource {

    private SQLiteDatabase database;
    private UserStorySqlite userStorySqlite;
    private String[] allColumns = {userStorySqlite.COLUMN_ID,
            userStorySqlite.COLUMN_NOM ,userStorySqlite.COLUMN_DESC,userStorySqlite.COLUMN_AVANCEMENT ,
            userStorySqlite.COLUMN_ESTIMATION,userStorySqlite.COLUMN_PRIORITY , userStorySqlite.COLUMN_NOMPROJET

    };

    public UserStoryDataSource(Context context) {
        userStorySqlite = new UserStorySqlite(context);
    }

    public void open() throws SQLException {
        database = userStorySqlite.getWritableDatabase();
    }

    public void close() {
        userStorySqlite.close();
    }

    private UserStory cursorToEvent(Cursor cursor) {
        UserStory user_story = new UserStory();
        user_story.setId(cursor.getInt(0));
        user_story.setNom(cursor.getString(1));
        user_story.setDesc(cursor.getString(2));
        user_story.setAvancement(cursor.getString(3));
        user_story.setEstimation(cursor.getInt(4));
        user_story.setPriority(cursor.getString(5));
        user_story.setNom_projet(cursor.getString(6));

        return user_story;
    }

    public UserStory showEvent(int id) {
        SQLiteDatabase db = userStorySqlite.getReadableDatabase();
        Cursor cursor = db.query(userStorySqlite.TABLE_EVENTS, allColumns, userStorySqlite.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


        UserStory userStory = cursorToEvent(cursor);
        cursor.close();
        return userStory;
    }

    public UserStory createEvent(int id,String nom ,String  desc , String avancement , int estimation ,
     String nom_projet) {
        ContentValues values = new ContentValues();
        values.put(userStorySqlite.COLUMN_ID, id);

        values.put(userStorySqlite.COLUMN_NOM, nom);
        values.put(userStorySqlite.COLUMN_DESC, desc);
        values.put(userStorySqlite.COLUMN_AVANCEMENT, avancement);
        values.put(userStorySqlite.COLUMN_ESTIMATION, estimation);

        values.put(userStorySqlite.COLUMN_NOMPROJET, nom_projet);
        long insertId = database.insert(userStorySqlite.TABLE_EVENTS, null,
                values);
        Cursor cursor = database.query(userStorySqlite.TABLE_EVENTS, allColumns, userStorySqlite.COLUMN_ID + " = " + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        UserStory userStory = cursorToEvent(cursor);
        cursor.close();
        return userStory;
    }

    public void deleteEvent(UserStory userStory) {
        int id = userStory.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(userStorySqlite.TABLE_EVENTS, userStorySqlite.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<UserStory> getAllUserStories() {
        ArrayList<UserStory> userstories = new ArrayList<>();

        Cursor cursor = database.query(userStorySqlite.TABLE_EVENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserStory userStory = cursorToEvent(cursor);
            userstories.add(userStory);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return userstories;
    }

    public int getEventCount() {
        String countQuery = "SELECT  * FROM " + userStorySqlite.TABLE_EVENTS;
        SQLiteDatabase db = userStorySqlite.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    public int updateUserStory(UserStory event) {
        SQLiteDatabase db = userStorySqlite.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(userStorySqlite.TABLE_EVENTS, event.getAvancement());


        // updating row
        return db.update(userStorySqlite.TABLE_EVENTS, values, null,
                new String[]{String.valueOf(event.getId())});
    }



    public boolean updatedetails(int rowId,String etat)
    {
        SQLiteDatabase db = userStorySqlite.getWritableDatabase();
        ContentValues args = new ContentValues();

        args.put(userStorySqlite.COLUMN_AVANCEMENT, etat);

        return   db.update(userStorySqlite.TABLE_EVENTS, args, userStorySqlite.COLUMN_ID + "=" + rowId, null) > 0;
    }


}
