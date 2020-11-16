package fr.yncrea.scorpion.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.yncrea.scorpion.model.Planning;

@Dao
public interface ScorpionPlanningDao {
    @Query("select * from Planning where id = :id")
    Planning getPlanningById(int id);

    @Insert
    void insertPlanning(Planning planning);

    @Update
    void updatePlanning(Planning planning);
}
