package tn.esprit.spotup.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;

public class UpdateEventActivity extends AppCompatActivity {

    EditText eventTitleEdit, eventDescriptionEdit, eventPriceEdit, eventStartDateEdit, eventEndDateEdit;
    Spinner spinnerLocation;
    ImageView eventImageEdit;
    Button btnSaveChanges, btnUploadImage, delete_button;
    String _id, title, description, dateDebut, dateFin, location, prix,categorie;
    byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_update);

        // Initialisation des vues
        eventTitleEdit = findViewById(R.id.eventTitleEdit);
        eventDescriptionEdit = findViewById(R.id.eventDescriptionEdit);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        eventPriceEdit = findViewById(R.id.eventPriceEdit);
        eventStartDateEdit = findViewById(R.id.eventStartDateEdit);
        eventEndDateEdit = findViewById(R.id.eventEndDateEdit);
        eventImageEdit = findViewById(R.id.eventImageEdit);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnUploadImage = findViewById(R.id.btn_upload_image);

        // Configuration du Spinner pour la localisation
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations_tunisia, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapter);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Modifier l'événement");
        }

        // DatePicker pour le champ date de début
        eventStartDateEdit.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(UpdateEventActivity.this, (datePicker, year, month, day) -> {
                dateDebut = day + "/" + (month + 1) + "/" + year;
                new TimePickerDialog(UpdateEventActivity.this, (timePicker, hour, minute) -> {
                    dateDebut += " " + hour + ":" + String.format("%02d", minute);
                    eventStartDateEdit.setText(dateDebut);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // DatePicker pour le champ date de fin
        eventEndDateEdit.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(UpdateEventActivity.this, (datePicker, year, month, day) -> {
                dateFin = day + "/" + (month + 1) + "/" + year;
                new TimePickerDialog(UpdateEventActivity.this, (timePicker, hour, minute) -> {
                    dateFin += " " + hour + ":" + String.format("%02d", minute);
                    eventEndDateEdit.setText(dateFin);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Bouton pour uploader une image
        btnUploadImage.setOnClickListener(v -> openGallery());
        getAndSetIntentData();

        // Bouton pour sauvegarder les modifications
        btnSaveChanges.setOnClickListener(view -> {
            DataBaseHelper myDB = new DataBaseHelper(UpdateEventActivity.this);

            // Récupérer les nouvelles valeurs des champs
            title = eventTitleEdit.getText().toString().trim();
            description = eventDescriptionEdit.getText().toString().trim();
            dateDebut = eventStartDateEdit.getText().toString().trim();
            dateFin = eventEndDateEdit.getText().toString().trim();
            location = spinnerLocation.getSelectedItem().toString();
            prix = eventPriceEdit.getText().toString().trim();

            // Convertir l'image en byte[] si une nouvelle image a été uploadée
//            if (eventImageEdit.getDrawable() != null) {
//                Bitmap bitmap = ((BitmapDrawable) eventImageEdit.getDrawable()).getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                image = stream.toByteArray();
//            }

            // Mise à jour des données dans la base de données
            myDB.updateDataEvent( title, description,dateDebut, dateFin, location, image, prix,categorie);
            Toast.makeText(UpdateEventActivity.this, "Modifications enregistrées", Toast.LENGTH_SHORT).show();
            new SendSmsTaskUpdate(title, dateDebut).execute();  // Here you pass event title and the updated start date

            finish(); // Fermer l'activité après la sauvegarde
        });

//        // Bouton pour supprimer l'événement
//        delete_button.setOnClickListener(view -> confirmDeleteDialog(title));
//    }
//
//    private void confirmDeleteDialog(String title) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Supprimer " + title + " ?");
//        builder.setMessage("Êtes-vous sûr de vouloir supprimer cet événement ?");
//        builder.setPositiveButton("Oui", (dialog, which) -> {
//            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateEventActivity.this);
//            myDB.deleteEventByTitle(title); // Supprime l'événement par titre
//            Toast.makeText(UpdateEventActivity.this, "Événement supprimé", Toast.LENGTH_SHORT).show();
//            finish(); // Ferme l'activité après la suppression
//        });
//        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());
//        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        eventImageEdit.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    void getAndSetIntentData() {
        if ( getIntent().hasExtra("title") &&
                getIntent().hasExtra("description") && getIntent().hasExtra("location") &&
                getIntent().hasExtra("prix") && getIntent().hasExtra("dateDebut") && getIntent().hasExtra("dateFin") &&
                getIntent().hasExtra("image")) {

            // Récupération des données de l'intent
            title = getIntent().getStringExtra("title");
            description = getIntent().getStringExtra("description");
            location = getIntent().getStringExtra("location");
            prix = getIntent().getStringExtra("prix");
            dateDebut = getIntent().getStringExtra("dateDebut");
            dateFin = getIntent().getStringExtra("dateFin");
            image = getIntent().getByteArrayExtra("image");

            // Afficher l'image dans l'ImageView
            if (image != null && image.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                eventImageEdit.setImageBitmap(bitmap);
            } else {
                eventImageEdit.setImageResource(R.drawable.card_background); // Image par défaut
            }

            // Définir les valeurs dans les champs de texte
            eventTitleEdit.setText(title);
            eventDescriptionEdit.setText(description);
            eventPriceEdit.setText(prix);
            eventStartDateEdit.setText(dateDebut);
            eventEndDateEdit.setText(dateFin);

            // Mettre à jour la sélection du Spinner pour la localisation
            int locationPosition = ((ArrayAdapter) spinnerLocation.getAdapter()).getPosition(location);
            if (locationPosition >= 0) {
                spinnerLocation.setSelection(locationPosition);
            }
        }
    }
}
