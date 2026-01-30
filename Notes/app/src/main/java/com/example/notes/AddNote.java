package com.example.notes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import com.example.notes.data.DBHelper;

public class AddNote extends AppCompatActivity {

    private TextInputEditText edtTitre, edtContenu, edtCategorie;
    private Button btnSaveNote;
    private DBHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation
        edtTitre = findViewById(R.id.edtTitre);
        edtContenu = findViewById(R.id.edtContenu);
        edtCategorie = findViewById(R.id.edtCategorie);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        
        dbHelper = new DBHelper(this);

        // Récupérer l'ID de l'utilisateur
        userId = getIntent().getIntExtra("user_id", -1);
        
        if (userId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non identifié", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bouton Enregistrer
        btnSaveNote.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String titre = edtTitre.getText().toString().trim();
        String contenu = edtContenu.getText().toString().trim();
        String categorie = edtCategorie.getText().toString().trim();

        // Validation
        if (titre.isEmpty()) {
            edtTitre.setError("Le titre est requis");
            edtTitre.requestFocus();
            return;
        }

        if (contenu.isEmpty()) {
            edtContenu.setError("Le contenu est requis");
            edtContenu.requestFocus();
            return;
        }

        // Si catégorie vide, mettre "Sans catégorie"
        if (categorie.isEmpty()) {
            categorie = "Sans catégorie";
        }

        // Sauvegarder la note
        long result = dbHelper.addNote(userId, titre, contenu, categorie);

        if (result != -1) {
            Toast.makeText(this, "Note enregistrée avec succès", Toast.LENGTH_SHORT).show();
            finish(); // Retourner à Accueil
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
        }
    }
}

