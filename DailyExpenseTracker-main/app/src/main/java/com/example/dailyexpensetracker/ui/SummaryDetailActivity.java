package com.example.dailyexpensetracker.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyexpensetracker.R;

public class SummaryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FILTER_TYPE = "FILTER_TYPE";

    private TextView summaryTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_detail);

        summaryTitleTextView = findViewById(R.id.summaryTitleTextView);

        // Get the filter type passed from MainActivity
        String filterType = getIntent().getStringExtra(EXTRA_FILTER_TYPE);

        if (filterType == null) {
            filterType = "today"; // Default filter
        }

        // Set the title based on filter type
        switch (filterType) {
            case "today":
                summaryTitleTextView.setText("Today's Expense Summary");
                break;
            case "week":
                summaryTitleTextView.setText("This Week's Expense Summary");
                break;
            case "month":
                summaryTitleTextView.setText("This Month's Expense Summary");
                break;
            default:
                summaryTitleTextView.setText("Expense Summary");
                break;
        }

        // TODO: Load and display detailed summary for the selected filterType
        // You can query your DBHelper here for expenses filtered by filterType
    }
}
