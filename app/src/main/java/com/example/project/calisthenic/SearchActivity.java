package com.example.project.calisthenic;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.example.project.adapters.TrackAdapter;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

/**
 * Created by saleventa7 on 5/26/2016.
 */
public class SearchActivity extends AppCompatActivity {

    private TwoWayView recyclerView;
    private ProgressWheel wheel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (TwoWayView) findViewById(R.id.list);
        final Drawable divider = getResources().getDrawable(R.drawable.divider);
        recyclerView.addItemDecoration(new DividerItemDecoration(divider));
        wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search artist...");
        MenuItemCompat.expandActionView(menu.findItem(R.id.item_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    Utils.hideKeyboard(SearchActivity.this);
                    new SearchSongsAsync().execute(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;

    }

    private class SearchSongsAsync extends AsyncTask<String, Void, Track[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected Track[] doInBackground(String... params) {
            return NetworkUtils.searchTracks(params[0]);
        }

        @Override
        protected void onPostExecute(Track[] tracks) {
            super.onPostExecute(tracks);
            wheel.setVisibility(View.GONE);
            if (tracks != null) {
                if (tracks.length > 0) {
                    for (int i = 0; i < tracks.length; i++)
                        Log.d("TRACK", tracks[i].user.username + ": " + tracks[i].title);

                    TrackAdapter adapter = new TrackAdapter(SearchActivity.this, tracks);
                    recyclerView.setAdapter(adapter);
                }

                else {

                }
            }
            else {

            }
        }
    }
}
