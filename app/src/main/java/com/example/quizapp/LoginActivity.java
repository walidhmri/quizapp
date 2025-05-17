package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.database.UserDatabaseHelper;
import com.example.quizapp.model.User;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.model.Quiz;

import java.util.List;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isLoggedIn()) {
            navigateToQuizList();
            return;
        }

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        dbHelper = new UserDatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Veuillez entrer votre nom d'utilisateur");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Veuillez entrer votre mot de passe");
            return;
        }

        User user = dbHelper.authenticateUser(username, password);

        if (user != null) {
            saveLoginState(user.getId(), user.getUsername());
            
            navigateToQuizList();
        } else {
            Toast.makeText(this, "Nom d'utilisateur ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLoginState(int userId, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void navigateToQuizList() {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        List<Quiz> quizzes = dbHelper.getAllQuizzes();
        
        if (quizzes.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, QuizListActivity.class);
            startActivity(intent);
            finish();
        } else {
            Random random = new Random();
            int randomIndex = random.nextInt(quizzes.size());
            Quiz randomQuiz = quizzes.get(randomIndex);
            
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("quiz_id", randomQuiz.getId());
            intent.putExtra("from_login", true);
            startActivity(intent);
            finish();
        }
    }

    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
