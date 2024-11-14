package tn.esprit.spotup.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;

public class OverViewEventActivity extends AppCompatActivity {

    private ImageView overviewEventImage;
    private TextView overviewEventTitle;
    private TextView overviewEventDate;
    private TextView overviewEventTime;
    private TextView overviewEventCity;
    private TextView overviewEventDescription;
    private TextView overviewEventPrice;
    private Button overviewViewScheduleButton;
    private Button participateButton;
    private TextView participantCountTextView;
    DataBaseHelper dbHelper = new DataBaseHelper(this);

    private static final int PERMISSION_REQUEST_CODE = 1; // Permission request code

    private int userId = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize views

        overviewEventImage = findViewById(R.id.overview_event_image);
        overviewEventTitle = findViewById(R.id.overview_event_title);
        overviewEventDate = findViewById(R.id.overview_event_date_start);
        overviewEventTime = findViewById(R.id.overview_event_end_date);
        overviewEventCity = findViewById(R.id.overview_event_city);
        overviewEventDescription = findViewById(R.id.overview_event_description);
        overviewEventPrice = findViewById(R.id.overview_event_price);
        overviewViewScheduleButton = findViewById(R.id.overview_view_schedule_button);
        participateButton = findViewById(R.id.overview_participate_button);
        participantCountTextView = findViewById(R.id.overview_participant_count);
        participateButton = findViewById(R.id.overview_participate_button);
        participantCountTextView = findViewById(R.id.overview_participant_count);



               // Get event data from the intent
        Intent intent = getIntent();
       String  title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String dateDebut = intent.getStringExtra("dateDebut");
        String dateFin = intent.getStringExtra("dateFin");
        String location = intent.getStringExtra("location");
        String price = intent.getStringExtra("prix");
        // Vérifiez si l'utilisateur a déjà participé et mettez à jour l'interface
//        updateParticipantStatus(title);
//
//        participateButton.setOnClickListener(v -> handleParticipation(title));

        // Set event data to views
        if (title != null && description != null && dateDebut != null && dateFin != null && location != null) {
            overviewEventTitle.setText(title);
            overviewEventDescription.setText(description);
            overviewEventDate.setText(dateDebut);
            overviewEventTime.setText(dateFin);
            overviewEventCity.setText(location);
            overviewEventPrice.setText(String.format("Price: dt%.2f", Double.parseDouble(price)));
        } else {
            Toast.makeText(this, "Some event details are missing", Toast.LENGTH_SHORT).show();
        }

        // Check permissions before proceeding
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
        }
        int participantCount = dbHelper.getParticipantCountByEventTitle(title);
        participantCountTextView.setText("Participants: " + participantCount);

        // Check if the user has already participated and disable the button if necessary
        if (dbHelper.hasUserParticipated(title, userId)) {
            participateButton.setEnabled(false);
        }

        // Handle the participate button click
        participateButton.setOnClickListener(v -> {
            boolean isAdded = dbHelper.addParticipant(title, userId);
            if (isAdded) {
                participateButton.setEnabled(false);
                // Update participant count dynamically after adding the participant
                int updatedParticipantCount = dbHelper.getParticipantCountByEventTitle(title);
                participantCountTextView.setText("Participants: " + updatedParticipantCount);
                Toast.makeText(this, "You have successfully joined the event", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You are already a participant", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for view schedule button
        overviewViewScheduleButton.setOnClickListener(view -> {
            // Create the intent to insert an event in the calendar
            Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);

            // Set the event start and end times using the values from intent
            Calendar beginTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();

            // Convert dateDebut and dateFin into Calendar instances (assuming format is "yyyy-MM-dd HH:mm")
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                beginTime.setTime(sdf.parse(dateDebut)); // e.g., "2021-01-23 07:30"
                endTime.setTime(sdf.parse(dateFin)); // e.g., "2021-01-23 10:30"
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
            }

            // Adding event details to the intent
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
            calendarIntent.putExtra(CalendarContract.Events.TITLE, title);
            calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);

            // Check if there are any apps that can handle the calendar insert intent
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(calendarIntent, PackageManager.MATCH_DEFAULT_ONLY);

            if (activities.size() > 0) {
                // If there are apps that can handle the intent, start the calendar activity
                Intent chooserIntent = Intent.createChooser(calendarIntent, "Choose a calendar app");
                startActivity(chooserIntent);
            } else {
                // If no compatible calendar app is found, redirect the user to the Play Store to install Google Calendar
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://calendar.google.com/calendar/r/ "));
                startActivity(playStoreIntent);
            }

            // Querying the calendar provider for existing events
            queryCalendarEvents();
        });
    }
















    private void queryCalendarEvents() {
        // Query calendar events using the content resolver
        Cursor cursor = getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                new String[] {
                        CalendarContract.Events.CALENDAR_ID,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DESCRIPTION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.EVENT_LOCATION
                },
                null, null, null); // You can add selection and sort order as needed

        if (cursor != null && cursor.moveToFirst()) {
            // Process the events from the cursor
            do {
                String calendarId = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION));
                long startTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART));
                long endTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION));

                // Example of how to display the event info
                Log.d("CalendarEvent", "Event: " + title + " at " + location + " from " + startTimeMillis + " to " + endTimeMillis);
            } while (cursor.moveToNext());
            cursor.close(); // Close the cursor after use
        } else {
            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with calendar access
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied to read calendar", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
