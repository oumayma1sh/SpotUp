package tn.esprit.spotup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tn.esprit.spotup.R;
import tn.esprit.spotup.fragments.salsabil.AffichageSignal;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Configuration de la vue principale avec EdgeToEdge pour les insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Récupérer le bouton Signal List
        Button btnSignalList = findViewById(R.id.btn_signal_list);

        // Définir un OnClickListener pour le bouton Signal List
        btnSignalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer un Intent pour démarrer l'activité AffichageSignal
                Intent intent = new Intent(AdminActivity.this, AffichageSignal.class);
                startActivity(intent);
            }
        });
    }
}
