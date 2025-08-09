package com.example.dailyexpensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dailyexpensetracker.R;
import com.example.dailyexpensetracker.adapters.ExpenseAdapter;
import com.example.dailyexpensetracker.database.DBHelper;
import com.example.dailyexpensetracker.databinding.ActivityMainBinding;
import com.example.dailyexpensetracker.models.Expense;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DBHelper dbHelper;
    private ExpenseAdapter adapter;
    private final List<Expense> expenseList = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);
        setupRecyclerView();
        setupFAB();
        setupFilterButtons();
        setupCardClicks();
        loadExpenses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(this, expenseList, dbHelper);
        binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.expenseRecyclerView.setAdapter(adapter);
    }

    private void setupFAB() {
        binding.fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class))
        );
    }

    private void setupFilterButtons() {
        binding.btnFilterToday.setOnClickListener(v -> loadFilteredExpenses("today"));
        binding.btnFilterWeek.setOnClickListener(v -> loadFilteredExpenses("week"));
        binding.btnFilterMonth.setOnClickListener(v -> loadFilteredExpenses("month"));
    }

    private void setupCardClicks() {
        binding.cardToday.setOnClickListener(v -> openSummaryDetail("today"));
        binding.cardWeek.setOnClickListener(v -> openSummaryDetail("week"));
        binding.cardMonth.setOnClickListener(v -> openSummaryDetail("month"));
    }

    private void loadExpenses() {
        try {
            List<Expense> expenses = dbHelper.getAllExpenses();
            expenseList.clear();
            expenseList.addAll(expenses);

            double total = calculateTotal(expenses);
            updateUI(expenses.isEmpty(), total);

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            showErrorSnackbar("Failed to load expenses");
        }
    }

    private void loadFilteredExpenses(String filterType) {
        try {
            List<Expense> all = dbHelper.getAllExpenses();
            List<Expense> filtered = new ArrayList<>();

            for (Expense e : all) {
                long expenseTime = e.getDate().getTime();  // get Date as long

                if (filterType.equals("today") && isToday(expenseTime)) {
                    filtered.add(e);
                } else if (filterType.equals("week") && isThisWeek(expenseTime)) {
                    filtered.add(e);
                } else if (filterType.equals("month") && isThisMonth(expenseTime)) {
                    filtered.add(e);
                }
            }

            expenseList.clear();
            expenseList.addAll(filtered);

            double total = calculateTotal(filtered);
            updateUI(filtered.isEmpty(), total);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            showErrorSnackbar("Failed to filter expenses");
        }
    }
    private boolean isToday(long millis) {
        Calendar expenseCal = Calendar.getInstance();
        expenseCal.setTimeInMillis(millis);

        Calendar today = Calendar.getInstance();

        return expenseCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                expenseCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isThisWeek(long millis) {
        Calendar expenseCal = Calendar.getInstance();
        expenseCal.setTimeInMillis(millis);

        Calendar now = Calendar.getInstance();
        return expenseCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                expenseCal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR);
    }

    private boolean isThisMonth(long millis) {
        Calendar expenseCal = Calendar.getInstance();
        expenseCal.setTimeInMillis(millis);

        Calendar now = Calendar.getInstance();
        return expenseCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                expenseCal.get(Calendar.MONTH) == now.get(Calendar.MONTH);
    }

    private double calculateTotal(List<Expense> expenses) {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    private void updateUI(boolean isEmpty, double total) {
        binding.totalAmountTextView.setText(getString(R.string.total_amount, currencyFormat.format(total)));

        if (isEmpty) {
            binding.emptyStateView.setVisibility(View.VISIBLE);
            binding.expenseRecyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyStateView.setVisibility(View.GONE);
            binding.expenseRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openSummaryDetail(String filterType) {
        Intent intent = new Intent(MainActivity.this, SummaryDetailActivity.class);
        intent.putExtra("FILTER_TYPE", filterType);
        startActivity(intent);
    }

    private void showErrorSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
