package com.recycler.proto.states.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ListItemFragment extends Fragment implements ObservableScrollViewCallbacks {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private List<FeedItem> feedsList;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private static final String TAG = "RecyclerViewExample";



    public ListItemFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_item, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        String url = "http://bellagazzi.com/statesupdate1.json";
        new ListItemFragment.DownloadTask().execute(url);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        //mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(getActivity(), feedsList, communication);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).setSupportActionBar((Toolbar) getView().findViewById(R.id.toolbar));


        mToolbarView = getView().findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(getActivity(),R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) getView().findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);



    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mScrollView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = ContextCompat.getColor(getActivity(),R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mRecyclerView, scrollY / 50 );

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }


    private class DownloadTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);
            if (result == 1) {
                mAdapter = new MyAdapter(getActivity(), feedsList, communication);
                mRecyclerView.setAdapter(mAdapter);
                Toast.makeText(getActivity(), "Success! Fetched data!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void parseResult(String result) {
        feedsList = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("states");

            for (int i = 0; i < posts.length(); i++) {
                JSONObject obj = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setDate(obj.optString("Date"));
                item.setAbbreviation(obj.optString("abbreviation"));
                item.setCapital(obj.optString("capital"));
                item.setFlagurl(obj.optString("flagurls"));
                item.setGeolat(obj.optString("lat"));
                item.setGeolong(obj.optString("long"));
                item.setImagelocation(obj.getJSONObject("image")
                        .optString("location"));
                item.setImageurl(obj.getJSONObject("image")
                        .getJSONObject("credit").optString("url"));
                item.setImageauthor(obj.getJSONObject("image")
                        .getJSONObject("credit").optString("author"));
                item.setNickname(obj.optString("nickname"));
                item.setPopulation(obj.optInt("population"));
                item.setStatename(obj.optString("name"));
                item.setYear(obj.optInt("year"));
                item.setPicurl(obj.optString("picurl"));

                feedsList.add(item);
            }
        } catch (JSONException e) {
            Log.i("JSON: ", feedsList.toString());
            e.printStackTrace();
        }
    }

    FragmentCommunication communication = new FragmentCommunication() {

        @Override
        public void respond(int position, FeedItem feedItem) {
            DetailsItemFragment detailsItemFragment = new DetailsItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", feedItem.getStatename());
            bundle.putString("nickname", feedItem.getNickname());
            bundle.putString("date", feedItem.getDate());
            bundle.putString("capital", feedItem.getCapital());
            bundle.putString("abbreviation", feedItem.getAbbreviation());
            bundle.putString("lat", feedItem.getGeolat());
            bundle.putString("long", feedItem.getGeolong());
            bundle.putString("flagurls", feedItem.getNickname());
            bundle.putInt("population", feedItem.getPopulation());
            bundle.putInt("year", feedItem.getYear());
            bundle.putString("location", feedItem.getImagelocation());
            bundle.putString("imageurl", feedItem.getImageurl());
            bundle.putString("imageauthor", feedItem.getImageauthor());
            bundle.putString("picurl", feedItem.getPicurl());


           Log.d(TAG, "respond: "+feedItem.getPicurl());

            detailsItemFragment.setArguments(bundle);
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.list_main, detailsItemFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };
}

