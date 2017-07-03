package galaxypim.pimclientside;


import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;


/**
 * Created by Ch on 29/01/2017.
 */

public class ServerService extends Service {
    private Looper mServiceLooper;
    MainActivity context;
    private ServiceHandler mServiceHandler;
    Server server;
    boolean res=false;
    private File directory= null;



    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
           stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();


        //my code
        server = new Server(this);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    public  boolean showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());

             handler.post(new Runnable() {
                @Override
                public void run() {
                    // run this code in the main thread
                    //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                    builder1.setMessage("Do you want to participate in this meeting?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    System.out.println("YESFROMDIALOG");
                                    directory = new File(Environment.getExternalStorageDirectory()+"/shared");
                                    if(!directory.exists())
                                        directory.mkdir();
                                    Toast.makeText(getApplicationContext(), "You will receive all for this meeting", Toast.LENGTH_SHORT).show();

                                    new StreamFileTask(directory.listFiles(),
                                            "http://"+Server.SRVERADRESS+"/PIMNEWWEB/Php/upload.php",getApplicationContext()).execute();
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    System.out.println("NOFROMDIALOG");

                                    dialog.cancel();
                                    res = false;

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alert11.show();


                }
            });

        System.out.println("Resultat du dialog == "+res);
        return res;


    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
      //  Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
     //   server.onDestroy();
    }

}
