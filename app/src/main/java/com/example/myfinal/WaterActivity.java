package com.example.myfinal;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        TextView tvRecommend = findViewById(R.id.tvRecommend);

        String tempStr = getIntent().getStringExtra("temp");
        String humidityStr = getIntent().getStringExtra("humidity");

        double temp = tempStr != null ? Double.parseDouble(tempStr) : 25;
        double humidity = humidityStr != null ? Double.parseDouble(humidityStr) : 50;

        double baseWater = 1500;
        double tempWater = 50 * (temp - 20);
        double humidityWater = 10 * (50 - humidity);

        double totalWaterMl = baseWater + tempWater + humidityWater;
        if (totalWaterMl < 1500) totalWaterMl = 1500;

        double bottleCount = totalWaterMl / 550.0;

        String recommendText = String.format(
                "根据今日气温 %.1f℃ 和湿度 %.1f%%，\n建议今日饮水约 %.0f 毫升，\n约等于 %.1f 瓶农夫山泉。",
                temp, humidity, totalWaterMl, bottleCount
        );

        tvRecommend.setText(recommendText);
    }
}
