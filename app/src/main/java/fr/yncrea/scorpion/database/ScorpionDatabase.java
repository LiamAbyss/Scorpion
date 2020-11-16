package fr.yncrea.scorpion.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fr.yncrea.scorpion.dao.ScorpionPlanningDao;
import fr.yncrea.scorpion.model.Planning;

@Database(entities = {Planning.class}, version = 1)
public abstract class ScorpionDatabase extends RoomDatabase {
    public abstract ScorpionPlanningDao aurionPlanningDao();
}
