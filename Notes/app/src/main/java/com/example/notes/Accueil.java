package com.example.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.notes.data.DBHelper;
import com.example.notes.model.Note;
import com.example.notes.model.User;

import java.util.ArrayList;
import java.util.List;

public class Accueil extends AppCompatActivity {

    private RecyclerView recyclerNotes;
    private FloatingActionButton fabAddNote;
    private TextView txtEmptyNotes;
    private DBHelper dbHelper;
    private NotesAdapter adapter;
    private List<Note> noteList;
    private String userEmail;
    private int userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Initialisation des vues
        recyclerNotes = findViewById(R.id.recyclerNotes);
        fabAddNote = findViewById(R.id.fabAddNote);
        txtEmptyNotes = findViewById(R.id.txtEmptyNotes);

        // Configurer la Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);

        // Récupérer l'email de l'utilisateur connecté depuis Login
        userEmail = getIntent().getStringExtra("user_email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Erreur: Email utilisateur manquant", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        userId = dbHelper.getUserIdByEmail(userEmail);

        // Vérifier que l'utilisateur existe
        if (userId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser la liste des notes
        noteList = new ArrayList<>();
        
        // Charger les notes depuis SQLite
        loadNotes();

        // Configuration du RecyclerView
        adapter = new NotesAdapter(noteList);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotes.setAdapter(adapter);

        // Gestion du clic sur une note pour l'éditer
        adapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(Accueil.this, EditNote.class);
            intent.putExtra("note_id", note.getId());
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Gestion du long clic pour supprimer une note
        adapter.setOnNoteLongClickListener(note -> {
            showDeleteConfirmation(note);
        });

        // Bouton ajouter une nouvelle note
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(Accueil.this, AddNote.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Afficher le message si aucune note
        updateEmptyState();
    }

    private void loadNotes() {
        List<Note> notes = dbHelper.getNotesByUser(userId);
        if (notes != null) {
            noteList.clear();
            noteList.addAll(notes);
        }
    }

    private void updateEmptyState() {
        if (noteList.isEmpty()) {
            recyclerNotes.setVisibility(View.GONE);
            txtEmptyNotes.setVisibility(View.VISIBLE);
        } else {
            recyclerNotes.setVisibility(View.VISIBLE);
            txtEmptyNotes.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmation(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Êtes-vous sûr de vouloir supprimer la note \"" + 
                        (note.getTitre() != null ? note.getTitre() : "Sans titre") + "\" ?")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteNote(note))
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteNote(Note note) {
        boolean success = dbHelper.deleteNote(note.getId());

        if (success) {
            Toast.makeText(this, "Note supprimée avec succès", Toast.LENGTH_SHORT).show();
            loadNotes();
            if (adapter != null) {
                adapter.updateNotes(noteList);
            }
            updateEmptyState();
        } else {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menuProfile) {
            showProfileDialog();
            return true;
        } else if (id == R.id.menuLogout) {
            showLogoutConfirmation();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showProfileDialog() {
        User user = dbHelper.getUserByEmail(userEmail);
        String profileInfo = "Email: " + userEmail + "\n";
        
        if (user != null) {
            String fullName = user.getFullName();
            if (!fullName.equals(userEmail)) {
                profileInfo += "Nom complet: " + fullName + "\n";
            }
        }
        
        profileInfo += "\nNombre de notes: " + noteList.size();
        
        new AlertDialog.Builder(this)
                .setTitle("Profil")
                .setMessage(profileInfo)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton("Déconnexion", (dialog, which) -> logout())
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void logout() {
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger la liste à chaque retour depuis Add/Edit Note
        loadNotes();
        if (adapter != null) {
            adapter.updateNotes(noteList);
        }
        updateEmptyState();
    }
}

