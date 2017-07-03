package galaxypim.pimclientside;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by maher on 13/02/2017.
 */
public class Files {
    private File directory= null;
    private String serverAdress="";
    HashSet<String> fileset ;
    File files  [] ;
    Activity activity;
    private Intent intent;
    public Files(String serverAdress, Activity activity){
        this.activity=activity;
        directory = new File(Environment.getExternalStorageDirectory()+"/shared");
        if(!directory.exists())
            directory.mkdir();

    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    private void connect() {
      //  ConnectivityManager ConnectionManager = (ConnectivityManager) activity.getSystemService(activity.getApplicationContext().CONNECTIVITY_SERVICE);
        serverAdress="http://"+ Server.SRVERADRESS+"/PIM/upload.php";
        new StreamFileTask(directory.listFiles(),serverAdress,activity).execute();
    }
    // return array of selected files
    public File[] getSelectedFiles() {
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            fileset = new HashSet();
            int i = 0;
            files = new File[uris.size()];
            for (Uri uri : uris) {
                fileset.add(getPathfromUri(uri));
                File f = new File(getPathfromUri(uri));
                files[i] = f;
                i++;
            }
        }
        return files;
    }
    //return one file
    public File[] getSelectedFile() {
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            fileset = new HashSet();
            fileset.add(getPathfromUri(uri));
            files = new File[1];
            File f = new File(getPathfromUri(uri));
            files[0] = f;

        }
        return files;
    }

    // return the file's path passed on parameter
    public String getPathfromUri(Uri uri) {
        if(uri.toString().startsWith("file://"))
            return uri.getPath();
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        activity.startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path= cursor.getString(column_index);
        //cursor.close();
        return path;
    }


}
