package com.example.movies;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static String TOP_RATED = "top_rated_movies";
    private static String MOST_POPULAR = "most_popular_movies";
    private ArrayList<Movie> myMovies;
    private RequestQueue queue;
    private JSONObject myData;
    private String showingMovies;
    private int page;
    private boolean loading;

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.popularButton)
    Button popularButton;
    @BindView(R.id.topRatedButton)
    Button topRatedButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        queue = Volley.newRequestQueue(this);

        recView.setLayoutManager(new LinearLayoutManager(this));
        myMovies = new ArrayList<>();
        recView.setAdapter(new MyAdapter(this.getApplicationContext(), myMovies));

        loading = false;
        setListeners();

        showingMovies = TOP_RATED;
        topRatedButton.setBackgroundColor(Color.GRAY);
        getTopRatedTitles("fromTop");
    }

    public void setListeners() {
        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popularButton.setClickable(false);
                popularButton.setBackgroundColor(Color.GRAY);
                topRatedButton.setBackgroundColor(Color.LTGRAY);
                getPopularTitles("fromTop");
            }
        });
        topRatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topRatedButton.setClickable(false);
                popularButton.setBackgroundColor(Color.LTGRAY);
                topRatedButton.setBackgroundColor(Color.GRAY);
                getTopRatedTitles("fromTop");
            }
        });
        //if recyclerView is scrolled to the bottom load more movies
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recView.canScrollVertically(1)) {
                    if (!loading) {
                        if (showingMovies.equals(MOST_POPULAR)) {
                            loading = true;
                            getPopularTitles(MOST_POPULAR);
                        } else {
                            loading = true;
                            getTopRatedTitles(TOP_RATED);
                        }
                    }
                }
            }
        });
    }

    //get popular movie titles
    public void getPopularTitles(String type) {
        showingMovies = MOST_POPULAR;
        //if movies are loaded from first page
        if (type.equals("fromTop")) {
            myMovies.clear();
            page = 1;
            recView.scrollToPosition(1);
        //if recyclerView is scrolled to the bottom request more pages
        } else if (type.equals(MOST_POPULAR)) {
            page++;
        }
        String url = URLS.MOVIES_API_BASE_URL + URLS.POPULAR_MOVIES_URL + "?api_key=" + URLS.API_KEY + "&page=" + page;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    JSONArray array = (JSONArray) myData.get("results");
                    if (array.length() == 0) return;
                    for (int i = 0; i < array.length(); i++) {
                        Movie data = new Movie();
                        JSONObject object = (JSONObject) array.get(i);
                        data.setId(String.valueOf(object.get("id")));
                        data.setTitle((String) object.get("title"));
                        data.setPosterPath((String) object.get("poster_path"));
                        myMovies.add(data);
                    }
                    recView.getAdapter().notifyDataSetChanged();
                    popularButton.setClickable(true);
                    loading = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    //get top rated titles
    public void getTopRatedTitles(String type) {
        showingMovies = TOP_RATED;
        //if movies are loaded from first page
        if (type.equals("fromTop")) {
            myMovies.clear();
            page = 1;
            recView.scrollToPosition(1);
        //if recyclerView is scrolled to the bottom request more pages
        } else if (type.equals(TOP_RATED)) {
            page++;
        }
        String url = URLS.MOVIES_API_BASE_URL + URLS.TOP_RATED_MOVIES_URL + "?api_key=" + URLS.API_KEY + "&page=" + page;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    JSONArray array = (JSONArray) myData.get("results");
                    if (array.length() == 0) return;
                    for (int i = 0; i < array.length(); i++) {
                        Movie data = new Movie();
                        JSONObject object = (JSONObject) array.get(i);
                        data.setId(String.valueOf(object.get("id")));
                        data.setTitle((String) object.get("title"));
                        data.setPosterPath((String) object.get("poster_path"));
                        myMovies.add(data);
                    }
                    recView.getAdapter().notifyDataSetChanged();
                    topRatedButton.setClickable(true);
                    loading = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

}
