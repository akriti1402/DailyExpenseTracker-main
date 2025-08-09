package com.example.dailyexpensetracker.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Intent;
import com.example.dailyexpensetracker.ui.EditExpenseActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyexpensetracker.R;
import com.example.dailyexpensetracker.database.DBHelper;
import com.example.dailyexpensetracker.databinding.ItemExpenseBinding;
import com.example.dailyexpensetracker.models.Expense;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private final Context context;
    private final DBHelper dbHelper;
    private final NumberFormat currencyFormat;
    private final SimpleDateFormat dateFormat;

    public ExpenseAdapter(Context context, List<Expense> expenseList, DBHelper dbHelper) {
        this.context = context;
        this.expenseList = expenseList;
        this.dbHelper = dbHelper;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    public void updateData(List<Expense> newExpenses) {
        this.expenseList = newExpenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);

        // 🟢 Edit on single tap
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditExpenseActivity.class);
            intent.putExtra("expense_id", expense.getId());
            context.startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {
            showUndoSnackbar(holder, position, expense);
            return true;
        });
    }


    private void showUndoSnackbar(ViewHolder holder, int position, Expense expense) {
        Expense deletedExpense = new Expense(expense); // Using copy constructor
        int deletedPosition = position;

        // Remove item temporarily
        expenseList.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(holder.itemView, context.getString(R.string.expense_deleted), Snackbar.LENGTH_LONG)
                .setAction(context.getString(R.string.undo), v -> {
                    // Undo deletion
                    expenseList.add(deletedPosition, deletedExpense);
                    notifyItemInserted(deletedPosition);
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            // Permanent deletion
                            dbHelper.deleteExpense(deletedExpense.getId());
                        }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return expenseList != null ? expenseList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemExpenseBinding binding;

        public ViewHolder(ItemExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Expense expense) {
            if (expense == null) {
                showEmptyState();
                return;
            }

            try {
                binding.tvCategory.setText(expense.getCategory() != null ?
                        expense.getCategory() : "No Category");

                binding.tvNote.setText(expense.getNote() != null ?
                        expense.getNote() : "");

                binding.tvAmount.setText(formatCurrency(expense.getAmount()));
                binding.tvDate.setText(formatDate(expense.getDate()));
            } catch (Exception e) {
                showEmptyState();
            }
        }

        private String formatCurrency(double amount) {
            try {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                return format.format(amount);
            } catch (Exception e) {
                return "₹0.00";
            }
        }

        private String formatDate(Date date) {
            if (date == null) return "No Date";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return sdf.format(date);
            } catch (Exception e) {
                return "Invalid Date";
            }
        }

        private void showEmptyState() {
            binding.tvCategory.setText("Error");
            binding.tvNote.setText("");
            binding.tvAmount.setText("₹0.00");
            binding.tvDate.setText("N/A");
        }
    }

}
