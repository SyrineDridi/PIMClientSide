package galaxypim.pimclientside;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import galaxypim.pimclientside.Utils.Config;

/**
 * Created by Ch on 09/02/2017.
 */


public class WifiReceiver extends BroadcastReceiver {
    String name = null;
    @Override
    public void onReceive(Context context, Intent intent) {


        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        Intent intent1 = new Intent(context, ServerService.class);
        Intent intent2 = new Intent(context, ServerServiceFiles.class);
        //    Intent intent3 = new Intent(context, FileModificationService.class);

     /*   IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);


        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                //do stuff
                System.out.println("wifi has change ");
            } else {
                // wifi connection was lost
                System.out.println("wifi has not change ");
            }
        } */
        System.out.println("state wifi" + info.getState().toString());
        if (info != null && info.isConnected()) {
            // Do your work.
            // e.g. To check the Network Name or other info:
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();

            if (Config.wifi_name == null) {
                Config.wifi_name = ssid;

                System.out.println("______________ yes coonection "+Config.wifi_name);
            } else {
                if (!Config.wifi_name.trim().equals(ssid.trim())){

                    System.out.println("______________ There is no coonection "+Config.wifi_name);
                    context.stopService(intent1);
                    context.stopService(intent2);
                    return;
                }
            }
            System.out.println("***** ssid : " + ssid);
            System.out.println(" ***** " + wifiInfo.getIpAddress());
            System.out.println("***** " + wifiInfo.getMacAddress());

            //      if(ssid.equals("\"Temo\"")){

            context.startService(intent1);
            context.startService(intent2);
            //           context.startService(intent3);

            System.out.println("matemchich");
            //       }


        } else {
            System.out.println("______________ There is no coonection ");
            context.stopService(intent1);
            context.stopService(intent2);
            //       context.stopService(intent3);
        }
    }
}