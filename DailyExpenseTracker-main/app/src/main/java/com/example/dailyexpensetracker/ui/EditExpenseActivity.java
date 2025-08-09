package com.example.dailyexpensetracker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyexpensetracker.R;
import com.example.dailyexpensetracker.database.DBHelper;
import com.example.dailyexpensetracker.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText editAmount, editCategory, editNote;
    private Button updateButton;
    private DBHelper dbHelper;
    private Expense currentExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        editAmount = findViewById(R.id.edit_amount);
        editCategory = findViewById(R.id.edit_category);
        editNote = findViewById(R.id.edit_note);
        updateButton = findViewById(R.id.button_update);

        dbHelper = new DBHelper(this);

        int expenseId = getIntent().getIntExtra("expense_id", -1);
        currentExpense = dbHelper.getExpense(expenseId);

        if (currentExpense != null) {
            editAmount.setText(String.valueOf(currentExpense.getAmount()));
            editCategory.setText(currentExpense.getCategory());
            editNote.setText(currentExpense.getNote());
        }

        updateButton.setOnClickListener(v -> {
            String category = editCategory.getText().toString();
            String note = editNote.getText().toString();
            double amount = Double.parseDouble(editAmount.getText().toString());
            Date date = new Date(); // current date, or you can use a picker

            Expense updatedExpense = new Expense(
                    currentExpense.getId(), amount, category, note, date
            );

            int result = dbHelper.updateExpense(updatedExpense);
            if (result > 0) {
                Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
