package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.database.UserDatabaseHelper;
import com.example.quizapp.model.User;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView totalScoreTextView;
    private TextView quizzesCompletedTextView;
    private Button logoutButton;
    private Button backButton;

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        totalScoreTextView = findViewById(R.id.totalScoreTextView);
        quizzesCompletedTextView = findViewById(R.id.quizzesCompletedTextView);
        logoutButton = findViewById(R.id.logoutButton);
        backButton = findViewById(R.id.backButton);

        // Initialize database helper
        dbHelper = new UserDatabaseHelper(this);

        // Load user data
        loadUserData();

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Set up back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (userId != -1) {
            User user = dbHelper.getUserById(userId);

            if (user != null) {
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
                totalScoreTextView.setText(String.valueOf(user.getTotalScore()));
                quizzesCompletedTextView.setText(String.valueOf(user.getQuizzesCompleted()));
            }
        }
    }

    private void logout() {
        // Clear login state
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
