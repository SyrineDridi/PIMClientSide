package galaxypim.pimclientside;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import galaxypim.pimclientside.Utils.Config;

public class Profil extends AppCompatActivity implements IPickResult.IPickClick {
    String Existe;
    TextView TvEmail, TvFirstName, TvLastName, TvPhone;
    String email, first_name, last_name, tel;
    CircleImageView profile_image;
    FloatingActionButton btnchoosefile, btnUpdateProfile;
    Button btnUpdate, btnCancel;
    private ImageView imgUserStories ;


    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_SIGNUP = 0;
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_REQUEST = 1888;

    private static final String TAG = MainActivity.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Bitmap bitmap;
    public String image = null;
    String id;
    private String filePath = null;
    private Uri fileUri; // file url to store image/video

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create"
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "Syrine" + timeStamp + ".jpg");

        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        TvEmail = (TextView) findViewById(R.id.TvEmail);
        TvFirstName = (TextView) findViewById(R.id.TvFirstName);
        TvLastName = (TextView) findViewById(R.id.TvLastName);
        TvPhone = (TextView) findViewById(R.id.TvPhone);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        btnchoosefile = (FloatingActionButton) findViewById(R.id.btnchoosefile);
        btnUpdateProfile = (FloatingActionButton) findViewById(R.id.btnUpdateProfile);
        imgUserStories = (ImageView) findViewById(R.id.ImgUserStory);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        /****** disable all the edittext ****/
        TvEmail.setEnabled(false);
        TvFirstName.setEnabled(false);
        TvLastName.setEnabled(false);
        TvPhone.setEnabled(false);


        String id_user = "";
        SharedPreferences sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("id_user", id_user) != null)
            id = sharedpreferences.getString("id_user", id_user);
        if (sharedpreferences.getString("email", "email") != null)
            email = sharedpreferences.getString("email", "email");

        first_name = sharedpreferences.getString("first_name", "");
        last_name = sharedpreferences.getString("last_name", "");
        tel = sharedpreferences.getString("tel", "tel");

        TvEmail.setText(email);
        TvFirstName.setText(first_name);
        TvLastName.setText(last_name);
        TvPhone.setText(tel);
        Picasso.with(Profil.this).load("http://" + Server.SRVERADRESS.trim() + "/PIMNEWWEB/Php/uploads/"+email+".jpg")
                .into(profile_image);

        /****** end disable all the edittext ****/
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /****** enabled all the edittext ****/
                TvEmail.setEnabled(true);
                TvFirstName.setEnabled(true);
                TvLastName.setEnabled(true);
                TvPhone.setEnabled(true);
                btnUpdate.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnchoosefile.setVisibility(View.VISIBLE);
                /****** end enabled all the edittext ****/
            }
        });


        imgUserStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Profil.this,UserStoryActivity.class) ;
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TvEmail.setEnabled(false);
                TvFirstName.setEnabled(false);
                TvLastName.setEnabled(false);
                TvPhone.setEnabled(false);
                btnUpdate.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnchoosefile.setVisibility(View.GONE);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUser();
            }
        });




        if (!Server.SRVERADRESS.trim().equals("0")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://" + Server.SRVERADRESS.trim() + "/PIMNEWWEB/Php/User.php?method=findById&id=" + id, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d("", response.toString());
                    System.out.println(response.toString());
                    try {

                        // Parsing json object response
                        // response will be a json object
                        JSONObject mainObject = new JSONObject(response.toString());
                        Existe = mainObject.getString("Existe");
                        JSONArray array = new JSONArray(Existe);
                        String user = array.get(0).toString();
                        System.out.println("hhhhh" + user);
                        JSONObject ObjectUser = new JSONObject(user);


                        //  TvEmail.setText(email);
                        //  TvFirstName.setText(mainObject.getString("fist_name"));
                        //    TvLastName.setText(mainObject.getString("last_name"));
                        //   TvPhone.setText(mainObject.getString("tel"));
                        //   Picasso.with(Profil.this).load(mainObject.getString("url_image"))
                        //          .into(profile_image);


                        System.out.println("first name " + first_name.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());

                }
            });
            // Adding request to request queue*
            RequestQueue requestQueue = Volley.newRequestQueue(Profil.this);
            requestQueue.add(stringRequest);
        }
    }

    public void takeImageFromCamera() {
        captureImage();
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                //selectedImagePath = getPath(selectedImageUri);
                Uri selectedImageUri = data.getData();
                filePath = getPath(selectedImageUri);
                //launchUploadActivity();

                previewMedia();
            }

            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity();

                    previewMedia();


                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();


    }

    private void launchUploadActivity() {

        filePath = fileUri.getPath();


    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public String getOutputMediaFileName(File type) {
        return type.getName();
    }

    private void previewMedia() {
        // Checking whether captured media is image or video

        profile_image.setVisibility(View.VISIBLE);

        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        Toast.makeText(getApplicationContext(),
                filePath, Toast.LENGTH_SHORT)
                .show();
//        bitmap = BitmapFactory.decodeFile(filePath, options);
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.no_mage);
        //Setting the Bitmap to ImageView

        profile_image.setImageBitmap(bitmap);


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onGaleryClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
        //startActivityForResult(intent, SELECT_PICTURE);

    }

    @Override
    public void onCameraClick() {
        takeImageFromCamera();
    }

    @Override
    public void onBackPressed() {

        //   Intent main = new Intent(Register.this, LoginActivity.class);
        //  startActivityForResult(main, REQUEST_SIGNUP);
        //   overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
        finish();


    }

    void UpdateUser() {

        final String email = TvEmail.getText().toString().trim();
        final String firstname = TvFirstName.getText().toString().trim();
        final String lastname = TvLastName.getText().toString().trim();
        //   final String password = txtPassword.getText().toString().trim();
        final String phone = TvPhone.getText().toString().trim();

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        // final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        if (!Server.SRVERADRESS.trim().equals("0")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + Server.SRVERADRESS.trim() + "/PIMNEWWEB/Php/UpdateUser.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                System.out.println(s + "android response");
                                JSONObject mainObject = new JSONObject(s.toString());
                                String id_user = mainObject.getString("id");
                                TvEmail.setText(email);
                                TvFirstName.setText(mainObject.getString("fist_name"));
                                TvLastName.setText(mainObject.getString("last_name"));
                                TvPhone.setText(mainObject.getString("tel"));
                                Picasso.with(Profil.this).load(mainObject.getString("url_image"))
                                        .into(profile_image);
                                Intent intent = new Intent(Profil.this, Profil.class);
                                startActivity(intent);
                            } catch (JSONException e) {
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
                            //  System.out.println(volleyError.getMessage());
                            //            Toast.makeText(getApplicationContext(),"azeaz"+ volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    //     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
                    bitmap = BitmapFactory.decodeResource(Profil.this.getResources(), R.drawable.no_mage);
                    image = getStringImage(bitmap);
                    System.out.println("zzzzzzzzz" + image);
                    String namee = "Syrine" + ".jpg";
                    //Getting Image Nam
                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();

                    //Adding parameters
                    params.put("fileToUpload", image);
                    params.put("first_name", firstname);
                    params.put("last_name", lastname);
                    params.put("tel", phone);
                    params.put("email", (String) email);
                    params.put("id", id);

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(Profil.this);

            if (firstname.length() >= 10) {
                Toast.makeText(Profil.this, "UserName Trop Long", Toast.LENGTH_LONG).show();
            } else if (!matcher.matches()) {
                Toast.makeText(Profil.this, "Verifier Votre Email", Toast.LENGTH_LONG).show();
            }

            requestQueue.add(stringRequest);

        }
    }
}
