package galaxypim.pimclientside;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by maher on 22/01/2017.
 */
public class StreamFileTask extends AsyncTask<Void,Integer,Void> {
    boolean[] syncSucces2;
    private File[] syncFiles;
    private String serverAdress="";
    private ProgressDialog progressBar;
    Context context;


    public StreamFileTask(File[] syncFiles, String serverAdress, Context context){
        this.syncFiles=syncFiles;
        this.serverAdress=serverAdress;
        this.context=context;



    }

    protected Void doInBackground(Void... params) {
        syncSucces2 = new boolean[syncFiles.length];
        Arrays.fill(syncSucces2, Boolean.FALSE);
        for (int i = 0; i < syncFiles.length; i++) {
            File file = syncFiles[i];


                File sourceFile = new File(file.getAbsolutePath());



                HttpURLConnection conn;
                DataOutputStream dos;
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                int serverResponseCode;
                Log.e("VideoUpload", "Uploading: sourcefileURI, " + file.getName());

                if (!sourceFile.isFile()) {
                    Log.e("uploadFile", "Source File not exist");
                } else {
                    try {
                        FileInputStream fin = new FileInputStream(sourceFile);
                        System.out.println();
                        URL url = new URL(serverAdress);
                        Log.v("VideoUpload", url.toString());
                        // Open a HTTP  connection to  the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data");
                        conn.setRequestProperty("FILENAME", file.getName());
                        conn.setRequestProperty("MACADR", getMacAdress());
                        conn.setRequestProperty("CONTENTLENGTH", String.valueOf(sourceFile.length()));
                        conn.connect();
                        publishProgress(2, i);



                        dos = new DataOutputStream(conn.getOutputStream());


                        bytesAvailable = fin.available();
                        int thirdOfBytes = bytesAvailable / 3;
                        int state = 0;
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        bytesRead = fin.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fin.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fin.read(buffer, 0, bufferSize);
                            Log.i("VideoUpload", "->");
                            if (bytesAvailable < thirdOfBytes && state == 1) {
                                publishProgress(4, i);
                                state = 2;
                            } else if (bytesAvailable < (2 * thirdOfBytes) && state == 0) {
                                publishProgress(3, i);
                                state = 1;
                            }
                        }
                      //  dos.flush();
                        publishProgress(5, i);

                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();
                        Log.i("VideoUpload", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                        publishProgress(9, i);

                        DataInputStream inStream;
                        HashMap<String,String> responseMap = new HashMap<>();
                        try {
                            inStream = new DataInputStream(conn.getInputStream());
                            String str;
                            while ((str = inStream.readLine()) != null) {
                                Log.e("VideoUpload", "SOF Server Response: " + str);
                                String[] responseItem = str.split(" - ");
                                responseMap.put(responseItem[0], responseItem[0]);
                            }
                            inStream.close();
                            if (responseMap.get("ERROR").equals("FALSE")) {
                                int index2 = Integer.parseInt(responseMap.get("INDEX"));
                                syncSucces2[index2] = true;

                            }
                        } catch (IOException e) {
                            Log.e("VideoUpload", "SOF error: " + e.getMessage(), e);
                        }
                        fin.close();
                        dos.flush();
                        dos.close();
                        publishProgress(10, i);
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                        Log.e("VideoUpload", "UL error: " + ex.getMessage(), ex);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("UploadFileException", "Exception : " + e.getMessage(), e);
                    }
                }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
       // progressBar.setProgress(values[0] + (values[1] * 10));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*
        progressBar = new ProgressDialog(context);
        progressBar.setMax(10 * syncFiles.length);
        progressBar.setMessage("Uploading File(s)");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(false);
//        progressBar.show();
*/
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
       // progressBar.hide();

    }
    private String getMacAdress(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return  wInfo.getMacAddress();
    }
}