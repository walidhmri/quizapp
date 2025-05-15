package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.database.UserDatabaseHelper;
import com.example.quizapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Initialize database helper
        dbHelper = new UserDatabaseHelper(this);

        // Set up register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set up login text view click listener
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Veuillez entrer un nom d'utilisateur");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Veuillez entrer une adresse e-mail");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Veuillez entrer une adresse e-mail valide");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Veuillez entrer un mot de passe");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Les mots de passe ne correspondent pas");
            return;
        }

        // Check if username already exists
        if (dbHelper.checkUsername(username)) {
            usernameEditText.setError("Ce nom d'utilisateur est déjà utilisé");
            return;
        }

        // Create user
        User user = new User(username, password, email);
        long id = dbHelper.addUser(user);

        if (id != -1) {
            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } else {
            Toast.makeText(this, "Échec de l'inscription", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
