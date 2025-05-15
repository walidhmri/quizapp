package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.adapter.QuizAdapter;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.model.Quiz;

import java.util.List;

public class QuizListActivity extends AppCompatActivity implements QuizAdapter.OnQuizClickListener {

    private static final String TAG = "QuizListActivity";
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USERNAME = "username";

    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private QuizDatabaseHelper dbHelper;
    private TextView emptyView;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new QuizDatabaseHelper(this);

        // Set welcome message
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        if (!username.isEmpty()) {
            welcomeTextView.setText("Bienvenue, " + username + " !");
        }

        loadQuizzes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_profile) {
            // Go to profile activity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_random_quiz) {
            // Start a random quiz
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_add_quiz) {
            // Go to add quiz activity
            Intent intent = new Intent(this, AddQuizActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadQuizzes();
    }

    private void loadQuizzes() {
        List<Quiz> quizList = dbHelper.getAllQuizzes();

        if (quizList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            quizAdapter = new QuizAdapter(quizList, this);
            recyclerView.setAdapter(quizAdapter);
        }
    }

    @Override
    public void onQuizClick(int quizId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("quiz_id", quizId);
        startActivity(intent);
    }
}
