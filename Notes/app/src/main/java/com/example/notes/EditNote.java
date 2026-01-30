package com.example.notes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import com.example.notes.data.DBHelper;
import com.example.notes.model.Note;

public class EditNote extends AppCompatActivity {

    private TextInputEditText edtTitre, edtContenu, edtCategorie;
    private TextView txtDateCreation;
    private Button btnUpdateNote, btnDeleteNote;
    private DBHelper dbHelper;
    private Note currentNote;
    private int noteId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation
        edtTitre = findViewById(R.id.edtTitre);
        edtContenu = findViewById(R.id.edtContenu);
        edtCategorie = findViewById(R.id.edtCategorie);
        txtDateCreation = findViewById(R.id.txtDateCreation);
        btnUpdateNote = findViewById(R.id.btnUpdateNote);
        btnDeleteNote = findViewById(R.id.btnDeleteNote);
        
        dbHelper = new DBHelper(this);

        // Récupérer l'ID de la note et de l'utilisateur
        noteId = getIntent().getIntExtra("note_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        if (noteId == -1) {
            Toast.makeText(this, "Erreur: Note non trouvée", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Charger la note
        loadNote();

        // Bouton Mettre à jour
        btnUpdateNote.setOnClickListener(v -> updateNote());

        // Bouton Supprimer
        btnDeleteNote.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadNote() {
        currentNote = dbHelper.getNoteById(noteId);
        
        if (currentNote != null) {
            edtTitre.setText(currentNote.getTitre());
            edtContenu.setText(currentNote.getContenu());
            edtCategorie.setText(currentNote.getCategorie());
            
            String dateText = "Date de création : " + 
                    (currentNote.getDateCreation() != null ? currentNote.getDateCreation() : "Non définie");
            txtDateCreation.setText(dateText);
        } else {
            Toast.makeText(this, "Note introuvable", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateNote() {
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

        // Mettre à jour la note
        boolean success = dbHelper.updateNote(noteId, titre, contenu, categorie);

        if (success) {
            Toast.makeText(this, "Note mise à jour avec succès", Toast.LENGTH_SHORT).show();
            finish(); // Retourner à Accueil
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteNote())
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteNote() {
        boolean success = dbHelper.deleteNote(noteId);

        if (success) {
            Toast.makeText(this, "Note supprimée avec succès", Toast.LENGTH_SHORT).show();
            finish(); // Retourner à Accueil
        } else {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }
}

