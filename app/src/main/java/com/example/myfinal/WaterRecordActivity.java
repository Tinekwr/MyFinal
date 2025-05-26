package com.example.myfinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterRecordActivity extends AppCompatActivity {

    private static final String PREF_NAME = "WaterPrefs";
    private static final String KEY_DATE = "record_date";
    private static final String KEY_AMOUNT = "record_amount";

    private int target;
    private int currentAmount = 0;
    private TextView tvDrank, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_record);

        target = getIntent().getIntExtra("target", 1500); // 默认1500ml

        tvDrank = findViewById(R.id.tvDrank);
        tvStatus = findViewById(R.id.tvStatus);
        Button btnAdd = findViewById(R.id.btnAddWater);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String today = getToday();
        String savedDate = prefs.getString(KEY_DATE, "");
        currentAmount = savedDate.equals(today) ? prefs.getInt(KEY_AMOUNT, 0) : 0;

        updateDisplay();

        btnAdd.setOnClickListener(v -> {
            currentAmount += 50;
            prefs.edit()
                    .putString(KEY_DATE, today)
                    .putInt(KEY_AMOUNT, currentAmount)
                    .apply();
            updateDisplay();
        });


    }

    private void updateDisplay() {
        tvDrank.setText("今日已喝水：" + currentAmount + "ml");
        if (currentAmount >= target) {
            tvStatus.setText("🎉 今日已达标！");
        } else {
            tvStatus.setText("");
        }
    }

    private String getToday() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }
}
