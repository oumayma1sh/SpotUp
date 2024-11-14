package tn.esprit.spotup.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import tn.esprit.spotup.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import tn.esprit.spotup.database.DataBaseHelper;

public class AddPlanActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;

    private EditText editTextName, editTextDescription, editTextLocalisation,
            editTextOpeningTime, editTextClosingTime;
    private Spinner spinnerCategory;
    private ImageView imageView;
    private DataBaseHelper databaseHelper; // Ajoutez ce champ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plan_activity_main);

        // Initialisez le DatabaseHelper
        databaseHelper = new DataBaseHelper(this);

        // Références des champs et boutons
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLocalisation = findViewById(R.id.editTextLocalisation);
        editTextOpeningTime = findViewById(R.id.editTextOpeningTime);
        editTextClosingTime = findViewById(R.id.editTextClosingTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageView = findViewById(R.id.imageView);

        Button btnUploadImage = findViewById(R.id.buttonUploadImage);
        Button btnAddPlan = findViewById(R.id.buttonAddPlan);

        // Configuration du Spinner avec les catégories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Listener pour le bouton d'upload d'image
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Listeners pour les champs d'heure
        editTextOpeningTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(editTextOpeningTime);
            }
        });

        editTextClosingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(editTextClosingTime);
            }
        });

        // Listener pour ajouter le plan
        btnAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlanToDatabase(); // Appel de la méthode pour ajouter le plan
            }
        });
    }

    // Ouvre la galerie pour sélectionner une image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    // Gère le résultat de la sélection d'image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageUri = saveImageToInternalStorage(selectedImageUri);
            imageView.setImageURI(imageUri);
        }
    }

    // Method to save the image in internal storage and return the new URI
    private Uri saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File imageFile = new File(getFilesDir(), "image_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return Uri.fromFile(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Affiche un TimePickerDialog pour sélectionner une heure
    private void showTimePicker(final EditText timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        timeField.setText(time);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void addPlanToDatabase() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String localisation = editTextLocalisation.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String openingTime = editTextOpeningTime.getText().toString().trim();
        String closingTime = editTextClosingTime.getText().toString().trim();
        String imagePath = imageUri != null ? imageUri.toString() : ""; // Chemin de l'image

        // Vérifiez si tous les champs sont remplis
        if (name.isEmpty() || description.isEmpty() || localisation.isEmpty() || openingTime.isEmpty() || closingTime.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Insérez les données dans la table
        String insertQuery = "INSERT INTO " + DataBaseHelper.TABLE_NAME + " (" +
                DataBaseHelper.COLUMN_NAME + ", " +
                DataBaseHelper.COLUMN_DESCRIPTION + ", " +
                DataBaseHelper.COLUMN_LOCATION + ", " +
                DataBaseHelper.COLUMN_CATEGORY + ", " +
                DataBaseHelper.COLUMN_IMAGE + ", " +
                DataBaseHelper.COLUMN_OPENING_TIME + ", " +
                DataBaseHelper.COLUMN_CLOSING_TIME + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        db.execSQL(insertQuery, new Object[]{name, description, localisation, category, imagePath, openingTime, closingTime});
        db.close();


        Toast.makeText(this, "Plan ajouté avec succès", Toast.LENGTH_SHORT).show();
        clearFields(); // Efface les champs après ajout




        // Envoyer un e-mail automatiquement
        String recipient = "rania.bensalem.1@esprit.tn";
        String subject = "New Plan Added: " + name;
        String message = "Hello,\n\n" +
                "🎉 **Exciting News! A New Plan Has Just Been Added!** 🎉\n\n" +
                "We're thrilled to introduce a fantastic new addition to our platform that you won't want to miss! Here are the exclusive details:\n\n" +
                "💥 **Why Check This Out?** 💥\n" +
                "This plan is not just any plan—it's a unique experience waiting for you! Whether you're looking for the best dining spots, exciting activities, or hidden gems in your area, this plan has it all!\n\n" +
                "Don't wait—be one of the first to explore and take advantage of this incredible opportunity.\n\n" +
                "👉 **Click here to discover more**: [Insert URL or App Link Here]\n\n" +
                "Best regards,\n" +
                "The BonPlans Team";
        new Thread(() -> MailSender.sendEmail(recipient, subject, message)).start();


        // Démarrer l'activité de la liste des plans
        Intent intent = new Intent(AddPlanActivity.this, PlanListActivity.class);
        startActivity(intent);
    }


    // Méthode pour effacer les champs après ajout
    private void clearFields() {
        editTextName.setText("");
        editTextDescription.setText("");
        editTextLocalisation.setText("");
        editTextOpeningTime.setText("");
        editTextClosingTime.setText("");
        spinnerCategory.setSelection(0); // Réinitialise le Spinner
        imageView.setImageResource(0); // Réinitialise l'image
    }




}
