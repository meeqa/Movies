package com.example.movies;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {
    private static String DETAIL_INTENT_STRING = "MOVIE_DETAILS";
    private String movieId;
    private RequestQueue queue;
    private JSONObject myData;
    private Movie movieDetails;

    @BindView(R.id.coverImage) ImageView coverImage;
    @BindView(R.id.posterImage) ImageView posterImage;
    @BindView(R.id.titleView) TextView titleView ;
    @BindView(R.id.overviewView) TextView overviewView;
    @BindView(R.id.releaseDateView) TextView releaseDateView;
    @BindView(R.id.genresView) TextView genresView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        queue = Volley.newRequestQueue(this);
        getMovieDetails();
    }


    public void getMovieDetails() {
        movieDetails = new Movie();
        movieId = getIntent().getStringExtra(DETAIL_INTENT_STRING);
        String url = URLS.MOVIES_API_BASE_URL + URLS.MOVIE_DETAILS_URL + movieId + "?api_key=" + URLS.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    movieDetails.setId(movieId);
                    movieDetails.setTitle((String) myData.get("title"));
                    movieDetails.setPosterPath((String) myData.get("poster_path"));
                    movieDetails.setCoverPath((String) myData.get("backdrop_path"));
                    movieDetails.setReleaseDate((String) myData.get("release_date"));
                    movieDetails.setOverview((String) myData.get("overview"));
                    JSONArray arr = (JSONArray) myData.get("genres");
                    for (int i = 0; i < arr.length(); i++) {
                        movieDetails.addGenre((String) ((JSONObject) arr.get(i)).get("name"));
                    }
                    showDetails();

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
    public void showDetails(){
        titleView.setText(movieDetails.getTitle());
        Picasso.get().load(URLS.IMAGE_BASE_URL + movieDetails.getCoverPath()).into(coverImage);
        Picasso.get().load(URLS.IMAGE_BASE_URL + movieDetails.getPosterPath()).into(posterImage);
        overviewView.setText(movieDetails.getOverview());
        releaseDateView.setText(movieDetails.getReleaseDate());
        genresView.setText(movieDetails.getGenres().toString());
    }

}


