package fr.yncrea.scorpion.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Planning {
    @PrimaryKey(autoGenerate = false)
    public long id;

    public String planningString;

    @Override
    public String toString() {
        return planningString;
    }
}
