package tn.esprit.spotup.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;



import java.util.List;

import tn.esprit.spotup.entities.Signal;

@Dao
public interface SignalDao {
    @Insert
    void insert(Signal Signal);

    @Update
    void update(Signal Signal);

    @Delete
    void delete(Signal Signal);

    @Query("SELECT * FROM Signal")
    List<Signal> getAllSignals();

    @Query("SELECT * FROM Signal WHERE id = :id")
    Signal getSignalById(int id);
}
