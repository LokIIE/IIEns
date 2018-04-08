package com.iiens.net.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.iiens.net.model.NewsItem;

import java.util.List;

@Dao
public interface NewsDao {

    @Query("SELECT * FROM news ORDER BY id DESC LIMIT 10")
    List<NewsItem> getAll();

    @Query("SELECT * FROM news ORDER BY id DESC LIMIT 1")
    NewsItem getFirst();

    @Insert
    void insert(NewsItem news);

    @Insert
    void insertAll(NewsItem... news);

    @Delete
    void delete(NewsItem news);

    @Query("DELETE FROM news")
    void deleteAll();

}
