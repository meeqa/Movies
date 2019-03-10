package com.example.movies;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private RecyclerView recView;
    private ArrayList<Movie> myMovies;
    private RequestQueue queue;
    private JSONObject myData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recView = findViewById(R.id.recView);

        queue = Volley.newRequestQueue(this);
        myRequestPermissions();

        recView.setLayoutManager(new LinearLayoutManager(this));

        myMovies = new ArrayList<>();
        recView.setAdapter(new MyAdapter(this.getApplicationContext(),myMovies));
        getTitles();

    }

    public void getTitles(){
        String url = URLS.MOVIES_API_BASE_URL + URLS.POPULAR_MOVIES_URL + "?api_key=" + URLS.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    JSONArray array = (JSONArray) myData.get("results");
                    for(int i = 0; i < array.length(); i++){
                        Movie data = new Movie();
                        JSONObject object = (JSONObject) array.get(i);
                        data.setId(String.valueOf(object.get("id")));
                        data.setTitle((String)object.get("title"));
                        data.setPosterPath((String)object.get("poster_path"));
                        myMovies.add(data);
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

    public void myRequestPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET},
                MY_PERMISSIONS_REQUEST_INTERNET);
    }

}
