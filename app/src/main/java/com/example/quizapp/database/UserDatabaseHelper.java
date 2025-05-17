package com.example.quizapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quizapp.model.User;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "UserDatabaseHelper";
    private static final String DATABASE_NAME = "user_db.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";

    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOTAL_SCORE = "total_score";
    private static final String KEY_QUIZZES_COMPLETED = "quizzes_completed";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_USERNAME + " TEXT UNIQUE,"
                    + KEY_PASSWORD + " TEXT,"
                    + KEY_EMAIL + " TEXT,"
                    + KEY_TOTAL_SCORE + " INTEGER DEFAULT 0,"
                    + KEY_QUIZZES_COMPLETED + " INTEGER DEFAULT 0"
                    + ")";
            db.execSQL(CREATE_USERS_TABLE);
            Log.d(TAG, "Users table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating users table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, user.getUsername());
            values.put(KEY_PASSWORD, user.getPassword());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_TOTAL_SCORE, user.getTotalScore());
            values.put(KEY_QUIZZES_COMPLETED, user.getQuizzesCompleted());

            id = db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding user: " + e.getMessage());
        } finally {
            db.close();
        }
        return id;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        try {
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID},
                    KEY_USERNAME + "=?", new String[]{username},
                    null, null, null);
            exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking username: " + e.getMessage());
        } finally {
            db.close();
        }
        return exists;
    }

    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID, KEY_USERNAME, KEY_EMAIL, KEY_TOTAL_SCORE, KEY_QUIZZES_COMPLETED},
                    KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?", new String[]{username, password},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
                user.setTotalScore(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_SCORE)));
                user.setQuizzesCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZZES_COMPLETED)));
                user.setPassword(""); 
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error authenticating user: " + e.getMessage());
        } finally {
            db.close();
        }
        return user;
    }

    public boolean updateUserScore(int userId, int additionalScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;

        try {
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_TOTAL_SCORE, KEY_QUIZZES_COMPLETED},
                    KEY_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int currentScore = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_SCORE));
                int quizzesCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZZES_COMPLETED));
                cursor.close();

                ContentValues values = new ContentValues();
                values.put(KEY_TOTAL_SCORE, currentScore + additionalScore);
                values.put(KEY_QUIZZES_COMPLETED, quizzesCompleted + 1);

                int rowsAffected = db.update(TABLE_USERS, values, KEY_ID + "=?", new String[]{String.valueOf(userId)});
                success = rowsAffected > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating user score: " + e.getMessage());
        } finally {
            db.close();
        }
        return success;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID, KEY_USERNAME, KEY_EMAIL, KEY_TOTAL_SCORE, KEY_QUIZZES_COMPLETED},
                    KEY_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
                user.setTotalScore(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_SCORE)));
                user.setQuizzesCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUIZZES_COMPLETED)));
                user.setPassword(""); 
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID: " + e.getMessage());
        } finally {
            db.close();
        }
        return user;
    }
}
