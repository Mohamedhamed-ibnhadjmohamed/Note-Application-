package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.data.DBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Register extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    
    EditText edtEmailRegister, edtPasswordRegister, edtConfirmPassword, edtNom, edtPrenom;
    Button btnRegister, btnSelectImage;
    TextView txtGoToLogin;
    ImageView imgProfile;
    DBHelper dbHelper;
    String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liaison avec XML
        edtEmailRegister = findViewById(R.id.edtEmailRegister);
        edtPasswordRegister = findViewById(R.id.edtPasswordRegister);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtNom = findViewById(R.id.edtNom);
        edtPrenom = findViewById(R.id.edtPrenom);
        btnRegister = findViewById(R.id.btnRegister);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        txtGoToLogin = findViewById(R.id.txtGoToLogin);
        imgProfile = findViewById(R.id.imgProfile);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Bouton pour sélectionner une image
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Lien vers Login
        txtGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        // Bouton "Créer un compte"
        btnRegister.setOnClickListener(v -> {

            String email = edtEmailRegister.getText().toString().trim();
            String password = edtPasswordRegister.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();
            String nom = edtNom.getText().toString().trim();
            String prenom = edtPrenom.getText().toString().trim();

            // Vérification des champs obligatoires
            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(Register.this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier si les mots de passe correspondent
            if (!password.equals(confirm)) {
                edtConfirmPassword.setError("Les mots de passe ne correspondent pas");
                return;
            }

            // Ajouter utilisateur dans la base SQLite avec les nouveaux champs
            boolean inserted = dbHelper.addUser(email, password, nom, prenom, selectedImagePath);
            if (inserted) {
                Toast.makeText(Register.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this, Login.class));
                finish();
            } else {
                Toast.makeText(Register.this, "Erreur : cet email existe déjà", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Charger l'image dans l'ImageView
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgProfile.setImageBitmap(bitmap);
                
                // Sauvegarder l'image dans le stockage interne de l'app
                selectedImagePath = saveImageToInternalStorage(bitmap, "profile_" + System.currentTimeMillis() + ".jpg");
                
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap, String filename) {
        try {
            File directory = new File(getFilesDir(), "profile_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            File file = new File(directory, filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

