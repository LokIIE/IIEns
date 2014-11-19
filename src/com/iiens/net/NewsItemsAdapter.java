package com.iiens.net;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/** NewsItemsAdapter
	Classe permettant d'adapter les news pour l'affichage
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class NewsItemsAdapter extends BaseAdapter {

	private List<NewsItem> newsItemsList;
	private Context context;

	public NewsItemsAdapter(Context context, ArrayList<NewsItem> getNews, int number) {
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if(arg1==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.news_item, arg2, false);
		}

		TextView newsTitle = (TextView) arg1.findViewById(R.id.rss_header);
		TextView newsDescription = (TextView) arg1.findViewById(R.id.rss_description);

		NewsItem newsItem = newsItemsList.get(arg0);

		newsTitle.setText(newsItem.getTitle());
		newsDescription.setText(Html.fromHtml(newsItem.getDescription()));
		newsTitle.setCompoundDrawablesWithIntrinsicBounds(
				context.getResources().getIdentifier(
						newsItem.getAuthor(), "drawable", "com.iiens.net"
						) , 0, 0, 0);

		return arg1;
	}
}