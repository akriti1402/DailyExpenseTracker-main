package com.example.dailyexpensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyexpensetracker.R;
import com.example.dailyexpensetracker.database.DBHelper;
import com.example.dailyexpensetracker.databinding.ActivityAddExpenseBinding;
import com.example.dailyexpensetracker.models.Expense;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;
    private DBHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        setupViews();
    }

    private void setupViews() {
        setupCategoryDropdown();
        setupDatePicker();
        setCurrentDate();

        binding.buttonSave.setOnClickListener(v -> saveExpense());
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_item,
                getResources().getStringArray(R.array.expense_categories)
        );
        binding.autoCompleteCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.editTextDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void setCurrentDate() {
        updateDateText();
    }

    private void updateDateText() {
        binding.editTextDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void saveExpense() {
        String category = binding.autoCompleteCategory.getText().toString().trim();
        String amountStr = binding.editTextExpenseAmount.getText().toString().trim();
        String note = binding.editTextNote.getText().toString().trim();
        String dateStr = binding.editTextDate.getText().toString().trim();

        if (validateInputs(category, amountStr, dateStr)) {
            try {
                double amount = Double.parseDouble(amountStr);

                // Convert the date string to Date object
                Date date = dateFormat.parse(dateStr);

                // Create expense with Date object
                Expense expense = new Expense(0, amount, category, note, date);

                long id = dbHelper.addExpense(expense);
                if (id != -1) {
                    showSuccess();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showError("Failed to save expense");
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount format");
            } catch (Exception e) {
                showError("Invalid date format");
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs(String category, String amount, String date) {
        boolean isValid = true;

        if (category.isEmpty()) {
            binding.autoCompleteCategory.setError("Category required");
            isValid = false;
        }

        if (amount.isEmpty()) {
            binding.editTextExpenseAmount.setError("Amount required");
            isValid = false;
        }

        if (date.isEmpty()) {
            binding.editTextDate.setError("Date required");
            isValid = false;
        }

        return isValid;
    }

    private void showSuccess() {
        Snackbar.make(binding.getRoot(), "Expense saved successfully", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.success_green))
                .show();
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.error_red))
                .show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}