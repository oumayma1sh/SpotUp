package tn.esprit.spotup.entities;


import android.util.Log;

import java.io.Serializable;

public class Event implements Serializable {
    private String _id;
    private String title;
    private String dateDebut;
    private String dateFin;
    private String location;
    private String prix;
    private byte[] image;
    private String category;
    private String description;

    public Event() {
    }

    public Event( String title, String description, String dateDebut, String dateFin, String location, byte[] image, String prix) {
        this.title = title;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.location = location;
        this.image = image;
        this.prix = prix;
        this.description = description;
    }
    public Event(String _id, String category, String title, String description, String dateDebut, String dateFin, String location, byte[] image, String prix) {
        this._id = _id;
        this.title = title;
        this.category=category;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.location = location;
        this.image = image;
        this.prix = prix;

        Log.d("Event", "ID: " + _id);
        Log.d("Event", "Title: " + title);
        // Ajoutez d'autres logs pour vérifier les valeurs
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "_id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", dateDebut='" + dateDebut + '\'' +
                ", dateFin='" + dateFin + '\'' +
                ", location='" + location + '\'' +
                ", prix='" + prix + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

