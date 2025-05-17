package com.example.quizapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREF_NAME = "QuizAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final int QUESTIONS_PER_QUIZ = 5;
    private static final int POINTS_PER_CORRECT_ANSWER = 10;
    private static final long COUNTDOWN_TIME = 30000; 

    private TextView quizTitleTextView;
    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button submitButton;
    private ProgressBar progressBar;
    private TextView progressTextView;
    private TextView timerTextView;
    private CardView questionCard;

    private QuizDatabaseHelper dbHelper;
    private Question currentQuestion;
    private Quiz currentQuiz;
    private int quizId;
    private boolean fromSplash;
    private BroadcastReceiver callReceiver;
    private BroadcastReceiver smsReceiver;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private boolean wasInBackground = false;
    private boolean needNewQuestion = false;

    private String sessionId;

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int score = 0;
    private List<Question> quizQuestions;

    private boolean[] questionAnswered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Log.d(TAG, "onCreate: Initialisation de l'activité");

            quizTitleTextView = findViewById(R.id.quizTitleTextView);
            questionTextView = findViewById(R.id.questionTextView);
            optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
            option1 = findViewById(R.id.option1);
            option2 = findViewById(R.id.option2);
            option3 = findViewById(R.id.option3);
            option4 = findViewById(R.id.option4);
            submitButton = findViewById(R.id.submitButton);
            progressBar = findViewById(R.id.progressBar);
            progressTextView = findViewById(R.id.progressTextView);
            timerTextView = findViewById(R.id.timerTextView);
            questionCard = findViewById(R.id.questionCard);

            dbHelper = new QuizDatabaseHelper(this);

            sessionId = UUID.randomUUID().toString();
            Log.d(TAG, "onCreate: Nouvelle session ID générée: " + sessionId);

            quizId = getIntent().getIntExtra("quiz_id", -1);
            fromSplash = getIntent().getBooleanExtra("from_splash", false);
            Log.d(TAG, "onCreate: Quiz ID: " + quizId + ", fromSplash: " + fromSplash);

            resetQuizProgress();

            if (quizId != -1) {
                currentQuiz = dbHelper.getQuiz(quizId);
                quizTitleTextView.setText(currentQuiz.getTitle());
                Log.d(TAG, "onCreate: Chargement du quiz spécifique: " + currentQuiz.getTitle());

                loadQuizQuestions();
            } else {
                Log.d(TAG, "onCreate: Chargement d'un quiz aléatoire");
                loadRandomQuestions();
            }

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer();
                }
            });

            initializeBroadcastReceivers();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Une erreur s'est produite. Veuillez réessayer.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, QuizListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void resetQuizProgress() {
        Log.d(TAG, "resetQuizProgress: Réinitialisation du score et des réponses correctes");
        score = 0;
        correctAnswers = 0;
        currentQuestionIndex = 0;

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_score", 0);
        editor.putInt("correct_answers", 0);
        editor.putInt("current_question_index", 0);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_all_quizzes) {
            Intent intent = new Intent(this, QuizListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeBroadcastReceivers() {
        callReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) ||
                            state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        Toast.makeText(context, "Phone Call received", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Appel téléphonique détecté, chargement d'une nouvelle question");
                        loadNewQuestion();
                    }
                }
            }
        };

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                    Log.d(TAG, "SMS reçu, chargement d'une nouvelle question");
                    loadNewQuestion();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Log.d(TAG, "onResume: Reprise de l'activité");

            IntentFilter callFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            registerReceiver(callReceiver, callFilter);

            IntentFilter smsFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(smsReceiver, smsFilter);

            if (wasInBackground || needNewQuestion) {
                Log.d(TAG, "onResume: Application revient de l'arrière-plan ou besoin d'une nouvelle question, chargement d'une nouvelle question");
                Toast.makeText(this, "Chargement d'une nouvelle question", Toast.LENGTH_SHORT).show();
                loadNewQuestion();
                wasInBackground = false;
                needNewQuestion = false;
            }

            fromSplash = false;
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Mise en pause de l'activité");
        Toast.makeText(this, "L'application est en pause", Toast.LENGTH_SHORT).show();

        try {
            unregisterReceiver(callReceiver);
            unregisterReceiver(smsReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Receiver not registered: " + e.getMessage());
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
        }

        wasInBackground = true;

        needNewQuestion = true;

        saveQuizState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Arrêt de l'activité");

        saveQuizState();

        wasInBackground = true;

        needNewQuestion = true;
    }

    private void saveQuizState() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_score", score);
        editor.putInt("correct_answers", correctAnswers);
        editor.putInt("current_question_index", currentQuestionIndex);
        editor.apply();

        Log.d(TAG, "saveQuizState: État sauvegardé - Score: " + score + ", Réponses correctes: " + correctAnswers + ", Index question: " + currentQuestionIndex);
    }

    private void loadNewQuestion() {
        Log.d(TAG, "loadNewQuestion: Chargement d'une nouvelle question");

        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
        }

        if (quizId != -1) {
            Question newQuestion = dbHelper.getRandomQuestionFromQuiz(quizId, sessionId,
                    currentQuestion != null ? currentQuestion.getId() : -1);

            if (newQuestion != null) {
                Log.d(TAG, "loadNewQuestion: Nouvelle question chargée: " + newQuestion.getQuestionText());
                if (currentQuestionIndex < quizQuestions.size()) {
                    quizQuestions.set(currentQuestionIndex, newQuestion);
                    currentQuestion = newQuestion;
                    displayQuestion(newQuestion);
                    questionAnswered[currentQuestionIndex] = false;
                }
            } else {
                Log.d(TAG, "loadNewQuestion: Aucune nouvelle question disponible");
                dbHelper.resetQuizSession(quizId, sessionId);
                newQuestion = dbHelper.getRandomQuestionFromQuiz(quizId, sessionId, -1);
                if (newQuestion != null) {
                    Log.d(TAG, "loadNewQuestion: Nouvelle question chargée après réinitialisation: " + newQuestion.getQuestionText());
                    if (currentQuestionIndex < quizQuestions.size()) {
                        quizQuestions.set(currentQuestionIndex, newQuestion);
                        currentQuestion = newQuestion;
                        displayQuestion(newQuestion);
                        questionAnswered[currentQuestionIndex] = false;
                    }
                } else {
                    Log.d(TAG, "loadNewQuestion: Toujours aucune question disponible après réinitialisation");
                }
            }
        } else {
            Question newQuestion = dbHelper.getRandomQuestion();
            if (newQuestion != null) {
                Log.d(TAG, "loadNewQuestion: Nouvelle question aléatoire chargée: " + newQuestion.getQuestionText());
                if (currentQuestionIndex < quizQuestions.size()) {
                    quizQuestions.set(currentQuestionIndex, newQuestion);
                    currentQuestion = newQuestion;
                    displayQuestion(newQuestion);
                    questionAnswered[currentQuestionIndex] = false;
                }
            } else {
                Log.d(TAG, "loadNewQuestion: Aucune nouvelle question aléatoire disponible");
            }
        }
    }

    private void loadQuizQuestions() {
        try {
            Log.d(TAG, "loadQuizQuestions: Chargement des questions pour le quiz " + quizId);

            resetQuizProgress();

            quizQuestions = new ArrayList<>();

            dbHelper.resetQuizSession(quizId, sessionId);

            for (int i = 0; i < QUESTIONS_PER_QUIZ; i++) {
                Question question = dbHelper.getRandomQuestionFromQuiz(quizId, sessionId, -1);
                if (question != null) {
                    quizQuestions.add(question);
                    Log.d(TAG, "loadQuizQuestions: Question ajoutée: " + question.getQuestionText());
                } else {
                    Log.d(TAG, "loadQuizQuestions: Plus de questions disponibles après " + i + " questions");
                    break;
                }
            }

            if (!quizQuestions.isEmpty()) {
                questionAnswered = new boolean[quizQuestions.size()];

                currentQuestionIndex = 0;
                displayQuestion(quizQuestions.get(currentQuestionIndex));
                updateProgress();

                Log.d(TAG, "loadQuizQuestions: " + quizQuestions.size() + " questions chargées");
            } else {
                Log.d(TAG, "loadQuizQuestions: Aucune question disponible pour ce quiz");
                Toast.makeText(this, "Aucune question disponible pour ce quiz", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading quiz questions: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors du chargement des questions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadRandomQuestions() {
        try {
            Log.d(TAG, "loadRandomQuestions: Chargement d'un quiz aléatoire");

            resetQuizProgress();

            quizQuestions = new ArrayList<>();

            List<Quiz> allQuizzes = dbHelper.getAllQuizzes();
            if (allQuizzes.isEmpty()) {
                Log.d(TAG, "loadRandomQuestions: Aucun quiz disponible");
                Toast.makeText(this, "Aucun quiz disponible", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Random random = new Random();
            int randomIndex = random.nextInt(allQuizzes.size());
            Quiz randomQuiz = allQuizzes.get(randomIndex);
            quizId = randomQuiz.getId();

            Log.d(TAG, "loadRandomQuestions: Quiz aléatoire sélectionné: " + randomQuiz.getTitle() + " (ID: " + quizId + ")");

            dbHelper.resetQuizSession(quizId, sessionId);

            for (int i = 0; i < QUESTIONS_PER_QUIZ; i++) {
                Question question = dbHelper.getRandomQuestionFromQuiz(quizId, sessionId, -1);
                if (question != null) {
                    quizQuestions.add(question);
                    Log.d(TAG, "loadRandomQuestions: Question ajoutée: " + question.getQuestionText());
                } else {
                    Log.d(TAG, "loadRandomQuestions: Plus de questions disponibles après " + i + " questions");
                    break;
                }
            }

            if (!quizQuestions.isEmpty()) {
                questionAnswered = new boolean[quizQuestions.size()];

                currentQuestionIndex = 0;
                quizTitleTextView.setText(randomQuiz.getTitle());
                displayQuestion(quizQuestions.get(currentQuestionIndex));
                updateProgress();

                Log.d(TAG, "loadRandomQuestions: " + quizQuestions.size() + " questions chargées");
            } else {
                Log.d(TAG, "loadRandomQuestions: Aucune question disponible");
                Toast.makeText(this, "Aucune question disponible", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading random questions: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors du chargement des questions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayQuestion(Question question) {
        currentQuestion = question;

        Log.d(TAG, "displayQuestion: Affichage de la question: " + question.getQuestionText());

        questionTextView.setText(currentQuestion.getQuestionText());

        String[] options = currentQuestion.getOptions();
        option1.setText(options[0]);
        option2.setText(options[1]);
        option3.setText(options[2]);
        option4.setText(options[3]);

        optionsRadioGroup.clearCheck();

        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0");
                Toast.makeText(MainActivity.this, "Temps écoulé !", Toast.LENGTH_SHORT).show();
                moveToNextQuestion();
            }
        }.start();

        timerRunning = true;
        Log.d(TAG, "startTimer: Timer démarré pour " + COUNTDOWN_TIME / 1000 + " secondes");
    }

    private boolean allQuestionsAnswered() {
        for (boolean answered : questionAnswered) {
            if (!answered) {
                return false;
            }
        }
        return true;
    }

    private void checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT).show();
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
        }

        RadioButton selectedOption = findViewById(selectedId);
        String selectedAnswer = selectedOption.getText().toString();

        Log.d(TAG, "checkAnswer: Réponse sélectionnée: " + selectedAnswer);

        questionAnswered[currentQuestionIndex] = true;

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            correctAnswers++;
            score += POINTS_PER_CORRECT_ANSWER;

            Log.d(TAG, "checkAnswer: Réponse correcte! Score: " + score + ", Réponses correctes: " + correctAnswers);

            if (!currentQuestion.isAnswered()) {
                dbHelper.markQuestionAsAnswered(currentQuestion.getId());
            }
        } else {
            Toast.makeText(this, "Incorrect! La bonne réponse est: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "checkAnswer: Réponse incorrecte. La bonne réponse était: " + currentQuestion.getCorrectAnswer());
        }

        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;

        Log.d(TAG, "moveToNextQuestion: Passage à la question suivante. Nouvel index: " + currentQuestionIndex);

        if (currentQuestionIndex < quizQuestions.size()) {
            displayQuestion(quizQuestions.get(currentQuestionIndex));
            updateProgress();
        } else {
            if (allQuestionsAnswered()) {
                Log.d(TAG, "moveToNextQuestion: Toutes les questions ont été répondues. Fin du quiz.");
                finishQuiz();
            } else {
                for (int i = 0; i < questionAnswered.length; i++) {
                    if (!questionAnswered[i]) {
                        currentQuestionIndex = i;
                        Log.d(TAG, "moveToNextQuestion: Question non répondue trouvée à l'index " + i + ". Retour à cette question.");
                        displayQuestion(quizQuestions.get(currentQuestionIndex));
                        updateProgress();
                        Toast.makeText(this, "Veuillez répondre à toutes les questions", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    }

    private void updateProgress() {
        progressBar.setMax(quizQuestions.size());
        progressBar.setProgress(currentQuestionIndex + 1);

        progressTextView.setText((currentQuestionIndex + 1) + "/" + quizQuestions.size() + " questions");

        Log.d(TAG, "updateProgress: Progression mise à jour: " + (currentQuestionIndex + 1) + "/" + quizQuestions.size());
    }

    private void finishQuiz() {
        Log.d(TAG, "finishQuiz: Fin du quiz. Score final: " + score + ", Réponses correctes: " + correctAnswers);

        Intent intent = new Intent(MainActivity.this, QuizResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("correct_answers", correctAnswers);
        intent.putExtra("total_questions", quizQuestions.size());
        intent.putExtra("quiz_id", quizId);
        intent.putExtra("quiz_title", quizTitleTextView.getText().toString());
        startActivity(intent);
        finish();
    }
}
