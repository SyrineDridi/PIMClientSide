package galaxypim.pimclientside;

import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;


public class Server {
    public static String SRVERADRESS="0";
    ServerService activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8585;
    private InetAddress ipAdressServer =null;

    private File directory= null;
    private String serverAdress="";
    HashSet<String> fileset ;
    File files  [] ;

    public Server(ServerService activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }

    private class SocketServerThread extends Thread {

        int count = 0; String text;

        @Override
        public void run() {

                        int server_port = 50008;
                        byte[] message = new byte[1500];
                        try{
                            System.out.println("blocked 1");
                            DatagramPacket p = new DatagramPacket(message, message.length);
                            DatagramSocket s = new DatagramSocket(server_port);
                            System.out.println("blocked 2");
                            s.receive(p);
                            System.out.println("blocked 3");
                            text = new String(message, 0, p.getLength());
                            System.out.println("rockman message:" + text);
                            s.close();
                        }catch(Exception e){
                            e.printStackTrace();

                        }

                  System.out.println(message);
                  Server.SRVERADRESS=text;
                  SharedPreferences sharedPref = activity.getSharedPreferences("adress",activity.MODE_PRIVATE);
                  SharedPreferences.Editor editor = sharedPref.edit();
                  editor.putString("serverAdress", Server.SRVERADRESS);
                  editor.commit();



            Boolean b = activity.showToast(text);
            System.out.println("Value B 1 = "+b);
            if(b == true){
                      directory = new File(Environment.getExternalStorageDirectory()+"/shared");
                      if(!directory.exists())
                          directory.mkdir();
                      new StreamFileTask(directory.listFiles(),
                              "http://"+Server.SRVERADRESS+"/PIMNEWWEB/Php/upload.php",activity).execute();
                  }
            System.out.println("Value B 2= "+b);
        }


    }

    private class SocketServerReplyThread extends Thread {



        private Socket hostThreadSocket;
        private Socket hostThreadSocket1;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            try {
                hostThreadSocket1 = new Socket(ipAdressServer,8586);
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


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

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
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();


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

    public void showDialog (){


    }
}