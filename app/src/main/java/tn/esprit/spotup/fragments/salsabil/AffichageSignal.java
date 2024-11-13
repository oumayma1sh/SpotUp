package tn.esprit.spotup.fragments.salsabil;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.Signal;


public class AffichageSignal extends AppCompatActivity {

    private AppDatabase db;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_signal);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "spotup_database")
                .allowMainThreadQueries()  // Pour les tests uniquement
                .build();

        tableLayout = findViewById(R.id.table_layout);
        loadSignals();
    }

    private void loadSignals() {
        List<Signal> signals = db.SignalDao().getAllSignals();
        for (Signal signal : signals) {
            addSignalRow(signal);
        }
    }

    private void addSignalRow(Signal signal) {
        TableRow tableRow = new TableRow(this);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(signal.nameprofile);
        nameTextView.setPadding(8, 8, 8, 8);
        tableRow.addView(nameTextView);

        TextView causeTextView = new TextView(this);
        causeTextView.setText(signal.cause);
        causeTextView.setPadding(8, 8, 8, 8);
        tableRow.addView(causeTextView);

        // Replace the standard Button with FloatingActionButton
        FloatingActionButton deleteButton = new FloatingActionButton(this);
        deleteButton.setImageResource(android.R.drawable.ic_delete); // Use delete icon
        deleteButton.setOnClickListener(v -> {
            db.SignalDao().delete(signal);
            tableLayout.removeView(tableRow);  // Supprime la ligne du tableau
            Toast.makeText(this, "Signal supprimé", Toast.LENGTH_SHORT).show();
        });

        // Set layout parameters for the FloatingActionButton
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8); // Optional: Set margins
        deleteButton.setLayoutParams(params);

        tableRow.addView(deleteButton);
        tableLayout.addView(tableRow);
    }

    public void refreshList() {
        // Clear existing rows from the TableLayout, keeping the header row
        tableLayout.removeViewsInLayout(1, tableLayout.getChildCount() - 1); // Keep the header row

        // Load the updated signals from the database
        List<Signal> signals = db.SignalDao().getAllSignals();

        // Add each signal to the TableLayout
        for (Signal signal : signals) {
            addSignalRow(signal);
        }
    }
}
