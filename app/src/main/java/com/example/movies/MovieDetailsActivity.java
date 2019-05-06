package com.example.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String DETAIL_INTENT_STRING = "MOVIE_DETAILS";
    private static final String LOAD_MORE = "LOAD_MORE";
    private static final String DATA_ID = "id";
    private static final String DATA_TITLE = "title";
    private static final String DATA_POSTER_PATH = "poster_path";
    private static final String DATA_COVER_PATH = "backdrop_path";
    private static final String DATA_RELEASE_DATE = "release_date";
    private static final String DATA_OVERVIEW = "overview";
    private static final String DATA_GENRES = "genres";
    private static final String DATA_NAME = "name";
    private static final String DATA_RESULTS = "results";
    @BindView(R.id.image_cover)
    ImageView imageCover;
    @BindView(R.id.image_poster)
    ImageView imagePoster;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_overview)
    TextView textOverview;
    @BindView(R.id.text_release_date)
    TextView textReleaseDate;
    @BindView(R.id.text_genres)
    TextView textGenres;
    @BindView(R.id.linear_layout_similar_movies)
    LinearLayout linearLayoutSimilarMovies;
    @BindView(R.id.horizontal_scroll_view_similar_movies)
    HorizontalScrollView horizontalScrollSimilarMovies;
    private int page;
    private boolean loading;
    private String movieId;
    private RequestQueue queue;
    private JSONObject myData;
    private Movie movieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        queue = Volley.newRequestQueue(this);
        movieId = getIntent().getStringExtra(DETAIL_INTENT_STRING);
        loading = false;

        //if scroll comes to the end load more movies
        horizontalScrollSimilarMovies.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!horizontalScrollSimilarMovies.canScrollHorizontally(1)) {
                    if (!loading) {
                        loading = true;
                        getSimilarMovies(LOAD_MORE);
                    }
                }
            }
        });

        getMovieDetails();
        page = 1;
        getSimilarMovies("");
    }

    public void getMovieDetails() {
        movieDetails = new Movie();
        String url = URLS.MOVIES_API_BASE_URL
                + URLS.MOVIE_DETAILS_URL
                + movieId
                + MainActivity.API_KEY_REQUEST
                + URLS.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    myData = new JSONObject(response);
                    movieDetails.setId(movieId);
                    movieDetails.setTitle((String) myData.get(DATA_TITLE));
                    movieDetails.setPosterPath((String) myData.get(DATA_POSTER_PATH));
                    movieDetails.setCoverPath((String) myData.get(DATA_COVER_PATH));
                    movieDetails.setReleaseDate((String) myData.get(DATA_RELEASE_DATE));
                    movieDetails.setOverview((String) myData.get(DATA_OVERVIEW));
                    JSONArray arr = (JSONArray) myData.get(DATA_GENRES);
                    for (int i = 0; i < arr.length(); i++) {
                        movieDetails.addGenre((String) ((JSONObject) arr.get(i)).get(DATA_NAME));
                    }
                    showDetails();

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

    public void getSimilarMovies(String type) {
        //if this method is called with "more" string then more similar movies are required to load so variable page is incremented
        if (type.equals(LOAD_MORE)) {
            page++;
        }
        movieDetails = new Movie();
        String url = URLS.MOVIES_API_BASE_URL
                + URLS.MOVIE_DETAILS_URL
                + movieId
                + URLS.MOVIE_SIMILAR_URL
                + MainActivity.API_KEY_REQUEST
                + URLS.API_KEY
                + MainActivity.PAGE_REQUEST
                + page;
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showSimilar(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        queue.add(stringRequest);
    }

    public void showDetails() {
        textTitle.setText(movieDetails.getTitle());
        Picasso.get()
                .load(URLS.IMAGE_BASE_URL + movieDetails.getCoverPath())
                .into(imageCover);
        Picasso.get()
                .load(URLS.IMAGE_BASE_URL + movieDetails.getPosterPath())
                .into(imagePoster);
        textOverview.setText(movieDetails.getOverview());
        textReleaseDate.setText(movieDetails.getReleaseDate());
        String genres = movieDetails.getGenres().toString();
        textGenres.setText(genres.substring(1, genres.length() - 1));
    }

    public void showSimilar(String response) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        try {
            myData = new JSONObject(response);
            JSONArray array = (JSONArray) myData.get(DATA_RESULTS);
            if (array.length() == 0) return;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                final String id = object.getString(DATA_ID);
                View v = layoutInflater.inflate(R.layout.similar_movies_layout, linearLayoutSimilarMovies, false);
                linearLayoutSimilarMovies.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DETAIL_INTENT_STRING, id);
                        getApplicationContext().startActivity(intent);
                    }
                });
                Picasso.get()
                        .load(URLS.IMAGE_BASE_URL + object.getString(DATA_POSTER_PATH))
                        .into((ImageView) v);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading = false;
    }

}


