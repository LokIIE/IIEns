package com.iiens.net.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.iiens.net.model.AnnivItem;

import java.util.List;

@Dao
public interface AnnivDao {

    @Query("SELECT * FROM anniversaires")
    List<AnnivItem> getAll();

    @Query("SELECT * FROM anniversaires ORDER BY date")
    AnnivItem getFirst();

    @Query("SELECT id FROM anniversaires WHERE nom = :nom AND prenom = :prenom AND pseudo = :pseudo")
    int findId( String nom, String prenom, String pseudo );

    @Insert
    void insert(AnnivItem anniv);

    @Insert
    void insertAll(AnnivItem... anniv);

    @Delete
    void delete(AnnivItem anniv);

    @Query("DELETE FROM anniversaires")
    void deleteAll();
}
