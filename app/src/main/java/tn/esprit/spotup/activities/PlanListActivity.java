package tn.esprit.spotup.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;

public class PlanListActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private DataBaseHelper dbHelper;
    private LinearLayout planContainer;
    private DrawerLayout drawerLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            planContainer.removeAllViews(); // Clear old views
            loadPlansFromDatabase(); // Reload the updated list
        }
    }

    private void filterPlans(String category) {
        planContainer.removeAllViews(); // Vider la liste

        Cursor cursor = dbHelper.getReadableDatabase().query(DataBaseHelper.TABLE_NAME, null,
                DataBaseHelper.COLUMN_CATEGORY + " = ?", new String[]{category}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Récupérer les données du curseur
                String planName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME));
                String planDescription = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
                String planLocation = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
                String openingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_OPENING_TIME));
                String closingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_CLOSING_TIME));
                String imageUri = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_IMAGE));

                // Ajouter le plan au layout
                addPlanToLayout(planName, planDescription, planLocation, openingTime, closingTime, imageUri);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_home);

//        // Configure la Toolbar
//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar); // Associe la Toolbar comme ActionBar
//        // Modifier dynamiquement le titre de la Toolbar
//        getSupportActionBar().setTitle("Find Your Plan");
//        // Initialiser le DrawerLayout
//         drawerLayout = findViewById(R.id.drawer_layout);
//
////        // Configurer le NavigationView
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//              // case R.id.menu_plans:
//                   // Gérer l'item Plans
//               //     break;
//                //case R.id.menu_events:
//                    // Gérer l'item Events
//              // case R.id.menu_reclamation:
//                    // Gérer l'item Reclamation
//               //     break;
//           }
//            drawerLayout.closeDrawers(); // Fermer le drawer après la sélection
//            return true;
//        });
//
//        // Ajouter le bouton hamburger
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this,
//                drawerLayout,
//                toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close
//        );
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//





        // Récupérer le FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Démarrer addplanActivity
            Intent intent = new Intent(PlanListActivity.this, AddPlanActivity.class);
            startActivity(intent);
        });

        // Initialize the PlanDatabaseHelper
        dbHelper = new DataBaseHelper(this);

        // Find the LinearLayout container where we'll add the plans
        planContainer = findViewById(R.id.plan_container);  // Make sure you add this ID in your XML

        // Initialiser le Spinner pour la sélection de catégorie
        categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Configurer l'EventListener du Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();

                // Vérifier si la catégorie sélectionnée est "Plans" pour afficher tous les plans
                if (selectedCategory.equals("Categories")) { // Utilisez "Plans" comme dans le string-array
                    loadPlansFromDatabase(); // Affiche tous les plans
                } else {
                    filterPlans(selectedCategory); // Filtre par catégorie spécifique
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadPlansFromDatabase(); // Charge tous les plans si rien n'est sélectionné
            }
        });

        // Load the plans from the database
        //  loadPlansFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        planContainer.removeAllViews(); // Vider la liste
        loadPlansFromDatabase(); // Recharger les plans
    }

    private void loadPlansFromDatabase() {
        // Vider le conteneur pour éviter la redondance des plans
        planContainer.removeAllViews();

        // Récupérer tous les plans de la base de données
        Cursor cursor = dbHelper.getReadableDatabase().query(DataBaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Récupérer les données du curseur
                String planName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME));
                String planDescription = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
                String planLocation = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
                String openingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_OPENING_TIME));
                String closingTime = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_CLOSING_TIME));
                String imageUri = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_IMAGE)); // URI de l'image

                // Ajouter dynamiquement un CardView pour chaque plan
                addPlanToLayout(planName, planDescription, planLocation, openingTime, closingTime, imageUri);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }


    private void addPlanToLayout(String planName, String description, String location, String openingTime, String closingTime, String imageUri) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.plan_card, planContainer, false);

        // Set the plan details
        TextView nameTextView = cardView.findViewById(R.id.planName);
        nameTextView.setText(planName);

        TextView descriptionTextView = cardView.findViewById(R.id.planDescription);
        descriptionTextView.setText(description);

        TextView locationTextView = cardView.findViewById(R.id.planLocation);
        locationTextView.setText("Location : " + location);

        TextView openingTextView = cardView.findViewById(R.id.openingHours);
        openingTextView.setText("Ooening Time: " + openingTime);

        TextView closingTextView = cardView.findViewById(R.id.closingHours);
        closingTextView.setText("Closing Time : " + closingTime);

        ImageView imageView = cardView.findViewById(R.id.planImage);

        // Charger l'image depuis l'URI avec ContentResolver
        try {
            imageView.setImageURI(Uri.parse(imageUri));
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.placeholder); // Image par défaut si échec
        }

        // Récupérer le bouton de suppression
        ImageView deleteButton = cardView.findViewById(R.id.deletePlan);
        deleteButton.setOnClickListener(view -> {
            // Appeler la fonction pour supprimer le plan de la base de données
            deletePlan(planName);
            // Supprimer dynamiquement le CardView de l'interface
            planContainer.removeView(cardView);
        });

        ImageView editButton = cardView.findViewById(R.id.editPlan);
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(PlanListActivity.this, EditPlanActivity.class);
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
        // Supprimer le plan en fonction de son nom (ou autre critère)
        dbHelper.getWritableDatabase().delete(
                DataBaseHelper.TABLE_NAME,
                DataBaseHelper.COLUMN_NAME + " = ?",
                new String[]{planName}
        );
    }
}
