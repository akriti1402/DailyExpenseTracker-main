package com.example.dailyexpensetracker.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Expense {
    private int id;
    private double amount;
    private String category;
    private String note;
    private Date date;

    // Empty constructor
    public Expense() {}

    // Constructor with all fields including id
    public Expense(int id, double amount, String category, String note, Date date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.date = date;
    }

    // Constructor without id (for new expenses)
    public Expense(double amount, String category, String note, Date date) {
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.date = date;
    }

    // Constructor that accepts date as String
    public Expense(double amount, String category, String note, String dateString) {
        this.amount = amount;
        this.category = category;
        this.note = note;
        setDate(dateString);
    }

    // Copy constructor
    public Expense(Expense other) {
        this.id = other.id;
        this.amount = other.amount;
        this.category = other.category;
        this.note = other.note;
        this.date = other.date != null ? new Date(other.date.getTime()) : null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public Date getDate() {
        return date;
    }

    // Get formatted date string
    public String getFormattedDate() {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Set date from string
    public void setDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            this.date = sdf.parse(dateString);
        } catch (Exception e) {
            this.date = new Date(); // Current date as fallback
        }
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", note='" + note + '\'' +
                ", date=" + getFormattedDate() +
                '}';
    }

    // ✅ NEWLY ADDED METHODS

    // Check if the expense is from today
    public boolean isToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        return getFormattedDate().equals(today);
    }

    // Check if the expense is from the current week
    public boolean isThisWeek() {
        Calendar expenseCalendar = Calendar.getInstance();
        expenseCalendar.setTime(date);

        Calendar now = Calendar.getInstance();
        int expenseWeek = expenseCalendar.get(Calendar.WEEK_OF_YEAR);
        int expenseYear = expenseCalendar.get(Calendar.YEAR);

        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);

        return expenseWeek == currentWeek && expenseYear == currentYear;
    }

    // Check if the expense is from the current month
    public boolean isThisMonth() {
        Calendar expenseCalendar = Calendar.getInstance();
        expenseCalendar.setTime(date);

        Calendar now = Calendar.getInstance();
        int expenseMonth = expenseCalendar.get(Calendar.MONTH);
        int expenseYear = expenseCalendar.get(Calendar.YEAR);

        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);

        return expenseMonth == currentMonth && expenseYear == currentYear;
    }

    // Get date as String for database (yyyy-MM-dd)
    public String getDateStringForDatabase() {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
}
