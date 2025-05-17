package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;

import java.util.List;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; 
    private QuizDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        dbHelper = new QuizDatabaseHelper(this);
        
        if (dbHelper.getQuizzesCount() == 0) {
            populateSampleQuizzesAndQuestions();
        }
        
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("QuizAppPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
                
                if (!isLoggedIn) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                
                List<Quiz> quizzes = dbHelper.getAllQuizzes();
                
                if (quizzes.isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, QuizListActivity.class);
                    startActivity(intent);
                } else {
                    Random random = new Random();
                    int randomIndex = random.nextInt(quizzes.size());
                    Quiz randomQuiz = quizzes.get(randomIndex);
                    
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("quiz_id", randomQuiz.getId());
                    intent.putExtra("from_splash", true);
                    startActivity(intent);
                }
                
                finish();
            }
        }, SPLASH_DURATION);
    }
    
    private int getRandomQuizId() {
        List<Quiz> quizzes = dbHelper.getAllQuizzes();
        if (quizzes.isEmpty()) {
            return -1;
        }
        
        Random random = new Random();
        int randomIndex = random.nextInt(quizzes.size());
        return quizzes.get(randomIndex).getId();
    }
    
    private void populateSampleQuizzesAndQuestions() {
        Quiz historyQuiz = new Quiz();
        historyQuiz.setTitle("Histoire de l'Algérie");
        historyQuiz.setDescription("Questions sur l'histoire algérienne");
        historyQuiz.setTotalQuestions(5);
        historyQuiz.setCompletedQuestions(0);
        long historyQuizId = dbHelper.addQuiz(historyQuiz);
        
        String[] options1 = {"1954", "1962", "1965", "1971"};
        Question q1 = new Question("En quelle année l'Algérie a-t-elle obtenu son indépendance?", options1, "1962");
        q1.setQuizId((int) historyQuizId);
        dbHelper.addQuestion(q1);
        
        String[] options2 = {"Ahmed Ben Bella", "Houari Boumédiène", "Chadli Bendjedid", "Abdelaziz Bouteflika"};
        Question q2 = new Question("Qui était le premier président de l'Algérie indépendante?", options2, "Ahmed Ben Bella");
        q2.setQuizId((int) historyQuizId);
        dbHelper.addQuestion(q2);
        
        String[] options3 = {"FLN", "ALN", "OAS", "MNA"};
        Question q3 = new Question("Quel était le nom du principal mouvement de libération nationale algérien?", options3, "FLN");
        q3.setQuizId((int) historyQuizId);
        dbHelper.addQuestion(q3);
        
        String[] options4 = {"Bataille d'Alger", "Bataille de Constantine", "Bataille de Sidi-Bel-Abbès", "Toussaint Rouge"};
        Question q4 = new Question("Quelle bataille a marqué le début de la guerre d'indépendance algérienne?", options4, "Toussaint Rouge");
        q4.setQuizId((int) historyQuizId);
        dbHelper.addQuestion(q4);
        
        String[] options5 = {"Accords d'Évian", "Accords de Genève", "Accords de Paris", "Accords d'Alger"};
        Question q5 = new Question("Quel accord a mis fin à la guerre d'Algérie?", options5, "Accords d'Évian");
        q5.setQuizId((int) historyQuizId);
        dbHelper.addQuestion(q5);
        
        Quiz geographyQuiz = new Quiz();
        geographyQuiz.setTitle("Géographie de l'Algérie");
        geographyQuiz.setDescription("Questions sur la géographie algérienne");
        geographyQuiz.setTotalQuestions(5);
        geographyQuiz.setCompletedQuestions(0);
        long geographyQuizId = dbHelper.addQuiz(geographyQuiz);
        
        String[] options6 = {"Oran", "Constantine", "Alger", "Annaba"};
        Question q6 = new Question("Quelle est la capitale de l'Algérie?", options6, "Alger");
        q6.setQuizId((int) geographyQuizId);
        dbHelper.addQuestion(q6);
        
        String[] options7 = {"Sahara", "Tanezrouft", "Grand Erg Oriental", "Grand Erg Occidental"};
        Question q7 = new Question("Quel est le plus grand désert d'Algérie?", options7, "Sahara");
        q7.setQuizId((int) geographyQuizId);
        dbHelper.addQuestion(q7);
        
        String[] options8 = {"Mont Tahat", "Djebel Chélia", "Lalla Khedidja", "Mont Aiguille"};
        Question q8 = new Question("Quel est le point culminant de l'Algérie?", options8, "Mont Tahat");
        q8.setQuizId((int) geographyQuizId);
        dbHelper.addQuestion(q8);
        
        String[] options9 = {"Mer Méditerranée", "Mer Rouge", "Océan Atlantique", "Mer Noire"};
        Question q9 = new Question("Quelle mer borde l'Algérie au nord?", options9, "Mer Méditerranée");
        q9.setQuizId((int) geographyQuizId);
        dbHelper.addQuestion(q9);
        
        String[] options10 = {"Alger", "Oran", "Annaba", "Béjaïa"};
        Question q10 = new Question("Quelle est la plus grande ville portuaire d'Algérie?", options10, "Oran");
        q10.setQuizId((int) geographyQuizId);
        dbHelper.addQuestion(q10);
        
        Quiz cultureQuiz = new Quiz();
        cultureQuiz.setTitle("Culture Algérienne");
        cultureQuiz.setDescription("Questions sur la culture et les traditions algériennes");
        cultureQuiz.setTotalQuestions(5);
        cultureQuiz.setCompletedQuestions(0);
        long cultureQuizId = dbHelper.addQuiz(cultureQuiz);
        
        String[] options11 = {"Couscous", "Chorba", "Rechta", "Chakhchoukha"};
        Question q11 = new Question("Quel est le plat national algérien?", options11, "Couscous");
        q11.setQuizId((int) cultureQuizId);
        dbHelper.addQuestion(q11);
        
        String[] options12 = {"Arabe", "Français", "Berbère", "Arabe et Berbère"};
        Question q12 = new Question("Quelle est la langue officielle de l'Algérie?", options12, "Arabe et Berbère");
        q12.setQuizId((int) cultureQuizId);
        dbHelper.addQuestion(q12);
        
        String[] options13 = {"Kateb Yacine", "Mohammed Dib", "Assia Djebar", "Mouloud Feraoun"};
        Question q13 = new Question("Quel célèbre écrivain algérien a écrit 'Nedjma'?", options13, "Kateb Yacine");
        q13.setQuizId((int) cultureQuizId);
        dbHelper.addQuestion(q13);
        
        String[] options14 = {"Raï", "Gnawa", "Chaabi", "Tous ces styles"};
        Question q14 = new Question("Quel style musical traditionnel est originaire de l'Algérie?", options14, "Tous ces styles");
        q14.setQuizId((int) cultureQuizId);
        dbHelper.addQuestion(q14);
        
        String[] options15 = {"5 juillet", "1er novembre", "19 mars", "18 février"};
        Question q15 = new Question("Quelle est la fête nationale de l'Algérie?", options15, "5 juillet");
        q15.setQuizId((int) cultureQuizId);
        dbHelper.addQuestion(q15);
    }
}
