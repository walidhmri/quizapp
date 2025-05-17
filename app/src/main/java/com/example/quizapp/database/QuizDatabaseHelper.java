package com.example.quizapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "QuizDatabaseHelper";
    private static final String DATABASE_NAME = "quiz_app.db";
    private static final int DATABASE_VERSION = 2;


    private static final String TABLE_QUIZZES = "quizzes";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_QUIZ_PROGRESS = "quiz_progress";


    private static final String KEY_ID = "id";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TOTAL_QUESTIONS = "total_questions";
    private static final String KEY_COMPLETED_QUESTIONS = "completed_questions";


    private static final String KEY_QUIZ_ID = "quiz_id";
    private static final String KEY_QUESTION_TEXT = "question_text";
    private static final String KEY_OPTION1 = "option1";
    private static final String KEY_OPTION2 = "option2";
    private static final String KEY_OPTION3 = "option3";
    private static final String KEY_OPTION4 = "option4";
    private static final String KEY_CORRECT_ANSWER = "correct_answer";
    private static final String KEY_ANSWERED = "answered";


    private static final String KEY_QUESTION_ID = "question_id";
    private static final String KEY_ASKED = "asked";
    private static final String KEY_SESSION_ID = "session_id";

    public QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            String CREATE_QUIZZES_TABLE = "CREATE TABLE " + TABLE_QUIZZES + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_TOTAL_QUESTIONS + " INTEGER,"
                    + KEY_COMPLETED_QUESTIONS + " INTEGER" + ")";

            // Create questions table
            String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_QUIZ_ID + " INTEGER,"
                    + KEY_QUESTION_TEXT + " TEXT,"
                    + KEY_OPTION1 + " TEXT,"
                    + KEY_OPTION2 + " TEXT,"
                    + KEY_OPTION3 + " TEXT,"
                    + KEY_OPTION4 + " TEXT,"
                    + KEY_CORRECT_ANSWER + " TEXT,"
                    + KEY_ANSWERED + " INTEGER DEFAULT 0" + ")";


            String CREATE_QUIZ_PROGRESS_TABLE = "CREATE TABLE " + TABLE_QUIZ_PROGRESS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_QUIZ_ID + " INTEGER,"
                    + KEY_QUESTION_ID + " INTEGER,"
                    + KEY_ASKED + " INTEGER DEFAULT 0,"
                    + KEY_SESSION_ID + " TEXT" + ")";

            db.execSQL(CREATE_QUIZZES_TABLE);
            db.execSQL(CREATE_QUESTIONS_TABLE);
            db.execSQL(CREATE_QUIZ_PROGRESS_TABLE);

            Log.d(TAG, "Tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        try {
            if (oldVersion < 2) {
                String CREATE_QUIZ_PROGRESS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_QUIZ_PROGRESS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_QUIZ_ID + " INTEGER,"
                        + KEY_QUESTION_ID + " INTEGER,"
                        + KEY_ASKED + " INTEGER DEFAULT 0,"
                        + KEY_SESSION_ID + " TEXT" + ")";

                db.execSQL(CREATE_QUIZ_PROGRESS_TABLE);
                Log.d(TAG, "Created quiz_progress table during upgrade");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a new quiz
    public long addQuiz(Quiz quiz) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, quiz.getTitle());
        values.put(KEY_DESCRIPTION, quiz.getDescription());
        values.put(KEY_TOTAL_QUESTIONS, quiz.getTotalQuestions());
        values.put(KEY_COMPLETED_QUESTIONS, quiz.getCompletedQuestions());

        // Insert row
        long id = db.insert(TABLE_QUIZZES, null, values);
        db.close();
        return id;
    }

    // Add a new question
    public long addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUIZ_ID, question.getQuizId());
        values.put(KEY_QUESTION_TEXT, question.getQuestionText());
        values.put(KEY_OPTION1, question.getOptions()[0]);
        values.put(KEY_OPTION2, question.getOptions()[1]);
        values.put(KEY_OPTION3, question.getOptions()[2]);
        values.put(KEY_OPTION4, question.getOptions()[3]);
        values.put(KEY_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(KEY_ANSWERED, question.isAnswered() ? 1 : 0);

        // Insert row
        long id = db.insert(TABLE_QUESTIONS, null, values);
        db.close();
        return id;
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_QUIZZES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Quiz quiz = new Quiz();
                quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                quiz.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                quiz.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                quiz.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_QUESTIONS)));
                quiz.setCompletedQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COMPLETED_QUESTIONS)));

                quizList.add(quiz);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return quizList;
    }

    // Get a quiz by ID
    public Quiz getQuiz(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_QUIZZES, new String[] { KEY_ID, KEY_TITLE, KEY_DESCRIPTION,
                        KEY_TOTAL_QUESTIONS, KEY_COMPLETED_QUESTIONS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Quiz quiz = new Quiz();
        quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        quiz.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        quiz.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        quiz.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_QUESTIONS)));
        quiz.setCompletedQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COMPLETED_QUESTIONS)));

        cursor.close();
        db.close();
        return quiz;
    }

    // Get questions for a specific quiz
    public List<Question> getQuestionsForQuiz(int quizId) {
        List<Question> questionList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_QUIZ_ID + " = " + quizId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                question.setQuizId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZ_ID)));
                question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(KEY_QUESTION_TEXT)));

                String[] options = new String[4];
                options[0] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION1));
                options[1] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION2));
                options[2] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION3));
                options[3] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION4));
                question.setOptions(options);

                question.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CORRECT_ANSWER)));
                question.setAnswered(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ANSWERED)) == 1);

                questionList.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return questionList;
    }

    // Get a random question from a specific quiz that hasn't been asked in the current session
    public Question getRandomQuestionFromQuiz(int quizId, String sessionId, int currentQuestionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Question question = null;

        try {
            Log.d(TAG, "getRandomQuestionFromQuiz: Recherche d'une question aléatoire pour le quiz " + quizId + " (session: " + sessionId + ")");

            // Vérifier si la table quiz_progress existe
            Cursor tableCheck = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_QUIZ_PROGRESS + "'",
                    null);
            boolean tableExists = tableCheck.moveToFirst();
            tableCheck.close();

            if (!tableExists) {
                // Si la table n'existe pas, créez-la
                String CREATE_QUIZ_PROGRESS_TABLE = "CREATE TABLE " + TABLE_QUIZ_PROGRESS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_QUIZ_ID + " INTEGER,"
                        + KEY_QUESTION_ID + " INTEGER,"
                        + KEY_ASKED + " INTEGER DEFAULT 0,"
                        + KEY_SESSION_ID + " TEXT" + ")";

                db.execSQL(CREATE_QUIZ_PROGRESS_TABLE);
                Log.d(TAG, "getRandomQuestionFromQuiz: Created quiz_progress table on-the-fly");
            }

            // Obtenir les IDs des questions déjà posées dans cette session
            String progressQuery = "SELECT " + KEY_QUESTION_ID + " FROM " + TABLE_QUIZ_PROGRESS +
                    " WHERE " + KEY_QUIZ_ID + " = " + quizId +
                    " AND " + KEY_SESSION_ID + " = '" + sessionId + "'";

            Cursor progressCursor = db.rawQuery(progressQuery, null);

            StringBuilder excludeIds = new StringBuilder();
            if (progressCursor.moveToFirst()) {
                excludeIds.append(KEY_ID + " NOT IN (");
                boolean first = true;
                do {
                    if (!first) {
                        excludeIds.append(",");
                    }
                    excludeIds.append(progressCursor.getInt(progressCursor.getColumnIndexOrThrow(KEY_QUESTION_ID)));
                    first = false;
                } while (progressCursor.moveToNext());
                excludeIds.append(")");
            }
            progressCursor.close();

            // Exclure également la question actuelle si elle est spécifiée
            if (currentQuestionId > 0) {
                if (excludeIds.length() > 0) {
                    excludeIds.append(" AND ");
                }
                excludeIds.append(KEY_ID + " != " + currentQuestionId);
            }

            // Construire la requête pour obtenir une question non posée
            String query;
            if (excludeIds.length() > 0) {
                query = "SELECT * FROM " + TABLE_QUESTIONS +
                        " WHERE " + KEY_QUIZ_ID + " = " + quizId +
                        " AND " + excludeIds.toString() +
                        " ORDER BY RANDOM() LIMIT 1";
            } else {
                query = "SELECT * FROM " + TABLE_QUESTIONS +
                        " WHERE " + KEY_QUIZ_ID + " = " + quizId;
                if (currentQuestionId > 0) {
                    query += " AND " + KEY_ID + " != " + currentQuestionId;
                }
                query += " ORDER BY RANDOM() LIMIT 1";
            }

            Log.d(TAG, "getRandomQuestionFromQuiz: Requête SQL: " + query);

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                question.setQuizId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZ_ID)));
                question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(KEY_QUESTION_TEXT)));

                String[] options = new String[4];
                options[0] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION1));
                options[1] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION2));
                options[2] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION3));
                options[3] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION4));
                question.setOptions(options);

                question.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CORRECT_ANSWER)));
                question.setAnswered(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ANSWERED)) == 1);

                Log.d(TAG, "getRandomQuestionFromQuiz: Question trouvée: " + question.getQuestionText());
            } else {
                Log.d(TAG, "getRandomQuestionFromQuiz: Aucune question trouvée");

                // Si aucune question n'est trouvée, essayer de réinitialiser la session
                resetQuizSession(quizId, sessionId);

                // Et réessayer avec une requête simple
                String retryQuery = "SELECT * FROM " + TABLE_QUESTIONS +
                        " WHERE " + KEY_QUIZ_ID + " = " + quizId;
                if (currentQuestionId > 0) {
                    retryQuery += " AND " + KEY_ID + " != " + currentQuestionId;
                }
                retryQuery += " ORDER BY RANDOM() LIMIT 1";

                Log.d(TAG, "getRandomQuestionFromQuiz: Requête de seconde chance: " + retryQuery);

                Cursor retryCursor = db.rawQuery(retryQuery, null);

                if (retryCursor.moveToFirst()) {
                    question = new Question();
                    question.setId(retryCursor.getInt(retryCursor.getColumnIndexOrThrow(KEY_ID)));
                    question.setQuizId(retryCursor.getInt(retryCursor.getColumnIndexOrThrow(KEY_QUIZ_ID)));
                    question.setQuestionText(retryCursor.getString(retryCursor.getColumnIndexOrThrow(KEY_QUESTION_TEXT)));

                    String[] options = new String[4];
                    options[0] = retryCursor.getString(retryCursor.getColumnIndexOrThrow(KEY_OPTION1));
                    options[1] = retryCursor.getString(retryCursor.getColumnIndexOrThrow(KEY_OPTION2));
                    options[2] = retryCursor.getString(retryCursor.getColumnIndexOrThrow(KEY_OPTION3));
                    options[3] = retryCursor.getString(retryCursor.getColumnIndexOrThrow(KEY_OPTION4));
                    question.setOptions(options);

                    question.setCorrectAnswer(retryCursor.getString(retryCursor.getColumnIndexOrThrow(KEY_CORRECT_ANSWER)));
                    question.setAnswered(retryCursor.getInt(retryCursor.getColumnIndexOrThrow(KEY_ANSWERED)) == 1);

                    Log.d(TAG, "getRandomQuestionFromQuiz: Question trouvée après réinitialisation: " + question.getQuestionText());
                } else {
                    Log.d(TAG, "getRandomQuestionFromQuiz: Toujours aucune question trouvée après réinitialisation");
                }

                retryCursor.close();
            }

            cursor.close();

            // Marquer cette question comme posée dans cette session
            if (question != null) {
                markQuestionAsAsked(quizId, question.getId(), sessionId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getRandomQuestionFromQuiz: " + e.getMessage());
            e.printStackTrace();

            // Fallback: obtenir une question aléatoire sans utiliser quiz_progress
            try {
                String fallbackQuery = "SELECT * FROM " + TABLE_QUESTIONS +
                        " WHERE " + KEY_QUIZ_ID + " = " + quizId;
                if (currentQuestionId > 0) {
                    fallbackQuery += " AND " + KEY_ID + " != " + currentQuestionId;
                }
                fallbackQuery += " ORDER BY RANDOM() LIMIT 1";

                Log.d(TAG, "getRandomQuestionFromQuiz: Requête fallback: " + fallbackQuery);

                Cursor fallbackCursor = db.rawQuery(fallbackQuery, null);

                if (fallbackCursor.moveToFirst()) {
                    question = new Question();
                    question.setId(fallbackCursor.getInt(fallbackCursor.getColumnIndexOrThrow(KEY_ID)));
                    question.setQuizId(fallbackCursor.getInt(fallbackCursor.getColumnIndexOrThrow(KEY_QUIZ_ID)));
                    question.setQuestionText(fallbackCursor.getString(fallbackCursor.getColumnIndexOrThrow(KEY_QUESTION_TEXT)));

                    String[] options = new String[4];
                    options[0] = fallbackCursor.getString(fallbackCursor.getColumnIndexOrThrow(KEY_OPTION1));
                    options[1] = fallbackCursor.getString(fallbackCursor.getColumnIndexOrThrow(KEY_OPTION2));
                    options[2] = fallbackCursor.getString(fallbackCursor.getColumnIndexOrThrow(KEY_OPTION3));
                    options[3] = fallbackCursor.getString(fallbackCursor.getColumnIndexOrThrow(KEY_OPTION4));
                    question.setOptions(options);

                    question.setCorrectAnswer(fallbackCursor.getString(fallbackCursor.getColumnIndexOrThrow(KEY_CORRECT_ANSWER)));
                    question.setAnswered(fallbackCursor.getInt(fallbackCursor.getColumnIndexOrThrow(KEY_ANSWERED)) == 1);

                    Log.d(TAG, "getRandomQuestionFromQuiz: Question fallback trouvée: " + question.getQuestionText());
                } else {
                    Log.d(TAG, "getRandomQuestionFromQuiz: Aucune question fallback trouvée");
                }

                fallbackCursor.close();
            } catch (Exception ex) {
                Log.e(TAG, "Error in fallback query: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        db.close();
        return question;
    }

    // Mark a question as asked in a session
    public void markQuestionAsAsked(int quizId, int questionId, String sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Log.d(TAG, "markQuestionAsAsked: Marquage de la question " + questionId + " comme posée (quiz: " + quizId + ", session: " + sessionId + ")");

            // Vérifier si la table quiz_progress existe
            Cursor tableCheck = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_QUIZ_PROGRESS + "'",
                    null);
            boolean tableExists = tableCheck.moveToFirst();
            tableCheck.close();

            if (!tableExists) {
                // Si la table n'existe pas, sortir de la méthode
                Log.d(TAG, "markQuestionAsAsked: Table quiz_progress does not exist, skipping markQuestionAsAsked");
                db.close();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(KEY_QUIZ_ID, quizId);
            values.put(KEY_QUESTION_ID, questionId);
            values.put(KEY_ASKED, 1);
            values.put(KEY_SESSION_ID, sessionId);

            // Check if this question is already in the progress table
            Cursor cursor = db.query(TABLE_QUIZ_PROGRESS,
                    new String[] { KEY_ID },
                    KEY_QUIZ_ID + " = ? AND " + KEY_QUESTION_ID + " = ? AND " + KEY_SESSION_ID + " = ?",
                    new String[] { String.valueOf(quizId), String.valueOf(questionId), sessionId },
                    null, null, null);

            if (cursor.moveToFirst()) {
                // Update existing record
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
                db.update(TABLE_QUIZ_PROGRESS, values, KEY_ID + " = ?", new String[] { String.valueOf(id) });
                Log.d(TAG, "markQuestionAsAsked: Question déjà marquée, mise à jour effectuée");
            } else {
                // Insert new record
                db.insert(TABLE_QUIZ_PROGRESS, null, values);
                Log.d(TAG, "markQuestionAsAsked: Question marquée comme posée");
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error in markQuestionAsAsked: " + e.getMessage());
            e.printStackTrace();
        }

        db.close();
    }

    // Reset a quiz session
    public void resetQuizSession(int quizId, String sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Log.d(TAG, "resetQuizSession: Réinitialisation de la session " + sessionId + " pour le quiz " + quizId);

            // Vérifier si la table quiz_progress existe
            Cursor tableCheck = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_QUIZ_PROGRESS + "'",
                    null);
            boolean tableExists = tableCheck.moveToFirst();
            tableCheck.close();

            if (!tableExists) {
                // Si la table n'existe pas, sortir de la méthode
                Log.d(TAG, "resetQuizSession: Table quiz_progress does not exist, skipping resetQuizSession");
                db.close();
                return;
            }

            // Delete all progress records for this quiz and session
            int deleted = db.delete(TABLE_QUIZ_PROGRESS,
                    KEY_QUIZ_ID + " = ? AND " + KEY_SESSION_ID + " = ?",
                    new String[] { String.valueOf(quizId), sessionId });

            Log.d(TAG, "resetQuizSession: " + deleted + " enregistrements supprimés");
        } catch (Exception e) {
            Log.e(TAG, "Error in resetQuizSession: " + e.getMessage());
            e.printStackTrace();
        }

        db.close();
    }

    // Get a random question from any quiz
    public Question getRandomQuestion() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_QUESTIONS + " ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        Question question = null;

        if (cursor.moveToFirst()) {
            question = new Question();
            question.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            question.setQuizId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZ_ID)));
            question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(KEY_QUESTION_TEXT)));

            String[] options = new String[4];
            options[0] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION1));
            options[1] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION2));
            options[2] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION3));
            options[3] = cursor.getString(cursor.getColumnIndexOrThrow(KEY_OPTION4));
            question.setOptions(options);

            question.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CORRECT_ANSWER)));
            question.setAnswered(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ANSWERED)) == 1);
        }

        cursor.close();
        db.close();
        return question;
    }

    // Mark a question as answered
    public void markQuestionAsAnswered(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ANSWERED, 1);

        // Update row
        db.update(TABLE_QUESTIONS, values, KEY_ID + " = ?", new String[] { String.valueOf(questionId) });

        // Get the quiz ID for this question
        Cursor cursor = db.query(TABLE_QUESTIONS, new String[] { KEY_QUIZ_ID },
                KEY_ID + " = ?", new String[] { String.valueOf(questionId) },
                null, null, null);

        if (cursor.moveToFirst()) {
            int quizId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZ_ID));

            // Increment completed questions for this quiz
            incrementCompletedQuestions(quizId);
        }

        cursor.close();
        db.close();
    }

    // Increment completed questions for a quiz
    private void incrementCompletedQuestions(int quizId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get current completed questions
        Cursor cursor = db.query(TABLE_QUIZZES, new String[] { KEY_COMPLETED_QUESTIONS },
                KEY_ID + " = ?", new String[] { String.valueOf(quizId) },
                null, null, null);

        if (cursor.moveToFirst()) {
            int completedQuestions = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COMPLETED_QUESTIONS));

            // Increment and update
            ContentValues values = new ContentValues();
            values.put(KEY_COMPLETED_QUESTIONS, completedQuestions + 1);

            db.update(TABLE_QUIZZES, values, KEY_ID + " = ?", new String[] { String.valueOf(quizId) });
        }

        cursor.close();
        db.close();
    }

    // Get the count of questions
    public int getQuestionsCount() {
        String countQuery = "SELECT * FROM " + TABLE_QUESTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Get the count of quizzes
    public int getQuizzesCount() {
        String countQuery = "SELECT * FROM " + TABLE_QUIZZES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }


    public void updateQuizTotalQuestions(int quizId, int totalQuestions) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TOTAL_QUESTIONS, totalQuestions);

        // Update row
        db.update(TABLE_QUIZZES, values, KEY_ID + " = ?", new String[] { String.valueOf(quizId) });
        db.close();

        Log.d(TAG, "updateQuizTotalQuestions: Quiz " + quizId + " mis à jour avec " + totalQuestions + " questions");
    }
}
