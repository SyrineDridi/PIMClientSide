package galaxypim.pimclientside;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect;
    private File directory= null;
    private String serverAdress="";
    File files  [] ;
    Files sendFiles;
    private String serverAdress1="http://"+Server.SRVERADRESS+"/PIMNEWWEB/Php /upload.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendFiles=new Files(serverAdress1,this);
       // Lancer Service Listener Notification and send Data if user click YES
       //Intent intent1 = new Intent(this, ServerService.class);
       //this.startService(intent1);



        // Lancer Service ti ==o listen from the server
      /*  Intent intent2 = new Intent(this, ServerServiceFiles.class);
        this.startService(intent2);
        */


        /*****************************Create Folder Shared if not exist*******************/
        //check if the folder shared exist otherwise create it
        directory = new File(Environment.getExternalStorageDirectory()+"/shared");
        if(!directory.exists())
            directory.mkdir();


        sendFiles.setIntent(getIntent());
        Intent intent = sendFiles.getIntent();

        SharedPreferences sharedPref = this.getSharedPreferences("adress",this.MODE_PRIVATE);
        serverAdress1="http://"+sharedPref.getString("serverAdress", "0")+"/PIMNEWWEB/Php/upload.php";

        if (Intent.ACTION_SEND.equals(intent.getAction()))
        {

            files=  sendFiles.getSelectedFile();
            System.out.println(serverAdress1+"action_send main activity");
            if(!Server.SRVERADRESS.equals("0"))
                new StreamFileTask(files , serverAdress1, this ).execute();
        }
        else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction()))
        {
            files=  sendFiles.getSelectedFiles();
            System.out.println(serverAdress1+"action_send_multiple main activity");
            if(!Server.SRVERADRESS.equals("0"))
                new StreamFileTask(files , serverAdress1, this ).execute();
        }




    }


    private void SendFileFromPhoneToServer() {
          ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
          serverAdress1="http://"+Server.SRVERADRESS+"/PIM/upload.php";
          new StreamFileTask(directory.listFiles(),serverAdress1,MainActivity.this).execute();
      }
}
