package tn.esprit.spotup.fragments.salsabil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.AppDatabase;
import tn.esprit.spotup.entities.Signal;

public class AddSignal extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signal);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "spotup_database")
                .allowMainThreadQueries()  // For testing only
                .build();

        Button btnAddSignal = findViewById(R.id.button_report);
        btnAddSignal.setOnClickListener(v -> insertSignal());
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(AddSignal.this, AffichageSignal.class);
            startActivity(intent);
        });
    }

    private void insertSignal() {
        EditText titleField = findViewById(R.id.profilname);
        EditText descField = findViewById(R.id.cause);

        Signal signal = new Signal();
        signal.nameprofile = titleField.getText().toString();
        signal.cause = descField.getText().toString();

        try {
            db.SignalDao().insert(signal);
            showToast(getString(R.string.signal_added_message));

            // Send email notification
            EmailSender emailSender = new EmailSender();
            String subject = "New Signal Added";
            String body = "A new signal has been added:\n" +
                    "Profile Name: " + signal.nameprofile + "\n" +
                    "Cause: " + signal.cause;
            emailSender.sendEmail("salsabil.zaabar@esprit.tn", subject, body); // Corrected line

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AddSignal", "Error inserting signal", e);
            showToast(getString(R.string.error_occurred_message));
        }

        clearFields(titleField, descField);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
    }

}
