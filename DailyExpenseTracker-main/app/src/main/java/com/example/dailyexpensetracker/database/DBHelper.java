package com.example.dailyexpensetracker.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dailyexpensetracker.models.Expense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE " + TABLE_EXPENSES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_CATEGORY + " TEXT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_AMOUNT + " REAL NOT NULL,"
                    + COLUMN_DATE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        }
    }

    public long addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_NOTE, expense.getNote());
        values.put(COLUMN_AMOUNT, expense.getAmount());

        String dateString = DATE_FORMAT.format(expense.getDate());
        values.put(COLUMN_DATE, dateString);

        long id = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return id;
    }

    public Expense getExpense(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Expense expense = null;

        Cursor cursor = db.query(TABLE_EXPENSES,
                new String[]{COLUMN_ID, COLUMN_CATEGORY, COLUMN_NOTE, COLUMN_AMOUNT, COLUMN_DATE},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                Date date = DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                expense = new Expense(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                        date
                );
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return expense;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    Date date = DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    Expense expense = new Expense(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                            date
                    );
                    expenses.add(expense);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }

    public int getExpensesCount() {
        String countQuery = "SELECT * FROM " + TABLE_EXPENSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_NOTE, expense.getNote());
        values.put(COLUMN_AMOUNT, expense.getAmount());

        String dateString = DATE_FORMAT.format(expense.getDate());
        values.put(COLUMN_DATE, dateString);

        int rowsAffected = db.update(TABLE_EXPENSES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(expense.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteExpense(Expense expense) {
        deleteExpense(expense.getId());
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES, null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // ✅ NEW METHODS FOR FILTERING

    public List<Expense> getTodayExpenses() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE date LIKE '" + today + "%' ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        return getFilteredExpenses(query);
    }

    public List<Expense> getThisWeekExpenses() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        String startOfWeek = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        String endOfWeek = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE date BETWEEN '" + startOfWeek + "' AND '" + endOfWeek + "' ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        return getFilteredExpenses(query);
    }

    public List<Expense> getThisMonthExpenses() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startOfMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endOfMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE date BETWEEN '" + startOfMonth + "' AND '" + endOfMonth + "' ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        return getFilteredExpenses(query);
    }

    // ✅ Helper method to reuse parsing and mapping
    private List<Expense> getFilteredExpenses(String query) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    Date date = DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    Expense expense = new Expense(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                            date
                    );
                    expenses.add(expense);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenses;
    }
}

