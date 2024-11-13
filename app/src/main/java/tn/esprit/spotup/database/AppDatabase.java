package tn.esprit.spotup.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.spotup.dao.SignalDao;
import tn.esprit.spotup.dao.UserDao;
import tn.esprit.spotup.entities.Signal;
import tn.esprit.spotup.entities.User;
import tn.esprit.spotup.utils.Converters;

@Database(entities = {User.class, Signal.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract SignalDao SignalDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "spotup_database")
                            .fallbackToDestructiveMigration()  // Utilisation de cette méthode pour réinitialiser la base
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


//    public static AppDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (AppDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                                    AppDatabase.class, "spotup_database")
//                            .allowMainThreadQueries()
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }

}
