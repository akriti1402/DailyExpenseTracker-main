package com.example.dailyexpensetracker.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyexpensetracker.R;
import com.example.dailyexpensetracker.database.DBHelper;

public class ExpenseSummaryActivity extends AppCompatActivity {

    private TextView totalExpensesTextView, totalCountTextView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_summary);

        totalExpensesTextView = findViewById(R.id.text_total_expense);
        totalCountTextView = findViewById(R.id.text_expense_count);

        dbHelper = new DBHelper(this);
        loadSummary();
    }

    private void loadSummary() {
        double totalAmount = dbHelper.getTotalExpenses();
        int totalCount = dbHelper.getExpensesCount();

        totalExpensesTextView.setText("₹ " + totalAmount);
        totalCountTextView.setText(String.valueOf(totalCount));
    }
}

