package com.recycler.proto.states.myapplication;



import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsItemFragment extends Fragment implements ObservableScrollViewCallbacks {
    private ImageView mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    private String mNickname;
    private String mDate;
    private int mYear;
    private int mPopulation;
    private String mCapital;
    private String mGeolat;
    private String mGeolong;
    private String mStatename;
    private String mAbbreviation;
    private String mImagelocation;
    private String mImageauthor;
    private String mImageurl;

    private String wikiApiUrl;
    private String mDescription;


    TextView sname;
    TextView nname;
    TextView capital;
    TextView geolat;
    TextView geolong;
    TextView abbreviation;
    TextView imagelocation;
    TextView imageauthor;
    TextView population;
    TextView description;






    public DetailsItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatename=getArguments().getString("name");
        mNickname= getArguments().getString("nickname");
        mImageurl = getArguments().getString("picurl");
        mImageauthor = getArguments().getString("imageauthor");
        mImagelocation = getArguments().getString("location");
        mDate = getArguments().getString("date");
        mYear = getArguments().getInt("year");
        mCapital = getArguments().getString("capital");
        mAbbreviation = getArguments().getString("abbreviation");
        mGeolat = getArguments().getString("lat");
        mGeolong = getArguments().getString("long");
        mPopulation = getArguments().getInt("population");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            JSONObject json = wikiJsonObj(wikiDescription());
            String pageIds = json.getJSONObject("query").getJSONArray("pageids").get(0).toString();
           // pageIds = '"'+pageIds+'"';
            mDescription = json.getJSONObject("query").getJSONObject("pages").getJSONObject(pageIds).getString("extract");
            Log.d("JSON DATA", "onCreate: "+mDescription);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String wikiDescription(){
        wikiApiUrl = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&indexpageids=false&exlimit=max&explaintext&exintro&titles=";
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(mStatename);
        boolean whiteSpaceFound = matcher.find();
        if(whiteSpaceFound) {
            mStatename = mStatename.replaceAll("\\s","_");
        }
        String wikiDescriptionUrl = wikiApiUrl+mStatename;

        return wikiDescriptionUrl;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    private JSONObject wikiJsonObj(String url)throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details_item, container, false);
        sname= (TextView) v.findViewById(R.id.name);
        sname.setText(mStatename+"\n"+mNickname);
        nname= (TextView) v.findViewById(R.id.snname);
        nname.setText("Estd: "+mDate+", "+mYear);
        imageauthor= (TextView) v.findViewById(R.id.author);
        imageauthor.setText("Author: "+mImageauthor);
        imagelocation= (TextView) v.findViewById(R.id.location);
        imagelocation.setText("Location: "+mImagelocation);
//        date = (TextView) v.findViewById(R.id.date);
//        date.setText("Established: "+mDate+", "+mYear);
        capital= (TextView) v.findViewById(R.id.capital);
        capital.setText("Capital\n"+mCapital);
        abbreviation = (TextView) v.findViewById(R.id.abb);
        abbreviation.setText("Abbreviation\n"+mAbbreviation);
        geolat = (TextView) v.findViewById(R.id.glat);
        geolat.setText("Lat\n"+mGeolat);
        geolong = (TextView) v.findViewById(R.id.glong);
        geolong.setText("Long\n"+mGeolong);
        population = (TextView) v.findViewById(R.id.population);
        population.setText("Population\n"+mPopulation);
        description = (TextView) v.findViewById(R.id.description);
        description.setText(mDescription);




        mImageView = (ImageView) v.findViewById(R.id.image);

        //Render image using Picasso library
        if (!TextUtils.isEmpty(mImageurl)) {
            Picasso.with(v.getContext())
                    .load(mImageurl)
                    .fit()
                    .centerInside()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(mImageView);
        }

        if(savedInstanceState == null) {
        }
        else   {
            onRestoreInstanceState(savedInstanceState);
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).setSupportActionBar((Toolbar) getView().findViewById(R.id.toolbar));

        //mImageView.setImageResource(R.id.image);
        //mImageView = (ImageView)findViewById(R.id.image);
        mToolbarView = getView().findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(getActivity(),R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) getView().findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mScrollView.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = ContextCompat.getColor(getActivity(),R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
