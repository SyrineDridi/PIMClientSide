package galaxypim.pimclientside;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerFiles {
    public static int NameFile = 0;

    ServerServiceFiles activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8787;
    private InetAddress ipAdressServer =null;

    public ServerFiles(ServerServiceFiles activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

        new Thread(){
            @Override
            public void run() {
            }
        }.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
		/*	try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

        }
    }

    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            try {



                new FileServer(socketServerPORT).start();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

       // saveFile(clientSock);

        public class FileServer extends Thread {

            private ServerSocket ss;

            public FileServer(int port) {
                try {
                    ss = new ServerSocket(port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void run() {
                while (true) {
                    try {
                        while (true) {
                            System.out.println("bbglglufvlfvlu obv");
                            System.out.println(ss+"this the message ");
                            if (ss != null )
                            new Thread(new ClientWorker(ss.accept())).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }






        }

    }

    class ClientWorker implements Runnable {

        private Socket target_socket;
        private DataInputStream din;
        private DataOutputStream dout;

        public ClientWorker(Socket recv_socket) {
                 target_socket = recv_socket;


        }

        @Override
        public void run() {
        try {
            RandomAccessFile rw = null;
            long current_file_pointer = 0;
            boolean loop_break = false;
            while (true) {
                din = new DataInputStream(target_socket.getInputStream());
                dout = new DataOutputStream(target_socket.getOutputStream());
                byte[] initilize = new byte[1];
                try {
                    din.read(initilize, 0, initilize.length);
                    if (initilize[0] == 2) {
                        byte[] cmd_buff = new byte[3];
                        din.read(cmd_buff, 0, cmd_buff.length);
                        byte[] recv_data = ReadStream();
                        switch (Integer.parseInt(new String(cmd_buff))) {
                            case 124:
                                rw = new RandomAccessFile("/storage/emulated/0/shared/" + new String(recv_data), "rw");
                                System.out.println("**************************   +>  File Name "+ new String(recv_data));
                                String file_name = new String(recv_data);
                                System.out.println("**************************   +>  Size "+ rw.length());
                                long size_file = rw.length();
                                System.out.println("**************************   +>  Ip Address Mobile "+ getIpAddress());
                                String ip_address = getIpAddress();
                                System.out.println("**************************   +>  MAC Address Mobile "+ getMacAdress());
                                String mac_address = getMacAdress();
                                String url = "http://"+Server.SRVERADRESS+"/PIMNEWWEB/Php/Server/SendToClient/SaveLogSendFromServerToMobile.php?file_name="+file_name+"&size_file="+size_file+"&ip_address="+ip_address+"&mac_address="+mac_address;
                                RequestQueue mRequestQueue;
                                // Instantiate the cache
                                Cache cache = new DiskBasedCache(activity.getCacheDir(), 1024 * 1024); // 1MB cap
                                // Set up the network to use HttpURLConnection as the HTTP client.
                                Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
                                mRequestQueue = new RequestQueue(cache, network);

// Start the queue
                                mRequestQueue.start();

                                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println("YESSSSSSSSSSSSS");
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("NOOOOOOOOOOOOOO");
                                    }
                                });String  tag_string_req = "string_req";
                                // Add the request to the RequestQueue.
                                mRequestQueue.add(req);

                                dout.write(CreateDataPacket("125".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                                dout.flush();
                                break;
                                case 126:
                                rw.seek(current_file_pointer);
                                rw.write(recv_data);
                                current_file_pointer = rw.getFilePointer();
                                System.out.println("Download percentage: " + ((float) current_file_pointer / rw.length()) * 100 + "%");
                                dout.write(CreateDataPacket("125".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                                dout.flush();
                                break;
                            case 127:
                                if ("Close".equals(new String(recv_data))) {
                                    loop_break = true;
                                }
                                break;
                        }
                    }
                    if (loop_break == true) {
                        target_socket.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }

        private byte[] ReadStream() {
            byte[] data_buff = null;
            try {
                int b = 0;
                String buff_length = "";
                while ((b = din.read()) != 4) {
                    buff_length += (char) b;
                }
                int data_length = Integer.parseInt(buff_length);
                data_buff = new byte[Integer.parseInt(buff_length)];
                int byte_read = 0;
                int byte_offset = 0;
                while (byte_offset < data_length) {
                    byte_read = din.read(data_buff, byte_offset, data_length - byte_offset);
                    byte_offset += byte_read;
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            return data_buff;
        }

        private byte[] CreateDataPacket(byte[] cmd, byte[] data) {
            byte[] packet = null;
            try {
                byte[] initialize = new byte[1];
                initialize[0] = 2;
                byte[] separator = new byte[1];
                separator[0] = 4;
                byte[] data_length = String.valueOf(data.length).getBytes("UTF8");
                packet = new byte[initialize.length + cmd.length + separator.length + data_length.length + data.length];

                System.arraycopy(initialize, 0, packet, 0, initialize.length);
                System.arraycopy(cmd, 0, packet, initialize.length, cmd.length);
                System.arraycopy(data_length, 0, packet, initialize.length + cmd.length, data_length.length);
                System.arraycopy(separator, 0, packet, initialize.length + cmd.length + data_length.length, separator.length);
                System.arraycopy(data, 0, packet, initialize.length + cmd.length + data_length.length + separator.length, data.length);

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            return packet;
        }
    }

    private class SocketServerReplyThread extends Thread {



        private Socket hostThreadSocket;
        private Socket hostThreadSocket1;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            try {
                hostThreadSocket1 = new Socket(ipAdressServer,8787);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "ok";

            try {
                outputStream = hostThreadSocket1.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();


                message += "replayed: " + msgReply + "\n";
                System.out.println(message);
             /*   activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.etIPAdress.setText(message);
                    }
                });
                */

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
            /*
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    activity.etIPAdress.setText(message);
                }
            });
            */
        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    private String getMacAdress(){
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return  wInfo.getMacAddress();
    }




}