package galaxypim.pimclientside.Utils;

import galaxypim.pimclientside.Server;

/**
 * Created by maher on 14/12/2016.
 */
public class Config {
    // File upload url (replace the ip with your server address)

    public static final String FILE_UPLOAD_URL = "http://"+Server.SRVERADRESS.trim()+"/PIMNEWWEB/Php/RegisterUser.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    public static String wifi_name = null;

    public static final String UPDATE_URL = "http://elchebbi-ahmed.alwaysdata.net/fileUpload2.php";
}
