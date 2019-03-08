package com.example.movies;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private static String API_KEY = "fe3b8cf16d78a0e23f0c509d8c37caad";
    private static String MOVIES_API_BASE_URL = "https://api.themoviedb.org/3";
    private static String POPULAR_MOVIES_URL = "/movie/popular";
    private static String TOP_RATED_MOVIES_URL = "/movie/top_rated";
    private RecyclerView recView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<HashMap<String, String>> myDataset;
    private RequestQueue queue;
    private JSONObject myData;
    String bit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recView = findViewById(R.id.recView);

        queue = Volley.newRequestQueue(this);
        myRequestPermissions();

        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);

        myDataset = new ArrayList<>();
        myAdapter = new MyAdapter(myDataset);
        recView.setAdapter(myAdapter);
        getTitles();

    }

    public void getTitles(){
        String url = MOVIES_API_BASE_URL + POPULAR_MOVIES_URL + "?api_key=" + API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    JSONArray array = (JSONArray) myData.get("results");
                    for(int i = 0; i < array.length(); i++){
                        HashMap<String, String> data = new HashMap<>();
                        JSONObject object = (JSONObject) array.get(i);
                        data.put("id", String.valueOf(object.get("id")));
                        data.put("title", (String)object.get("title"));
                        data.put("poster_path", (String)object.get("poster_path"));
                        getPoster(data.get("poster_path"));
                        data.put("poster", bit);
                        myDataset.add(data);
                    }
                    recView.getAdapter().notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

    }

    public void getPoster (String poster_path){

        String url = "https://image.tmdb.org/t/p/w400" + poster_path;
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b = baos.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);
                bit = temp;
            }
        }, 0,0,
                ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                    }
                });
        queue.add(imageRequest);
    }

    public void myRequestPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET},
                MY_PERMISSIONS_REQUEST_INTERNET);
    }
}
