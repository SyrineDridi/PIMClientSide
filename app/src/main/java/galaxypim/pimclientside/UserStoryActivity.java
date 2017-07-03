package galaxypim.pimclientside;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import galaxypim.pimclientside.Adapter.UserStoryAdapter;
import galaxypim.pimclientside.Datasource.UserStoryDataSource;
import galaxypim.pimclientside.Entities.UserStory;
import galaxypim.pimclientside.SqliteDb.UserSoSqlite;
import galaxypim.pimclientside.SqliteDb.UserStorySqlite;

public class UserStoryActivity extends AppCompatActivity {

    RecyclerView RecycLeViewUserStory;
    UserStoryAdapter adapter;
    private ArrayList<UserStory> user_stories = new ArrayList<>();
    UserStoryDataSource ds;
    String Existe;
    String id;
    Paint p = new Paint();
    String avancement, nomprojet, description, priority;
    int estimation, id_user_story;
    String Etat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_story);
        ds = new UserStoryDataSource(this);
        ds.open();
        RecycLeViewUserStory = (RecyclerView) findViewById(R.id.RecycLeViewUserStory);
        RecycLeViewUserStory.setLayoutManager(new LinearLayoutManager(this));
        RecycLeViewUserStory.setItemAnimator(new DefaultItemAnimator());
        adapter = new UserStoryAdapter(UserStoryActivity.this, user_stories);
        RecycLeViewUserStory.setAdapter(adapter);


        String id_user = "";
        SharedPreferences sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("id_user", id_user) != null)
            id = sharedpreferences.getString("id_user", id_user);
        System.out.println("id user is " + id);
        getAllUserStoryDataBase();


    }


    void getAllUserStoryDataBase() {

        if (!Server.SRVERADRESS.trim().equals("0")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://" + Server.SRVERADRESS.trim() + "/PIMNEWWEB/Php/UsersStory.php?method=findByUser&id=" + id, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("", response.toString());
                    System.out.println(response.toString());
                    try {
                        System.out.println("la reponse de jsoon est " + response.toString());
                        // Parsing json object response
                        // response will be a json object
                        JSONObject mainObject = new JSONObject(response.toString());
                        Existe = mainObject.getString("Existe");
                        JSONArray array = new JSONArray(Existe);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject user = array.getJSONObject(i);
                            UserStory userStory = new UserStory();
                            avancement = user.getString("avancement");
                            description = user.getString("description");
                            nomprojet = user.getString("code_projet");
                            priority = user.getString("priority");
                            estimation = user.getInt("estimation");
                            id_user_story = user.getInt("id");
                            userStory.setDesc(description);
                            userStory.setAvancement(avancement);
                            userStory.setNom_projet(nomprojet);
                            userStory.setEstimation(estimation);
                            userStory.setId(id_user_story);
                            userStory.setNom_projet(nomprojet);
                            user_stories.add(userStory);
                        }
                        RecycLeViewUserStory.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        initSwipe();
                        //  String user = array.get(0).toString();
                        //  System.out.println("hhhhh" + user);
                        //  JSONObject ObjectUser = new JSONObject(user);
                        // System.out.println("the user story is" + ObjectUser);
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
            RequestQueue requestQueue = Volley.newRequestQueue(UserStoryActivity.this);
            requestQueue.add(stringRequest);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    Etat = "DONE";
                    System.out.println("etatt"+Etat);
                    updateUserStory(Etat,position);
                } else {
                    Etat = "DOING";
                    System.out.println("etatt"+Etat);
                    updateUserStory(Etat,position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.done);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                        // adapter.notifyDataSetChanged();
                    } else {
                        p.setColor(Color.parseColor("#ffff00"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.doing);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(RecycLeViewUserStory);
    }

    void updateUserStory(String Etat,int position) {
        int id_story  = user_stories.get(position).getId();
        System.out.println("Serveer adress" + Server.SRVERADRESS);
        if (!Server.SRVERADRESS.trim().equals("0")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+Server.SRVERADRESS.trim()+ "/PIMNEWWEB/Php/UsersStory.php?method=update&id="+id_story+"&avancement=" + Etat,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            System.out.println(s+"la reoooo");
                            System.out.println("user story id "+id_user_story);
                            user_stories.clear();
                            getAllUserStoryDataBase();

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
            };
            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(UserStoryActivity.this);
            requestQueue.add(stringRequest);
        }
    }
}
