package com.iiens.net.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.iiens.net.model.EdtSearchCategory;
import com.iiens.net.model.EdtSearchOption;

import java.util.List;

@Dao
public interface EdtSearchCategoryDao {

    @Query("SELECT * FROM edtSearchCategories ORDER BY id DESC LIMIT 10")
    List<EdtSearchCategory> getAll();

    @Query("SELECT * FROM edtSearchCategories ORDER BY id DESC LIMIT 1")
    EdtSearchCategory getFirst();

    @Query("SELECT * FROM edtSearchCategories WHERE value = :value" )
    EdtSearchCategory getByValue(String value);

    @Insert
    void insert(EdtSearchCategory category);

    @Insert
    void insertAll(EdtSearchCategory... category);

    @Insert
    void insertWithOption(EdtSearchCategory category, List<EdtSearchOption> options);

    @Delete
    void delete(EdtSearchCategory category);

    @Query("DELETE FROM edtSearchCategories")
    void deleteAll();
}
