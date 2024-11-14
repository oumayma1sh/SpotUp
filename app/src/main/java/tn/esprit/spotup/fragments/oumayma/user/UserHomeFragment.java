package tn.esprit.spotup.fragments.oumayma.user;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tn.esprit.spotup.R;
import tn.esprit.spotup.activities.AddPlanActivity;
import tn.esprit.spotup.activities.EditPlanActivity;
import tn.esprit.spotup.database.DataBaseHelper;

public class UserHomeFragment extends Fragment {

    private Spinner categorySpinner;
    private DataBaseHelper dbHelper;
    private LinearLayout planContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        // Initialiser les éléments
        dbHelper = new DataBaseHelper(getActivity());
        planContainer = view.findViewById(R.id.plan_container);
        categorySpinner = view.findViewById(R.id.categorySpinner);

        // Configurer le Spinner pour la sélection de catégorie
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Configurer l'EventListener du Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if ("Categories".equals(selectedCategory)) {
                    loadPlansFromDatabase();
                } else {
                    filterPlans(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadPlansFromDatabase();
            }
        });

        // Ajouter un FloatingActionButton pour ajouter un plan
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            // Lancer l'activité AddPlanActivity pour ajouter un plan
            Intent intent = new Intent(getActivity(), AddPlanActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void filterPlans(String category) {
        planContainer.removeAllViews();
        Cursor cursor = dbHelper.getReadableDatabase().query(DataBaseHelper.TABLE_NAME, null,
                DataBaseHelper.COLUMN_CATEGORY + " = ?", new String[]{category}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String planName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME));
                String planDescription = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
                String planLocation = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
                String openingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_OPENING_TIME));
                String closingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_CLOSING_TIME));
                String imageUri = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_IMAGE));

                addPlanToLayout(planName, planDescription, planLocation, openingTime, closingTime, imageUri);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void loadPlansFromDatabase() {
        planContainer.removeAllViews();
        Cursor cursor = dbHelper.getReadableDatabase().query(DataBaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String planName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME));
                String planDescription = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
                String planLocation = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
                String openingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_OPENING_TIME));
                String closingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_CLOSING_TIME));
                String imageUri = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_IMAGE));

                addPlanToLayout(planName, planDescription, planLocation, openingTime, closingTime, imageUri);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void addPlanToLayout(String planName, String description, String location, String openingTime, String closingTime, String imageUri) {
        View cardView = LayoutInflater.from(getActivity()).inflate(R.layout.plan_card, planContainer, false);

        // Set plan details
        TextView nameTextView = cardView.findViewById(R.id.planName);
        nameTextView.setText(planName);
        TextView descriptionTextView = cardView.findViewById(R.id.planDescription);
        descriptionTextView.setText(description);
        TextView locationTextView = cardView.findViewById(R.id.planLocation);
        locationTextView.setText("Location: " + location);
        TextView openingTextView = cardView.findViewById(R.id.openingHours);
        openingTextView.setText("Opening Time: " + openingTime);
        TextView closingTextView = cardView.findViewById(R.id.closingHours);
        closingTextView.setText("Closing Time: " + closingTime);
        ImageView imageView = cardView.findViewById(R.id.planImage);
        try {
            imageView.setImageURI(Uri.parse(imageUri));
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.placeholder); // Default image in case of error
        }

        // Delete button
        ImageView deleteButton = cardView.findViewById(R.id.deletePlan);
        deleteButton.setOnClickListener(v -> {
            deletePlan(planName);
            planContainer.removeView(cardView);
        });

        // Edit button
        ImageView editButton = cardView.findViewById(R.id.editPlan);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditPlanActivity.class);
            intent.putExtra("planName", planName);
            intent.putExtra("planDescription", description);
            intent.putExtra("planLocation", location);
            intent.putExtra("openingTime", openingTime);
            intent.putExtra("closingTime", closingTime);
            intent.putExtra("planImage", imageUri);
            startActivityForResult(intent, 1); // Start with request code
        });

        planContainer.addView(cardView);
    }

    private void deletePlan(String planName) {
        dbHelper.getWritableDatabase().delete(DataBaseHelper.TABLE_NAME,
                DataBaseHelper.COLUMN_NAME + " = ?", new String[]{planName});
    }
}
