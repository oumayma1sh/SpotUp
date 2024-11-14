package tn.esprit.spotup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tn.esprit.spotup.R;
import tn.esprit.spotup.database.DataBaseHelper;
import tn.esprit.spotup.entities.Event;

public class EventListe extends AppCompatActivity {

    private RecyclerView nearbyEventRecycler;
    private RecyclerView popularEventRecycler;
    private EventAdapter nearbyEventAdapter;
    private EventAdapter popularEventAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_event);
        initializeViews();
        setupRecyclerViews();
        loadEventData();

        setupSearchView();
        setupCategorySpinner();
    }

    private void initializeViews() {
        searchView = findViewById(R.id.searchBar);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(EventListe.this, AddEvent.class);
            startActivity(intent);
        });
    }

    private void setupCategorySpinner() {
        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        // Liste des catégories
        ArrayList<String> categories = new ArrayList<>(Arrays.asList("Tous", "Boutique", "Hôtel", "Restaurant"));

        // Adapter pour le Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Ajouter un écouteur pour le Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterByCategory(selectedCategory);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Aucun filtre si rien n'est sélectionné
                filterByCategory("Tous");
            }
        });
    }


    private void filterByCategory(String category) {
        DataBaseHelper dbHelper = new DataBaseHelper(this);

        // Check if "Tous" (All) is selected, then display all events
        if ("Tous".equals(category)) {
            List<Event> allNearbyEvents = dbHelper.getAllEvents();
            List<Event> allPopularEvents = dbHelper.getAllEvents();

            // Update adapters with all events
            nearbyEventAdapter.updateEvents(allNearbyEvents);
            popularEventAdapter.updateEvents(allPopularEvents);
        } else {
            // Filter events by the selected category
            List<Event> filteredNearbyEvents = dbHelper.getEventsByCategory(category);
            List<Event> filteredPopularEvents = dbHelper.getEventsByCategory(category);

            // Update adapters with filtered events
            nearbyEventAdapter.updateEvents(filteredNearbyEvents);
            popularEventAdapter.updateEvents(filteredPopularEvents);
        }
    }


    private void setupRecyclerViews() {
        nearbyEventRecycler = findViewById(R.id.nearbyEventRecycler);
        popularEventRecycler = findViewById(R.id.popularEventRecycler);

        // Configuration des LayoutManager pour chaque RecyclerView
        nearbyEventRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularEventRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText);
                return false;
            }
        });
    }

    private void filterEvents(String query) {
        nearbyEventAdapter.getFilter().filter(query);
        popularEventAdapter.getFilter().filter(query);
    }

    private void loadEventData() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        List<Event> nearbyEvents = dbHelper.getUpcomingEvents(); // Récupère les événements proches
        List<Event> popularEvents = dbHelper.getAllEvents(); // Récupère les événements populaires

        // Initialisation des adapters avec les données récupérées
        nearbyEventAdapter = new EventAdapter(this, nearbyEvents);
        popularEventAdapter = new EventAdapter(this, popularEvents);

        // Association des adapters aux RecyclerView
        nearbyEventRecycler.setAdapter(nearbyEventAdapter);
        popularEventRecycler.setAdapter(popularEventAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventData(); // Rafraîchir les données
    }
}
