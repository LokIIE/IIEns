package com.iiens.net;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class NewsItem {
	private String title;
	private String description;
	private String author;
	private String date;

	public String getTitle() {return title;}
	public String getDescription() {return description;}
	public String getAuthor() {return author;}
	public String getDate() {return date;}

	public void setTitle(String title) {this.title = title;}
	public void setDescription(String description) {this.description = description;}
	public void setAuthor(String author) {this.author = author;}
	public void setDate(String date) {this.date = date;}
	
	public NewsItem(String title, String description, String author, String date){
		this.title = title;
		this.description = description;
		this.author = author;
		this.date = date;
	}
	
	public NewsItem() {}
	
	public void mapJsonObject(JSONObject json_data) {
		try {
			setTitle(json_data.getString("titre"));
			String contenu = json_data.getString("contenu");
			setDescription(formatHtml(contenu));
			setAuthor(json_data.getString("par"));
			setDate(json_data.getString("calDate"));
		} catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
	}
	
	private String formatHtml(String contenu){
		String formatContenu = contenu;
		
		formatContenu = formatContenu.replaceAll(" :bold:", " <b>");
		formatContenu = formatContenu.replace(":bold:", "</b>");
		formatContenu = formatContenu.replaceAll(":underlined:", "");
		
		return formatContenu;
	}
	
	public ArrayList<String> toArrayList() {
		ArrayList<String> result = new ArrayList<String>();
		
		result.add(title);
		result.add(description);
		result.add(author);
		result.add(date);
		
		return result;
	}
}