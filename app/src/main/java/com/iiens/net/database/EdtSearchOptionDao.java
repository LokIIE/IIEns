package com.iiens.net.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.iiens.net.model.EdtSearchOption;

import java.util.List;

@Dao
public interface EdtSearchOptionDao {

    @Query("SELECT * FROM edtSearchOptions ORDER BY id DESC LIMIT 10")
    List<EdtSearchOption> getAll ();

    @Insert
    void insert ( EdtSearchOption option );

    @Insert
    void insertAll ( List<EdtSearchOption> options );

    @Delete
    void delete ( EdtSearchOption category );

    @Query("DELETE FROM edtSearchOptions")
    void deleteAll ();
}
