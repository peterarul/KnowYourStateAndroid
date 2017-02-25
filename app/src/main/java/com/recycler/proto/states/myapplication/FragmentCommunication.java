package com.recycler.proto.states.myapplication;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by arulpeter on 2/16/17.
 */
public interface FragmentCommunication {
    void respond(int position, FeedItem feedItem);
}
