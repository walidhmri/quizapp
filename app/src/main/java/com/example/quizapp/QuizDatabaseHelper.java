package com.example.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table name
    private static final String TABLE_QUESTIONS = "questions";
    
    // Table columns
    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION_TEXT = "question_text";
    private static final String KEY_OPTION1 = "option1";
    private static final String KEY_OPTION2 = "option2";
    private static final String KEY_OPTION3 = "option3";
    private static final String KEY_OPTION4 = "option4";
    private static final String KEY_CORRECT_ANSWER = "correct_answer";
    
    public QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create questions table
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_QUESTION_TEXT + " TEXT,"
                + KEY_OPTION1 + " TEXT,"
                + KEY_OPTION2 + " TEXT,"
                + KEY_OPTION3 + " TEXT,"
                + KEY_OPTION4 + " TEXT,"
                + KEY_CORRECT_ANSWER + " TEXT" + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        
        // Create tables again
        onCreate(db);
    }
    
    // Add a new question
    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_TEXT, question.getQuestionText());
        values.put(KEY_OPTION1, question.getOptions()[0]);
        values.put(KEY_OPTION2, question.getOptions()[1]);
        values.put(KEY_OPTION3, question.getOptions()[2]);
        values.put(KEY_OPTION4, question.getOptions()[3]);
        values.put(KEY_CORRECT_ANSWER, question.getCorrectAnswer());
        
        // Insert row
        db.insert(TABLE_QUESTIONS, null, values);
        db.close();
    }
    
    // Get a random question
    public Question getRandomQuestion() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Utilisation explicite de ORDER BY RANDOM() comme spécifié dans le cahier des charges
        String query = "SELECT * FROM " + TABLE_QUESTIONS + " ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        
        Question question = null;
        
        if (cursor.moveToFirst()) {
            String questionText = cursor.getString(cursor.getColumnIndex(KEY_QUESTION_TEXT));
            String option1 = cursor.getString(cursor.getColumnIndex(KEY_OPTION1));
            String option2 = cursor.getString(cursor.getColumnIndex(KEY_OPTION2));
            String option3 = cursor.getString(cursor.getColumnIndex(KEY_OPTION3));
            String option4 = cursor.getString(cursor.getColumnIndex(KEY_OPTION4));
            String correctAnswer = cursor.getString(cursor.getColumnIndex(KEY_CORRECT_ANSWER));
            
            String[] options = {option1, option2, option3, option4};
            question = new Question(questionText, options, correctAnswer);
        }
        
        cursor.close();
        db.close();
        return question;
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
}
