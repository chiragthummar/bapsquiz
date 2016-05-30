package com.bittechnologies.bapsquiz.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bittechnologies.bapsquiz.NewsDetail;
import com.bittechnologies.bapsquiz.R;
import com.bittechnologies.bapsquiz.model.FeedItem;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by chira on 1/30/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.FeedListRowHolder> {


    private List<FeedItem> feedItemList;

    private Context mContext;

    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public FeedListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, null);
        FeedListRowHolder mh = new FeedListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(FeedListRowHolder feedListRowHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        Picasso.with(mContext).load(feedItem.getPhoto())
                .error(R.drawable.baps_logo)
                .placeholder(R.drawable.baps_logo)
                .into(feedListRowHolder.photo);

        feedListRowHolder.title.setText(Html.fromHtml(feedItem.getTitle()));
        feedListRowHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent=new Intent(mContext,NewsDetail.class);
                intent.putExtra("id",position);
                mContext.startActivity(intent);
            }
        });
     //   feedListRowHolder.subtext.setText(Html.fromHtml(feedItem.getSubtext()));

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }



    public class FeedListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected ImageView photo;
        protected TextView title,subtext;
        private ItemClickListener clickListener;
        public FeedListRowHolder(View view) {
            super(view);
            this.photo = (ImageView) view.findViewById(R.id.news_img);
            this.title = (TextView) view.findViewById(R.id.new_maintxt);
            view.setOnClickListener(this);
           // this.subtext = (TextView) view.findViewById(R.id.news_subtxt);
        }
        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View v) {
                clickListener.onClick(v, getPosition(), false);
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

}

