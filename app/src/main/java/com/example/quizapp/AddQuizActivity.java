package com.example.quizapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;

public class AddQuizActivity extends AppCompatActivity {

    private static final String TAG = "AddQuizActivity";

    private EditText quizTitleEditText;
    private EditText quizDescriptionEditText;
    private EditText questionTextEditText;
    private EditText option1EditText;
    private EditText option2EditText;
    private EditText option3EditText;
    private EditText option4EditText;
    private EditText correctAnswerEditText;
    private Button addQuestionButton;
    private Button saveQuizButton;

    private QuizDatabaseHelper dbHelper;
    private Quiz currentQuiz;
    private int questionsAdded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

        quizTitleEditText = findViewById(R.id.quizTitleEditText);
        quizDescriptionEditText = findViewById(R.id.quizDescriptionEditText);
        questionTextEditText = findViewById(R.id.questionTextEditText);
        option1EditText = findViewById(R.id.option1EditText);
        option2EditText = findViewById(R.id.option2EditText);
        option3EditText = findViewById(R.id.option3EditText);
        option4EditText = findViewById(R.id.option4EditText);
        correctAnswerEditText = findViewById(R.id.correctAnswerEditText);
        addQuestionButton = findViewById(R.id.addQuestionButton);
        saveQuizButton = findViewById(R.id.saveQuizButton);

        dbHelper = new QuizDatabaseHelper(this);

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion();
            }
        });

        saveQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuiz();
            }
        });
    }

    private void addQuestion() {
        if (currentQuiz == null) {
            String title = quizTitleEditText.getText().toString().trim();
            String description = quizDescriptionEditText.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                quizTitleEditText.setError("Veuillez entrer un titre pour le quiz");
                return;
            }

            currentQuiz = new Quiz();
            currentQuiz.setTitle(title);
            currentQuiz.setDescription(description);
            currentQuiz.setTotalQuestions(0);
            currentQuiz.setCompletedQuestions(0);

            long quizId = dbHelper.addQuiz(currentQuiz);
            if (quizId == -1) {
                Toast.makeText(this, "Erreur lors de la création du quiz", Toast.LENGTH_SHORT).show();
                return;
            }
            currentQuiz.setId((int) quizId);

            quizTitleEditText.setEnabled(false);
            quizDescriptionEditText.setEnabled(false);
        }

        String questionText = questionTextEditText.getText().toString().trim();
        String option1 = option1EditText.getText().toString().trim();
        String option2 = option2EditText.getText().toString().trim();
        String option3 = option3EditText.getText().toString().trim();
        String option4 = option4EditText.getText().toString().trim();
        String correctAnswer = correctAnswerEditText.getText().toString().trim();

        if (TextUtils.isEmpty(questionText)) {
            questionTextEditText.setError("Veuillez entrer une question");
            return;
        }

        if (TextUtils.isEmpty(option1) || TextUtils.isEmpty(option2) || 
            TextUtils.isEmpty(option3) || TextUtils.isEmpty(option4)) {
            Toast.makeText(this, "Veuillez remplir toutes les options", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(correctAnswer)) {
            correctAnswerEditText.setError("Veuillez entrer la réponse correcte");
            return;
        }

        if (!correctAnswer.equals(option1) && !correctAnswer.equals(option2) && 
            !correctAnswer.equals(option3) && !correctAnswer.equals(option4)) {
            correctAnswerEditText.setError("La réponse correcte doit correspondre à l'une des options");
            return;
        }

        String[] options = {option1, option2, option3, option4};
        Question question = new Question(questionText, options, correctAnswer);
        question.setQuizId(currentQuiz.getId());

        long questionId = dbHelper.addQuestion(question);
        if (questionId == -1) {
            Toast.makeText(this, "Erreur lors de l'ajout de la question", Toast.LENGTH_SHORT).show();
            return;
        }

        currentQuiz.setTotalQuestions(currentQuiz.getTotalQuestions() + 1);
        dbHelper.updateQuizTotalQuestions(currentQuiz.getId(), currentQuiz.getTotalQuestions());

        questionTextEditText.setText("");
        option1EditText.setText("");
        option2EditText.setText("");
        option3EditText.setText("");
        option4EditText.setText("");
        correctAnswerEditText.setText("");

        questionsAdded++;

        Toast.makeText(this, "Question ajoutée avec succès", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Question ajoutée. Total: " + questionsAdded);
    }

    private void saveQuiz() {
        if (currentQuiz == null) {
            Toast.makeText(this, "Veuillez d'abord créer un quiz et ajouter des questions", Toast.LENGTH_SHORT).show();
            return;
        }

        if (questionsAdded == 0) {
            Toast.makeText(this, "Veuillez ajouter au moins une question", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Quiz enregistré avec succès", Toast.LENGTH_SHORT).show();
        finish();
    }
}
