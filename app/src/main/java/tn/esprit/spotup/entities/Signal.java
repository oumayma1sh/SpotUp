package tn.esprit.spotup.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Signal implements Serializable {  // Implement Serializable
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nameprofile;
    public String cause;

}
