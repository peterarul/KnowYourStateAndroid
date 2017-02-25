package com.recycler.proto.states.myapplication;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList = Collections.emptyList();
    private Context mContext;
    private FragmentCommunication mCommunicator;


    public MyAdapter(Context context, List<FeedItem> feedItemList,FragmentCommunication mCommunicator) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.mCommunicator = mCommunicator;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_card, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view,mCommunicator);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        customViewHolder.textViewName.setText(feedItem.getStatename());
        customViewHolder.textViewNickName.setText(feedItem.getNickname());
        //customViewHolder.imageView.setImageResource(feedItem.iconId);

        //Render image using Picasso library
        if (!TextUtils.isEmpty(feedItem.getFlagurl())) {
            Picasso.with(mContext).load(feedItem.getFlagurl())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }


    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected ImageView imageView;
        protected TextView textViewName;
        protected TextView textViewNickName;
        FragmentCommunication mComminication;

        public CustomViewHolder(View view, FragmentCommunication communicator)  {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textViewName = (TextView) view.findViewById(R.id.title);
            this.textViewNickName = (TextView) view.findViewById(R.id.nickname);
            mComminication=communicator;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //mComminication.respond(getAdapterPosition(),feedItemList.get(getAdapterPosition()).getStatename(),feedItemList.get(getAdapterPosition()).getNickname());
            mComminication.respond(getAdapterPosition(),feedItemList.get(getAdapterPosition()));

        }


    }
}