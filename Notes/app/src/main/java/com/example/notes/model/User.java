package com.example.notes.model;

public class User {
    private int id;
    private String email;
    private String nom;
    private String prenom;
    private String imagePath;

    public User() {}

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getFullName() {
        if ((nom == null || nom.isEmpty()) && (prenom == null || prenom.isEmpty())) {
            return email != null ? email : "Utilisateur";
        }
        String fullName = "";
        if (prenom != null && !prenom.isEmpty()) {
            fullName += prenom;
        }
        if (nom != null && !nom.isEmpty()) {
            if (!fullName.isEmpty()) fullName += " ";
            fullName += nom;
        }
        return fullName.isEmpty() ? email : fullName;
    }
}

