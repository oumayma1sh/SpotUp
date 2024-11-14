package tn.esprit.spotup.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tn.esprit.spotup.dao.SignalDao;
import tn.esprit.spotup.dao.UserDao;
import tn.esprit.spotup.entities.Comment;
import tn.esprit.spotup.entities.Event;

public class DataBaseHelper extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "SpotUpp.db";
    private static final int DATABASE_VERSION = 2;


//    table comments
    private static final String TABLE_COMMENTS = "comments";
    private static final String COL_ID = "id";
    private static final String COL_COMMENT = "comment";
    private static final String COL_DATE = "date";
    private static final String COL_LIKES = "likes";
    private static final String COL_DISLIKES = "dislikes";


//    Plans Table
    public static final String TABLE_NAME = "plans";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGE = "image";  // Chemin de l'image
    public static final String COLUMN_OPENING_TIME = "opening_time";
    public static final String COLUMN_CLOSING_TIME = "closing_time";

//   Events table
    public static final String TABLE_EVENTS = "events";
    public static final String CLN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE_DEBUT = "dateDebut";
    public static final String CLN_DESCRIPTION = "description";
    public static final String COLUMN_DATE_FIN = "dateFin";
    public static final String CLN_IMAGE = "image";
    public static final String CLN_LOCATION = "location";
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String COLUMN_PRIX = "prix";
    public static final String COLUMN_CATEGORIE = "categorie";

//   Table participants
    public static final String TABLE_PARTICIPANTS = "participants";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PARTICIPATION_DATE = "participation_date";



    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Créer la table des commentaires
        String createCommentsTable = "CREATE TABLE " + TABLE_COMMENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMMENT + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LIKES + " INTEGER DEFAULT 0, " +
                COL_DISLIKES + " INTEGER DEFAULT 0)";
        db.execSQL(createCommentsTable);

        // Créer la table des bons plans
        String createPlansTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_OPENING_TIME + " TEXT, " +
                COLUMN_CLOSING_TIME + " TEXT)";
        db.execSQL(createPlansTable);

         String createEventsTable = "CREATE TABLE " + TABLE_EVENTS +  " (" +
                        CLN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        CLN_DESCRIPTION + " TEXT, " +
                        COLUMN_DATE_DEBUT + " TEXT, " +
                        COLUMN_DATE_FIN + " TEXT, " +
                        CLN_LOCATION + " TEXT, " +
                        CLN_IMAGE + " BLOB, " +
                        COLUMN_PRIX + " TEXT, " +
                        COLUMN_CATEGORIE + " TEXT);";
        db.execSQL(createEventsTable);

        String createParticipantsTable = "CREATE TABLE " + TABLE_PARTICIPANTS + " (" +
                        COLUMN_EVENT_ID + " INTEGER, " +  // Foreign key referencing events table's ID
                        COLUMN_USER_ID + " INTEGER, " +
                        COLUMN_PARTICIPATION_DATE + " TEXT, " +
                        "PRIMARY KEY (" + COLUMN_EVENT_ID + ", " + COLUMN_USER_ID + "), " +
                        "FOREIGN KEY (" + COLUMN_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + CLN_ID + ") " +
                        "ON DELETE CASCADE);";
        db.execSQL(createParticipantsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

//    Comments Methods
// Ajouter un commentaire
public void addComment(Comment comment) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COL_COMMENT, comment.getTexte());
    values.put(COL_DATE, comment.getDate());
    values.put(COL_LIKES, comment.getLikeCount());
    values.put(COL_DISLIKES, comment.getDislikeCount());
    db.insert(TABLE_COMMENTS, null, values);
}

    // Récupérer tous les commentaires
    public ArrayList<Comment> getAllComments() {
        ArrayList<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMMENTS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                String texte = cursor.getString(cursor.getColumnIndex(COL_COMMENT));
                String date = cursor.getString(cursor.getColumnIndex(COL_DATE));
                int likes = cursor.getInt(cursor.getColumnIndex(COL_LIKES));
                int dislikes = cursor.getInt(cursor.getColumnIndex(COL_DISLIKES));
                comments.add(new Comment(id, texte, date, likes, dislikes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return comments;
    }

    public void updateComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMMENT, comment.getTexte());
        values.put(COL_LIKES, comment.getLikeCount());
        values.put(COL_DISLIKES, comment.getDislikeCount());

        int rowsAffected = db.update(TABLE_COMMENTS, values, COL_ID + "=?", new String[]{String.valueOf(comment.getId())});

        // Debug: afficher le nombre de lignes affectées
        if (rowsAffected > 0) {
            System.out.println("Mise à jour réussie de " + rowsAffected + " lignes.");
        } else {
            System.out.println("Erreur : aucune mise à jour effectuée.");
        }
    }
    // Supprimer un commentaire
    public void deleteComment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMMENTS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }


