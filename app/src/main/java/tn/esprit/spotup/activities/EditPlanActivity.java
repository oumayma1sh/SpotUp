package tn.esprit.spotup.activities;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import tn.esprit.spotup.R;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import tn.esprit.spotup.database.DataBaseHelper;

public class EditPlanActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private Uri selectedImageUri; // Stocker l'URI sélectionné ici
    private DataBaseHelper dbHelper;
    private EditText editPlanName, editPlanDescription, editPlanLocation, editOpeningHours, editClosingHours;
    private String originalPlanName; // Pour localiser le plan dans la base de données
    private ImageView editPlanImageView; // Pour afficher l'image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan);

        // Initialiser la base de données
        dbHelper = new DataBaseHelper(this);

        // Récupérer les vues
        editPlanName = findViewById(R.id.editPlanName);
        editPlanDescription = findViewById(R.id.editPlanDescription);
        editPlanLocation = findViewById(R.id.editPlanLocation);
        editOpeningHours = findViewById(R.id.editOpeningHours);
        editClosingHours = findViewById(R.id.editClosingHours);
        editPlanImageView = findViewById(R.id.editPlanImageView);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button saveChangesButton = findViewById(R.id.saveChangesButton);

        // Récupérer les données du plan depuis l'intent
        Intent intent = getIntent();
        originalPlanName = intent.getStringExtra("planName");
        editPlanName.setText(originalPlanName);
        editPlanDescription.setText(intent.getStringExtra("planDescription"));
        editPlanLocation.setText(intent.getStringExtra("planLocation"));
        editOpeningHours.setText(intent.getStringExtra("openingTime"));
        editClosingHours.setText(intent.getStringExtra("closingTime"));

        // Récupérer l'URI de l'image depuis l'intent ou la base de données
        String imageUri = intent.getStringExtra("imageUri");
        if (imageUri != null) {
            editPlanImageView.setImageURI(Uri.parse(imageUri));
            selectedImageUri = Uri.parse(imageUri); // Sauvegarder l'URI récupéré pour la mise à jour
        }

        // Bouton pour sélectionner une nouvelle image
        selectImageButton.setOnClickListener(view -> openImagePicker());

        // Ouvrir le sélecteur d'heure pour les champs d'ouverture et de fermeture
        editOpeningHours.setOnClickListener(v -> showTimePicker(editOpeningHours));
        editClosingHours.setOnClickListener(v -> showTimePicker(editClosingHours));

        // Gérer le bouton de sauvegarde
        saveChangesButton.setOnClickListener(view -> saveChanges());
    }

    // Ouvre la galerie pour sélectionner une image
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Gère le résultat de la sélection d'image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Récupérer l'URI de l'image sélectionnée
            editPlanImageView.setImageURI(selectedImageUri); // Affichez l'image dans l'ImageView
        }
    }

    // Affiche un TimePickerDialog pour sélectionner une heure
    private void showTimePicker(final EditText timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    timeField.setText(time);
                }, hour, minute, true); // 24-hour format
        timePickerDialog.show();
    }

    private void saveChanges() {
        ContentValues values = new ContentValues();
        values.put("name", editPlanName.getText().toString());
        values.put("description", editPlanDescription.getText().toString());
        values.put("location", editPlanLocation.getText().toString());
        values.put("opening_time", editOpeningHours.getText().toString()); // Correspondance avec la base de données
        values.put("closing_time", editClosingHours.getText().toString()); // Correspondance avec la base de données
        values.put("image", selectedImageUri != null ? selectedImageUri.toString() : null);  // Correspondance avec la base de données

        // Mettre à jour le plan dans la base de données
        dbHelper.updatePlan(originalPlanName, values);
        Toast.makeText(this, "Modifications enregistrées avec succès", Toast.LENGTH_SHORT).show();
        finish();  // Fermer l'activité après la sauvegarde
    }
}
