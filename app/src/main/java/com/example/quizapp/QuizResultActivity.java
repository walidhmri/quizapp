package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.database.UserDatabaseHelper;
import com.example.quizapp.model.User;

public class QuizResultActivity extends AppCompatActivity {

    private static final String TAG = "QuizResultActivity";
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USER_ID = "user_id";

    private TextView scoreTextView;
    private TextView correctAnswersTextView;
    private TextView quizTitleTextView;
    private TextView congratsTextView;
    private Button returnToListButton;
    private Button retryQuizButton;

    private int score;
    private int correctAnswers;
    private int totalQuestions;
    private int quizId;
    private String quizTitle;
    private UserDatabaseHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        scoreTextView = findViewById(R.id.scoreTextView);
        correctAnswersTextView = findViewById(R.id.correctAnswersTextView);
        quizTitleTextView = findViewById(R.id.quizTitleTextView);
        congratsTextView = findViewById(R.id.congratsTextView);
        returnToListButton = findViewById(R.id.returnToListButton);
        retryQuizButton = findViewById(R.id.retryQuizButton);

        userDbHelper = new UserDatabaseHelper(this);

        score = getIntent().getIntExtra("score", 0);
        correctAnswers = getIntent().getIntExtra("correct_answers", 0);
        totalQuestions = getIntent().getIntExtra("total_questions", 0);
        quizId = getIntent().getIntExtra("quiz_id", -1);
        quizTitle = getIntent().getStringExtra("quiz_title");
        
        Log.d(TAG, "onCreate: Score: " + score + ", Réponses correctes: " + correctAnswers + 
              ", Total questions: " + totalQuestions + ", Quiz ID: " + quizId);

        scoreTextView.setText(String.valueOf(score));
        correctAnswersTextView.setText(correctAnswers + "/" + totalQuestions);
        quizTitleTextView.setText(quizTitle);

        if (correctAnswers == totalQuestions) {
            congratsTextView.setText("Félicitations ! Score parfait !");
        } else if ((double) correctAnswers / totalQuestions >= 0.7) {
            congratsTextView.setText("Bien joué ! Vous avez réussi !");
        } else if ((double) correctAnswers / totalQuestions >= 0.5) {
            congratsTextView.setText("Pas mal ! Continuez à vous améliorer !");
        } else {
            congratsTextView.setText("Vous pouvez faire mieux ! Réessayez !");
        }

        updateUserScore();

        returnToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToQuizList();
            }
        });

        retryQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryQuiz();
            }
        });
        
        resetSharedPreferences();
    }
    
    private void resetSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_score", 0);
        editor.putInt("correct_answers", 0);
        editor.putInt("current_question_index", 0);
        editor.apply();
        
        Log.d(TAG, "resetSharedPreferences: Préférences partagées réinitialisées");
    }

    private void updateUserScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (userId != -1) {
            userDbHelper.updateUserScore(userId, score);
            Log.d(TAG, "updateUserScore: Score de l'utilisateur mis à jour. User ID: " + userId + ", Score: " + score);
        }
    }

    private void navigateToQuizList() {
        Intent intent = new Intent(QuizResultActivity.this, QuizListActivity.class);
        startActivity(intent);
        finish();
    }

    private void retryQuiz() {
        Intent intent = new Intent(QuizResultActivity.this, MainActivity.class);
        intent.putExtra("quiz_id", quizId);
        startActivity(intent);
        finish();
    }
}
