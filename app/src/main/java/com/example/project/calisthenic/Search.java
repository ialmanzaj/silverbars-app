package com.example.project.calisthenic;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;

public class Search extends Fragment {

    View rootView;

    SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_search, container, false);
//        mSearchView = (SearchView)getActivity().findViewById(R.id.floating_search_view);
//        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
//            @Override
//            public void onSearchTextChanged(String oldQuery, final String newQuery) {
//
//                //get suggestions based on newQuery
//
//                //pass them on to the search view
//                mSearchView.swapSuggestions(newSuggestions);
//            }
//        });
        return rootView;
    }


}
