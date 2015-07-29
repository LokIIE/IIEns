package com.iiens.net;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * NewsItemsAdapter
 * Classe permettant d'adapter les news pour l'affichage
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

class NewsItemsAdapter extends BaseAdapter {

    private final List<NewsItem> newsItemsList;
    private final Context context;

    public NewsItemsAdapter(Context context, ArrayList<NewsItem> getNews) {
        this.newsItemsList = getNews;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newsItemsList.size();
    }

    @Override
    public NewsItem getItem(int arg0) {
        return newsItemsList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.news_item, parent, false);
        }

        TextView newsTitle = (TextView) convertView.findViewById(R.id.news_title);
        TextView newsDescription = (TextView) convertView.findViewById(R.id.news_description);

        NewsItem newsItem = newsItemsList.get(position);

        if (newsItem.getTitle().length() > 0) newsTitle.setText(newsItem.getTitle());
        if (newsItem.getDescription().length() > 0)
            newsDescription.setText(Html.fromHtml(newsItem.getDescription()));
        int logoId = context.getResources().getIdentifier(newsItem.getAuthor(), "drawable", "com.iiens.net");
        if (logoId != 0) newsTitle.setCompoundDrawablesWithIntrinsicBounds(logoId, 0, 0, 0);

        return convertView;
    }
}