//   Plans Methods
    public void updatePlan(String originalPlanName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update("plans", values, "name = ?", new String[]{originalPlanName});
        db.close();
    }


//      Events methods

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public int getParticipantCountByEventTitle(String eventTitle) {
        int participantCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(p." + COLUMN_USER_ID + ") AS participantCount " +
                "FROM " + TABLE_PARTICIPANTS + " p " +
                "JOIN " + TABLE_EVENTS + " e ON p." + COLUMN_EVENT_ID + " = e." + CLN_ID + " " +
                "WHERE e." + COLUMN_TITLE + " = ? " +
                "GROUP BY e." + COLUMN_TITLE;

        Cursor cursor = db.rawQuery(query, new String[]{eventTitle});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                participantCount = cursor.getInt(cursor.getColumnIndexOrThrow("participantCount"));
            }
            cursor.close();
        }

        db.close();
        return participantCount;
    }


    public boolean addParticipant(String eventTitle, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Rechercher l'ID de l'événement en fonction de son titre
        String query = "SELECT " + CLN_ID + " FROM " + TABLE_EVENTS + " WHERE " + COLUMN_TITLE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{eventTitle});

        if (cursor.moveToFirst()) {
            int eventId = cursor.getInt(cursor.getColumnIndexOrThrow(CLN_ID));
            cursor.close();

            // Vérifier si le participant est déjà enregistré pour cet événement
            query = "SELECT COUNT(*) FROM " + TABLE_PARTICIPANTS + " WHERE " + COLUMN_EVENT_ID + " = ? AND " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(eventId), String.valueOf(userId)});

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                cursor.close();
                return false; // Participant déjà enregistré pour cet événement
            }
            cursor.close();

            // Ajouter le participant si pas encore inscrit
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVENT_ID, eventId);
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_PARTICIPATION_DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));

            long result = db.insert(TABLE_PARTICIPANTS, null, values);
            db.close();

            return result != -1; // Retourne true si l'insertion a réussi
        } else {
            cursor.close();
            db.close();
            return false; // Événement introuvable
        }
    }



    public long addEvent(String title, String description, String dateDebut, String dateFin, String selectedLocation, byte[] imageBytes, String prix,String selectedCategorie) {
        SQLiteDatabase db = this.getWritableDatabase(); // Ouvre la base de données

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(CLN_DESCRIPTION, description);
        values.put(COLUMN_DATE_DEBUT, dateDebut);
        values.put(COLUMN_DATE_FIN, dateFin);
        values.put(CLN_LOCATION, selectedLocation);
        values.put(COLUMN_PRIX, prix);
        values.put(COLUMN_CATEGORIE, selectedCategorie);

        if (imageBytes != null) {
            values.put(CLN_IMAGE, imageBytes);
        } else {
            values.putNull(CLN_IMAGE);
        }

        long result = db.insert(TABLE_EVENTS, null, values);
        db.close(); // Fermez la base de données après l’opération

        return result;
    }



    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(CLN_DESCRIPTION));
                String dateDebut = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_DEBUT));
                String dateFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_FIN));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(CLN_LOCATION));
                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(CLN_IMAGE));
                String prix = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIX));

                Event event = new Event(title, description, dateDebut, dateFin, location, imageBlob, prix);
                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return events;
    }



    public List<Event> getUpcomingEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
            cursor = db.rawQuery(selectQuery, null);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Calendar currentDate = Calendar.getInstance();

            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(CLN_DESCRIPTION));
                    String dateDebut = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_DEBUT));
                    String dateFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_FIN));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(CLN_LOCATION));
                    byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(CLN_IMAGE));
                    String prix = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIX));

                    Date eventDateDebut = sdf.parse(dateDebut);
                    Calendar eventCalendar = Calendar.getInstance();
                    eventCalendar.setTime(eventDateDebut);

                    long diffInMillies = eventCalendar.getTimeInMillis() - currentDate.getTimeInMillis();
                    long daysDifference = diffInMillies / (1000 * 60 * 60 * 24);

                    if (daysDifference >= 0 && daysDifference <= 7) {
                        Event event = new Event(title, description, dateDebut, dateFin, location, imageBlob, prix);
                        events.add(event);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database", "Error fetching events", e);
        } finally {
            if (cursor != null) cursor.close();

        }

        return events;
    }



    public boolean updateDataEvent(String title, String description, String dateDebut, String dateFin, String location, byte[] image, String prix,String selectedcategorie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Assurez-vous que ces colonnes correspondent à celles de votre table d'événements
        cv.put(COLUMN_TITLE, title); // Mettez à jour le titre
        cv.put(CLN_DESCRIPTION, description);
        cv.put(COLUMN_DATE_DEBUT, dateDebut);
        cv.put(COLUMN_DATE_FIN, dateFin);
        cv.put(CLN_LOCATION, location);
        //  cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_PRIX, prix);
        cv.put(COLUMN_CATEGORIE, selectedcategorie);

        try {
            // Utilisez le titre de l'événement passé en argument pour le mettre à jour
            db.update(TABLE_EVENTS, cv, COLUMN_TITLE + "=?", new String[]{title});


        } catch (Exception e) {
            Log.e("DatabaseError", "Error updating event: " + e.getMessage());
        } finally {
            db.close();
            return true;// Fermez la base de données pour éviter les fuites de mémoire
        }
    }


    public void deleteEventByTitle(String title) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Log.d("Database", "Tentative de suppression de l'événement avec le titre : " + title);

        // Vérifier l'existence de l'événement avant de supprimer
        Cursor cursor = DB.rawQuery("SELECT * FROM events WHERE title = ?", new String[]{title});

        if (cursor.moveToFirst()) { // Vérifie si l'événement existe
            Log.d("Database", "Événement trouvé avec le titre : " + title);

            // Tentative de suppression
            int result = DB.delete("events", "title = ?", new String[]{title});

            if (result > 0) {
                Log.d("Database", "Événement supprimé avec succès : " + title);
            } else {
                Log.d("Database", "Échec de la suppression de l'événement avec le titre : " + title);
            }
        } else {
            Log.d("Database", "Aucun événement trouvé avec le titre : " + title);
        }

        // Fermer le curseur et la base de données pour éviter les fuites de mémoire
        cursor.close();
        DB.close();
    }

    public List<Event> getEventsByCategory(String category) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery;
            if ("Tous".equals(category)) {
                // Fetch all events if "Tous" (All) is selected
                selectQuery = "SELECT * FROM " + TABLE_EVENTS;
                cursor = db.rawQuery(selectQuery, null);
            } else {
                // Fetch events of a specific category
                selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_CATEGORIE + " = ?";
                cursor = db.rawQuery(selectQuery, new String[]{category});
            }

            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(CLN_DESCRIPTION));
                    String dateDebut = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_DEBUT));
                    String dateFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_FIN));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(CLN_LOCATION));
                    byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(CLN_IMAGE));
                    String prix = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIX));

                    Event event = new Event(title, description, dateDebut, dateFin, location, imageBlob, prix);
                    events.add(event);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return events;
    }
    public boolean hasUserParticipated(String eventTitle, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PARTICIPANTS + " WHERE " + COLUMN_EVENT_ID + " = (SELECT " + CLN_ID + " FROM " + TABLE_EVENTS + " WHERE " + COLUMN_TITLE + " = ?) AND " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{eventTitle, String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            boolean hasParticipated = cursor.getInt(0) > 0;
            cursor.close();
            return hasParticipated;
        }
        return false;
    }





}
