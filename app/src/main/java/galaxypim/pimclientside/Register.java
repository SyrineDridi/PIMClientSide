package galaxypim.pimclientside;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vansuita.pickimage.IPickResult;
import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import galaxypim.pimclientside.Utils.Config;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Register extends AppCompatActivity {

    private EditText txtEmail, txtFirstName, txtLastName, txtPassword, txtPhone;
    private Button btnRegister;
    private CircleImageView ImageUpload;
    public FloatingActionButton ChooseImg;
    SharedPreferences sharedpreferences;
    private Socket socket;
    private String email , firstname , lastname , phone ;

    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_CAMERA = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_SIGNUP = 0;

    private static final String TAG = MainActivity.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Bitmap bitmap;
    public String image = null;
    private String filePath = null;
    private Uri fileUri; // file url to store image/video
    private Bitmap bmpUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        connectWebSocket();
        System.out.println("dfgugi" + getMacAddr());

        sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String state = sharedpreferences.getString("state_user", null);
        System.out.println("mon state est" + state);
        System.out.println("the state est " + state);

        if (state != null) {
            if (state.trim().equals("registred")) {
                Intent intent = new Intent(Register.this, Profil.class);
                startActivity(intent);
            }
        }
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        
        btnRegister = (Button) findViewById(R.id.btnRegister);
        ImageUpload = (CircleImageView) findViewById(R.id.ImageUpload);
        ChooseImg = (FloatingActionButton) findViewById(R.id.ChooseImg);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected() == true) {
                    uploadImage();
                } else {
                    new AlertDialog.Builder(Register.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage(
                                    "veuillez vous connecter a internet et cliquez sur ok pour continuer")
                            .setPositiveButton("OK", null).show();

                }
            }
        });

        ChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */


    private void launchUploadActivity() {

        filePath = fileUri.getPath();


    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    public void onBackPressed() {

        //   Intent main = new Intent(Register.this, LoginActivity.class);
        //  startActivityForResult(main, REQUEST_SIGNUP);
        //   overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
        finish();


    }


    private void uploadImage() {
        //Showing the progress dialog

         email = txtEmail.getText().toString().trim();
        firstname = txtFirstName.getText().toString().trim();
        lastname = txtLastName.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();
        phone = txtPhone.getText().toString().trim();

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        // final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        System.out.println("Serveer adress" + Server.SRVERADRESS);
        if (!Server.SRVERADRESS.trim().equals("0")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + Server.SRVERADRESS.trim() + "/PIMNEWWEB/Php/RegisterUser.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject mainObject = new JSONObject(s.toString());
                                String id_user = mainObject.getString("id");


                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("id_user", id_user);
                                editor.putString("state_user", "registred");
                              editor.putString("email", email);
                                editor.putString("first_name", firstname);
                               editor.putString("last_name", lastname);
                                editor.putString("tel", phone);
                                editor.commit();
                                System.out.println("sccss");
                                System.out.println("la réponse est " + s);
                                Intent intent = new Intent(Register.this, Profil.class);
                                startActivity(intent);
                            } catch (JSONException e) {
                                System.out.println("la réponse est " + s);

                                System.out.println("l erreuuur est" + e.getMessage().toString());
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            //loading.dismiss();
                            //Showing toast
                            Log.d("", "" + volleyError);
                            System.out.println("kkkk" + volleyError.getMessage());
                            //            Toast.makeText(getApplicationContext(),"azeaz"+ volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    //     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
                    image = getStringImage(bmpUser);
                    System.out.println("zzzzzzzzz" + image);
                    String namee = "Syrine" + ".jpg";
                    //Getting Image Nam
                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put("fileToUpload", image);
                    params.put("name", namee);
                    params.put("first_name", firstname);
                    params.put("last_name", lastname);
                    params.put("tel", phone);
                    params.put("email", (String) email);
                    params.put("password", password);
                    params.put("code_team", "1");
                    params.put("degree", "1");
                    params.put("mac_address", getMacAddr());

                    //returning parameters
                    return params;
                }

            };


            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(Register.this);

            if (firstname.length() >= 10) {
                Toast.makeText(Register.this, "UserName Trop Long", Toast.LENGTH_LONG).show();
            } else if (!matcher.matches()) {
                Toast.makeText(Register.this, "Verifier Votre Email", Toast.LENGTH_LONG).show();
            }

            requestQueue.add(stringRequest);

        }
    }

    private void connectWebSocket() {
        try {
            socket = IO.socket("http://" + Server.SRVERADRESS.trim() + ":3000");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        socket.on("SentInformation", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String[] tab = new String[3];
                tab[0] = "disc" + getIpAddress() + "*" + getMacAddr();
                tab[1] = getIpAddress() + "*" + getMacAddr();
                socket.emit("SentInformation", tab);
                // socket.disconnect();


            }
        });
        socket.on("SendInfoAdress", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                //  socket.emit("SendInfoAdress", getIpAddress());
                // socket.disconnect();


            }
        });
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("SentInformation", getIpAddress() + "*" + getMacAddr());
                socket.emit("SendInfoAdress", getIpAddress());
                // socket.disconnect();
                System.out.println("hello");

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                // socket.emit("logout", getMacAddr());
            }

        });
        socket.connect();
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
                        ip = inetAddress.getHostAddress();


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

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private void selectImage() {
        TextView tvCamera, tvGallery;
        Button btnCancelPicture;

        final Dialog dialog = new Dialog(Register.this);

        dialog.setContentView(R.layout.dialog_select_image);
        tvCamera = (TextView) dialog.findViewById(R.id.tvCamera);
        tvGallery = (TextView) dialog.findViewById(R.id.tvGallery);
        btnCancelPicture = (Button) dialog.findViewById(R.id.btnCancelPicture);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment
                        .getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                dialog.dismiss();
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                dialog.dismiss();
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        SELECT_PICTURE);
            }
        });
        btnCancelPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 2;

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-90);
                    bmpUser = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    ImageUpload.setImageBitmap(bmpUser);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "test";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    fOut = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                int angle = 0;
                String tempPath = getPath(selectedImageUri, Register.this);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(tempPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            angle = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            angle = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            angle = 270;
                            break;
                        default:
                            angle = 0;
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bm = null;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                btmapOptions.inSampleSize = 2;
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                Matrix matrix = new Matrix();
                matrix.postRotate(angle);
                bmpUser = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                ImageUpload.setImageBitmap(bmpUser);

            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    /***end select image ****/


}


