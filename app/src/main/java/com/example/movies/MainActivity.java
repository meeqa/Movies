package com.example.movies;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
    public static final String API_KEY_REQUEST = "?api_key=";
    public static final String PAGE_REQUEST = "&page=";

    private static final String TOP_RATED = "top_rated_movies";
    private static final String MOST_POPULAR = "most_popular_movies";
    private static final String FROM_TOP = "FROM_TOP";
    private static final String DATA_RESULT = "result";
    private static final String DATA_ID = "id";
    private static final String DATA_TITLE = "title";
    private static final String DATA_POSTER_PATH = "poster_path";
    @BindView(R.id.recycler_view)
    RecyclerView recView;
    @BindView(R.id.button_popular)
    Button btnPopular;
    @BindView(R.id.button_top_rated)
    Button btnTopRated;
    private ArrayList<Movie> myMovies;
    private int page;
    private boolean loading;
    private RequestQueue queue;
    private JSONObject myData;
    private String showingMovies;
    private String url;
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        queue = Volley.newRequestQueue(this);
        myMovies = new ArrayList<>();
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(new MyAdapter(this.getApplicationContext(), myMovies));

        loading = false;
        setListeners();

        showingMovies = TOP_RATED;
        btnTopRated.setBackgroundColor(Color.GRAY);
        getTopRatedTitles(FROM_TOP);
    }

    public void setListeners() {
        btnPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPopular.setClickable(false);
                btnPopular.setBackgroundColor(Color.GRAY);
                btnTopRated.setBackgroundColor(Color.LTGRAY);
                getPopularTitles(FROM_TOP);
            }
        });
        btnTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTopRated.setClickable(false);
                btnPopular.setBackgroundColor(Color.LTGRAY);
                btnTopRated.setBackgroundColor(Color.GRAY);
                getTopRatedTitles(FROM_TOP);
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
        if (type.equals(FROM_TOP)) {
            myMovies.clear();
            page = 1;
            recView.scrollToPosition(1);
            //if recyclerView is scrolled to the bottom request more pages
        } else if (type.equals(MOST_POPULAR)) {
            page++;
        }
        url = URLS.MOVIES_API_BASE_URL
                + URLS.POPULAR_MOVIES_URL
                + API_KEY_REQUEST
                + URLS.API_KEY
                + PAGE_REQUEST
                + page;
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    JSONArray array = (JSONArray) myData.get(DATA_RESULT);
                    if (array.length() == 0) return;
                    for (int i = 0; i < array.length(); i++) {
                        Movie data = new Movie();
                        JSONObject object = (JSONObject) array.get(i);
                        data.setId(String.valueOf(object.get(DATA_ID)));
                        data.setTitle((String) object.get(DATA_TITLE));
                        data.setPosterPath((String) object.get(DATA_POSTER_PATH));
                        myMovies.add(data);
                    }
                    if (recView.getAdapter() != null) {
                        recView.getAdapter().notifyDataSetChanged();
                    }
                    btnPopular.setClickable(true);
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
        if (type.equals(FROM_TOP)) {
            myMovies.clear();
            page = 1;
            recView.scrollToPosition(1);
            //if recyclerView is scrolled to the bottom request more pages
        } else if (type.equals(TOP_RATED)) {
            page++;
        }
        url = URLS.MOVIES_API_BASE_URL
                + URLS.TOP_RATED_MOVIES_URL
                + API_KEY_REQUEST
                + URLS.API_KEY
                + PAGE_REQUEST
                + page;
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    JSONArray array = (JSONArray) myData.get(DATA_RESULT);
                    if (array.length() == 0) return;
                    for (int i = 0; i < array.length(); i++) {
                        Movie data = new Movie();
                        JSONObject object = (JSONObject) array.get(i);
                        data.setId(String.valueOf(object.get(DATA_ID)));
                        data.setTitle((String) object.get(DATA_TITLE));
                        data.setPosterPath((String) object.get(DATA_POSTER_PATH));
                        myMovies.add(data);
                    }
                    if (recView.getAdapter() != null) {
                        recView.getAdapter().notifyDataSetChanged();
                    }
                    btnTopRated.setClickable(true);
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
