package galaxypim.pimclientside;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ch on 26/03/2017.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, ServerService.class);
        context.startService(intent1);
        Intent intent2 = new Intent(context, ServerServiceFiles.class);
        context.startService(intent2);
    }
}