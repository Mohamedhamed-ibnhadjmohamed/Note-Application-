package com.example.notes.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notes.model.Note;
import com.example.notes.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes_app.db";
    private static final int DB_VERSION = 2;

    // Tables
    public static final String TABLE_USER = "User";
    public static final String TABLE_NOTE = "Note";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table User
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "nom TEXT, " +
                "prenom TEXT, " +
                "image_path TEXT)";
        db.execSQL(createUserTable);

        // Table Note
        String createNoteTable = "CREATE TABLE " + TABLE_NOTE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "titre TEXT, " +
                "contenu TEXT, " +
                "categorie TEXT, " +
                "date_creation TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES User(id))";
        db.execSQL(createNoteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Ajouter les nouvelles colonnes à la table User
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN nom TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN prenom TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN image_path TEXT");
        }
    }

    // Ajouter un utilisateur avec tous les champs
    public boolean addUser(String email, String password, String nom, String prenom, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("password", password);
        cv.put("nom", nom);
        cv.put("prenom", prenom);
        cv.put("image_path", imagePath);

        long result = db.insert(TABLE_USER, null, cv);
        db.close();

        // Si insert renvoie -1, échec
        return result != -1;
    }

    // Méthode de compatibilité (ancienne version)
    public boolean addUser(String email, String password) {
        return addUser(email, password, "", "", "");
    }

    // Dans DBHelper.java
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE email=? AND password=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Récupérer l'ID d'un utilisateur à partir de son email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USER + " WHERE email=?", new String[]{email});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }
    // Récupérer les notes d'un utilisateur
    @SuppressLint("Range")
    public List<Note> getNotesByUser(int userId) {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE user_id=? ORDER BY date_creation DESC", new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndex("id")));
            note.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            note.setTitre(cursor.getString(cursor.getColumnIndex("titre")));
            note.setContenu(cursor.getString(cursor.getColumnIndex("contenu")));
            note.setCategorie(cursor.getString(cursor.getColumnIndex("categorie")));
            note.setDateCreation(cursor.getString(cursor.getColumnIndex("date_creation")));
            list.add(note);
        }
        cursor.close();
        db.close();
        return list;
    }

    // Ajouter une note
    public long addNote(int userId, String titre, String contenu, String categorie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("titre", titre);
        cv.put("contenu", contenu);
        cv.put("categorie", categorie);
        
        // Ajouter la date de création
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateCreation = sdf.format(new Date());
        cv.put("date_creation", dateCreation);

        long result = db.insert(TABLE_NOTE, null, cv);
        db.close();
        return result;
    }

    // Récupérer une note par son ID
    @SuppressLint("Range")
    public Note getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE id=?", new String[]{String.valueOf(noteId)});
        
        Note note = null;
        if (cursor.moveToFirst()) {
            note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndex("id")));
            note.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            note.setTitre(cursor.getString(cursor.getColumnIndex("titre")));
            note.setContenu(cursor.getString(cursor.getColumnIndex("contenu")));
            note.setCategorie(cursor.getString(cursor.getColumnIndex("categorie")));
            note.setDateCreation(cursor.getString(cursor.getColumnIndex("date_creation")));
        }
        cursor.close();
        db.close();
        return note;
    }

    // Mettre à jour une note
    public boolean updateNote(int noteId, String titre, String contenu, String categorie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("titre", titre);
        cv.put("contenu", contenu);
        cv.put("categorie", categorie);

        int result = db.update(TABLE_NOTE, cv, "id=?", new String[]{String.valueOf(noteId)});
        db.close();
        return result > 0;
    }

    // Supprimer une note
    public boolean deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NOTE, "id=?", new String[]{String.valueOf(noteId)});
        db.close();
        return result > 0;
    }

    // Récupérer les informations d'un utilisateur par son ID
    @SuppressLint("Range")
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE id=?", new String[]{String.valueOf(userId)});
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setNom(cursor.getString(cursor.getColumnIndex("nom")));
            user.setPrenom(cursor.getString(cursor.getColumnIndex("prenom")));
            user.setImagePath(cursor.getString(cursor.getColumnIndex("image_path")));
        }
        cursor.close();
        db.close();
        return user;
    }

    // Récupérer les informations d'un utilisateur par son email
    @SuppressLint("Range")
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE email=?", new String[]{email});
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setNom(cursor.getString(cursor.getColumnIndex("nom")));
            user.setPrenom(cursor.getString(cursor.getColumnIndex("prenom")));
            user.setImagePath(cursor.getString(cursor.getColumnIndex("image_path")));
        }
        cursor.close();
        db.close();
        return user;
    }

    // Mettre à jour les informations d'un utilisateur
    public boolean updateUser(int userId, String nom, String prenom, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nom", nom);
        cv.put("prenom", prenom);
        cv.put("image_path", imagePath);

        int result = db.update(TABLE_USER, cv, "id=?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

}

