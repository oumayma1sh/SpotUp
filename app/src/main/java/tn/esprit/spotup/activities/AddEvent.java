package tn.esprit.spotup.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;

public class AddEvent extends AppCompatActivity {

    Uri imageUri; // Variable to hold the selected image URI
    private static final int PICK_IMAGE = 1;
    EditText etStartDate, etEndDate,price,etTitle, etDescription,Price;
    Calendar calendar;
    private ImageView imageView;
    DataBaseHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        etTitle = findViewById(R.id.et_event_title);
        etDescription = findViewById(R.id.et_event_description);
        etStartDate = findViewById(R.id.et_event_start_date);
        etEndDate = findViewById(R.id.et_event_end_date);
        imageView = findViewById(R.id.imageView);
        Price = findViewById(R.id.Price);

        FloatingActionButton btnback = findViewById(R.id.btn_backs);

        Spinner spinnerLocation = findViewById(R.id.spinner_event_location);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations_tunisia, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapter);Spinner spinnerCategorie = findViewById(R.id.spinner_event_categorie);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorie.setAdapter(categoryAdapter); // Utilisez spinnerCategorie ici

        btnback.setOnClickListener(v -> {
            Intent intent = new Intent(AddEvent.this, EventListe.class);
            startActivity(intent);
        });

        dbHelper = new DataBaseHelper(this);


//       Bouton d'upload
        Button btnUploadImage = findViewById(R.id.btn_upload_image);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


//       Bouton d'ajout'

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the values from the text fields
                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                String startDate = etStartDate.getText().toString();
                String endDate = etEndDate.getText().toString();
                String selectedLocation = spinnerLocation.getSelectedItem().toString();
                String price = Price.getText().toString();
                String selectedCategorie = spinnerCategorie.getSelectedItem().toString();

                // Ensure all fields are filled
                if (title.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || imageUri == null) {
                    Toast.makeText(AddEvent.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert image Uri to byte[]
                byte[] imageBytes = getBytesFromUri(imageUri);

                // Call the addEvent method to insert the event into the database
                long result = dbHelper.addEvent(title, description, startDate, endDate, selectedLocation, imageBytes,price,selectedCategorie);

                if (result != -1) {
                    Calendar eventCalendar = Calendar.getInstance();
                    // Utilisez le format que vous avez utilisé pour stocker la date
                    eventCalendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE); // Remplacez avec les bonnes valeurs

                    // Définir le rappel
                    setEventReminder(eventCalendar.getTimeInMillis(), title, "Rappel pour votre événement : " + title);

                    Toast.makeText(AddEvent.this, "Événement ajouté avec succès", Toast.LENGTH_SHORT).show();
                    // Reset the fields if needed
                    etTitle.setText("");
                    etDescription.setText("");
                    etStartDate.setText("");
                    etEndDate.setText("");
                    imageView.setImageResource(0); // Reset the displayed image
                    Price.setText(""); // Reset the displayed image
                } else {
                    Toast.makeText(AddEvent.this, "Erreur lors de l'ajout de l'événement", Toast.LENGTH_SHORT).show();
                }

            }

        });




        calendar = Calendar.getInstance();
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(etStartDate);
            }
        });
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(etEndDate);
            }
        });
    }
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Handle the image URI, possibly copying it to local storage if necessary
                        imageView.setImageURI(selectedImageUri);
                    }
                }
            }
    );
    private byte[] getBytesFromUri(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
        startActivityForResult(intent, PICK_IMAGE);

    }
//                               Envoyer un rappel pour client
//    private void showNotification(String title, String message) {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        String channelId = "event_channel";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Event Notifications", NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_delete)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true);
//
//        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//    }
//
    private void setEventReminder(long eventTimeMillis, String title, String message) {
        Intent intent = new Intent(this, EventListe.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, eventTimeMillis, pendingIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }






    // Méthode pour afficher le DatePicker et le TimePicker
    private void showDateTimePicker(final EditText editText) {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();

        new DatePickerDialog(AddEvent.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AddEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " " + hourOfDay + ":" + minute);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }
}
