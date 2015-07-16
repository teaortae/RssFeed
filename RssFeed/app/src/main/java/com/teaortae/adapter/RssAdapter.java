package com.teaortae.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.teaortae.main.MainActivity;
import com.teaortae.main.R;
import com.teaortae.model.RSSFeedData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tae on 2015. 5. 14..
 */
public class RssAdapter extends RecyclerView.Adapter<RssAdapter.RssViewHolder> {
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private List<RSSFeedData> rssList;

    public RssAdapter(Context context, int textViewResourceId, List<RSSFeedData> rssList) {
        this.rssList = rssList;
    }

    @Override
    public int getItemCount() {
        return rssList.size();
    }

    @Override
    public void onBindViewHolder(RssViewHolder rssViewHolder, int position) {
        RSSFeedData rssFeed = rssList.get(position);
        System.out.println(position);
        rssViewHolder.rssPubDate.setText(rssFeed.getPubDate());
        rssViewHolder.rssTitleView.setText(rssFeed.getTitle());
        rssViewHolder.rssDescription.setText(rssFeed.getDescription());
        if (rssFeed.getImageUrl().length() != 0) {
            rssViewHolder.rssImageView.setVisibility(View.VISIBLE);
            rssViewHolder.rssDescription.setVisibility(View.GONE);
        }
        if (rssFeed.getDescription().length() == 0) {
            rssViewHolder.rssDescription.setVisibility(View.GONE);
        }
        if (rssFeed.getImageUrl().length() == 0) {
            rssViewHolder.rssImageView.setVisibility(View.GONE);
            rssViewHolder.rssDescription.setVisibility(View.VISIBLE);
        } else {
            ImageLoader.getInstance().displayImage(rssFeed.getImageUrl(),
                    rssViewHolder.rssImageView, MainActivity.options, animateFirstListener);
        }
    }

    @Override
    public RssViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new RssViewHolder(itemView);
    }

    public static class RssViewHolder extends RecyclerView.ViewHolder {
        protected TextView rssTitleView;
        protected TextView rssPubDate;
        //  protected TextView rssCategory;
        protected TextView rssDescription;
        protected ImageView rssImageView;

        public RssViewHolder(View v) {
            super(v);
            rssTitleView = (TextView) v.findViewById(R.id.rssTitleView);
            rssPubDate = (TextView) v.findViewById(R.id.rssPubdate);
            //rssCategory = (TextView) v.findViewById(R.id.rssCategory);
            rssDescription = (TextView) v.findViewById(R.id.rssDescription);
            rssImageView = (ImageView) v.findViewById(R.id.rssImage);
        }
    }

    public static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        public static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
