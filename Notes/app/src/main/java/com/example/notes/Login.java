package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.data.DBHelper;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Récupération des éléments graphiques
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        // Initialiser DBHelper
        dbHelper = new DBHelper(this);

        // Lien vers Register
        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });

        // Bouton Login
        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                edtEmail.setError("Champ requis");
                edtPassword.setError("Champ requis");
                return;
            }

            // Vérifier l'utilisateur dans SQLite
            boolean exists = dbHelper.checkUser(email, password);

            if (exists) {
                Toast.makeText(Login.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                // Redirection vers Accueil
                Intent intent = new Intent(Login.this, Accueil.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

